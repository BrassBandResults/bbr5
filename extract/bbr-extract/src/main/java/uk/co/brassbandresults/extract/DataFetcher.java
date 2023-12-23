package uk.co.brassbandresults.extract;

import uk.co.brassbandresults.extract.data.AdjudicatorData;
import uk.co.brassbandresults.extract.data.ContestEventData;
import uk.co.brassbandresults.extract.data.ContestResultData;
import uk.co.brassbandresults.extract.data.PieceData;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DataFetcher {

    private final String connectionUrl;
    private Connection dbConnection;

    public DataFetcher(String connectionUrl) {
        this.connectionUrl = connectionUrl;
    }

    public Connection getConnection() {
        return this.dbConnection;
    }

    public void connect() {
        try {
            System.out.print("Connecting to SQL Server ... ");
            this.dbConnection = DriverManager.getConnection(this.connectionUrl);
            System.out.println("...connected");
        } catch (Exception e) {
            System.out.println();
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            this.dbConnection.close();
        } catch (SQLException e) {
            // do nothing
        }
        this.dbConnection = null;
    }

    private static final String SELECT_CONTESTS_SINCE = """
        SELECT  c.slug, c.name, c.notes,
                g.slug, g.name, g.notes,
                r.slug, r.name, r.country_code,
                s.name, s.slug,
                v.name, v.slug,
                e.name, e.date_of_event, e.date_resolution, e.notes, e.no_contest,
                t.name, t.slug,
                e.id,
                t.draw_one_title, t.draw_two_title, t.draw_three_title, t.points_total_title, t.points_one_title, t.points_two_title, t.points_three_title, t.points_four_title, t.points_five_title, t.points_penalty_title,
                t.has_test_piece, t.has_own_choice, t.has_entertainments
        FROM contest_event e
        INNER JOIN contest c ON c.id = e.contest_id
        INNER JOIN contest_type t ON t.id = e.contest_type_id
        LEFT OUTER JOIN region r ON r.id = c.region_id
        LEFT OUTER JOIN section s ON s.id = c.section_id
        LEFT OUTER JOIN contest_group g ON g.id = c.contest_group_id
        LEFT OUTER JOIN venue v ON v.id = e.venue_id""";

    //    WHERE e.id IN (
    //      SELECT DISTINCT r.contest_event_id
    //      FROM contest_result r
    //      WHERE r.updated > CONVERT(datetime, '__UPDATED_SINCE__'))
    //    """;

    public List<ContestEventData> fetchContestsSince(int year, int month, int day) throws SQLException {
        String sql = SELECT_CONTESTS_SINCE.replace("__UPDATED_SINCE__", year + "-" + month + "-" + day);

        List<ContestEventData> contestEvents = new ArrayList<>();
        try (Statement statement = this.dbConnection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                ContestEventData contestEventData = new ContestEventData(resultSet);
                contestEvents.add(contestEventData);
            }
        }
        return contestEvents;
    }

    private static final String SELECT_RESULTS_FOR_CONTEST = """
        SELECT b.name, b.slug, r.band_name, r.result_position_type, r.result_position,
        r.draw, r.draw_second, r.draw_third,
        r.points_total, r.points_first, r.points_second, r.points_third, r.points_fourth, r.points_fifth, r.points_penalty,
        con1.slug as c1_slug, con1.first_names as c1_first_names, con1.surname as c1_surname,
        con2.slug as c2_slug, con2.first_names as c2_first_names, con2.surname as c2_surname,
        con3.slug as c3_slug, con3.first_names as c3_first_names, con3.surname as c3_surname,
        r.notes,
        reg.slug, reg.name, reg.country_code, r.id
        FROM contest_result r
        INNER JOIN band b ON b.id = r.band_id
        LEFT OUTER JOIN person con1 ON con1.id = r.conductor_id
        LEFT OUTER JOIN person con2 ON con2.id = r.conductor_two_id
        LEFT OUTER JOIN person con3 ON con3.id = r.conductor_three_id
        LEFT OUTER JOIN region reg ON reg.id = b.region_id
        WHERE r.contest_event_id = __CONTEST_EVENT_ID__
        """;

    public List<ContestResultData> fetchResultsFor(ContestEventData event) throws SQLException {
        String sql = SELECT_RESULTS_FOR_CONTEST.replace("__CONTEST_EVENT_ID__", event.getEventId());

        List<ContestResultData> results = new ArrayList<>();
        try (Statement statement = this.dbConnection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                ContestResultData resultData = new ContestResultData(resultSet);
                results.add(resultData);
            }
        }
        return results;
    }

    private static final String SELECT_ADJUDICATORS_FOR_CONTEST = """
        SELECT p.slug, p.first_names, p.surname
        FROM person p
        INNER JOIN contest_event_adjudicator a ON a.person_id = p.id
        WHERE a.contest_event_id = __CONTEST_EVENT_ID__
        """;

    public List<AdjudicatorData> fetchAdjudicatorsFor(ContestEventData event) throws SQLException {
        String sql = SELECT_ADJUDICATORS_FOR_CONTEST.replace("__CONTEST_EVENT_ID__", event.getEventId());

        List<AdjudicatorData> adjudicators = new ArrayList<>();
        try (Statement statement = this.dbConnection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                AdjudicatorData resultData = new AdjudicatorData(resultSet);
                adjudicators.add(resultData);
            }
        }
        return adjudicators;
    }

    private static final String SELECT_SET_TESTS_FOR_CONTEST = """
        SELECT p.name, p.slug, p.piece_year,
            c.slug, c.first_names, c.surname,
            a.slug, a.first_names, a.surname,
            p.notes, cetp.and_or, null, null
        FROM piece p
        INNER JOIN contest_event_test_piece cetp ON cetp.piece_id = p.id
        LEFT OUTER JOIN person c ON c.id = p.composer_id
        LEFT OUTER JOIN person a ON a.id = p.arranger_id
        WHERE cetp.contest_event_id = __CONTEST_EVENT_ID__
        """;
    public List<PieceData> fetchSetTestsFor(ContestEventData event) throws SQLException {
        String sql = SELECT_SET_TESTS_FOR_CONTEST.replace("__CONTEST_EVENT_ID__", event.getEventId());

        List<PieceData> pieces = new ArrayList<>();
        try (Statement statement = this.dbConnection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                PieceData resultData = new PieceData(resultSet);
                pieces.add(resultData);
            }
        }
        return pieces;
    }

    private static final String SELECT_OWN_CHOICE_FOR_CONTEST = """
        SELECT p.name, p.slug, p.piece_year,
            c.slug, c.first_names, c.surname,
            a.slug, a.first_names, a.surname,
            p.notes, null, r.id, crtp.ordering
        FROM piece p
        INNER JOIN contest_result_test_piece crtp ON crtp.piece_id = p.id
        INNER JOIN contest_result r ON r.id = crtp.contest_result_id
        LEFT OUTER JOIN person c ON c.id = p.composer_id
        LEFT OUTER JOIN person a ON a.id = p.arranger_id
        WHERE r.contest_event_id = __CONTEST_EVENT_ID__
        """;
    public List<PieceData> fetchOwnChoiceFor(ContestEventData event) throws SQLException {
        String sql = SELECT_OWN_CHOICE_FOR_CONTEST.replace("__CONTEST_EVENT_ID__", event.getEventId());

        List<PieceData> pieces = new ArrayList<>();
        try (Statement statement = this.dbConnection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                PieceData resultData = new PieceData(resultSet);
                pieces.add(resultData);
            }
        }
        return pieces;
    }
}
