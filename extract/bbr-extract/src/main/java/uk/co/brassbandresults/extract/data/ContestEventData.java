package uk.co.brassbandresults.extract.data;

import lombok.Getter;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

@Getter
public class ContestEventData {
    private final String contestSlug;
    private final String contestName;
    private final String contestNotes;
    private final String groupSlug;
    private final String groupName;
    private final String groupNotes;
    private final String regionSlug;
    private final String regionName;
    private final String regionCountryCode;
    private final String sectionName;
    private final String sectionSlug;
    private final String venueName;
    private final String venueSlug;
    private final String eventName;
    private final Date eventDate;
    private final String eventDateResolution;
    private final String eventNotes;
    private final boolean eventNoContestTookPlace;
    private final String contestTypeName;
    private final String contestTypeSlug;
    private final String eventId;
    private final String drawOneTitle;
    private final String drawTwoTitle;
    private final String drawThreeTitle;
    private final String pointsTotalTitle;
    private final String pointsOneTitle;
    private final String pointsTwoTitle;
    private final String pointsThreeTitle;
    private final String pointsFourTitle;
    private final String pointsFiveTitle;
    private final String pointsPenaltyTitle;
    private final boolean hasTestPiece;
    private final boolean hasOwnChoice;
    private final boolean hasEntertainments;

    // c.slug, c.name, c.notes,
    // g.slug, g.name, g.notes,
    // r.slug, r.name, r.country_code,
    // s.name, s.slug,
    // v.name, v.slug,
    // e.name, e.date_of_event, e.date_resolution, e.notes, e.no_contest,
    // t.name, t.slug
    // e.id
    // t.draw_one_title, t.draw_two_title, t.draw_three_title, t.points_total_title, t.points_one_title, t.points_two_title, t.points_three_title, t.points_four_title, t.points_five_title, t.points_penalty_title,
    // t.has_test_piece, t.has_own_choice, t.has_entertainments
    public ContestEventData(ResultSet resultSet) throws SQLException {
        this.contestSlug = resultSet.getString(1);
        this.contestName = resultSet.getString(2);
        this.contestNotes = resultSet.getString(3);
        this.groupSlug = resultSet.getString(4);
        this.groupName = resultSet.getString(5);
        this.groupNotes = resultSet.getString(6);
        this.regionSlug = resultSet.getString(7);
        this.regionName = resultSet.getString(8);
        this.regionCountryCode = resultSet.getString(9);
        this.sectionName = resultSet.getString(10);
        this.sectionSlug = resultSet.getString(11);
        this.venueName = resultSet.getString(12);
        this.venueSlug = resultSet.getString(13);
        this.eventName = resultSet.getString(14);
        this.eventDate = resultSet.getDate(15);
        this.eventDateResolution = resultSet.getString(16);
        this.eventNotes = resultSet.getString(17);
        this.eventNoContestTookPlace = resultSet.getBoolean(18);
        this.contestTypeName = resultSet.getString(19);
        this.contestTypeSlug = resultSet.getString(20);
        this.eventId = resultSet.getString(21);
        this.drawOneTitle = resultSet.getString(22);
        this.drawTwoTitle = resultSet.getString(23);
        this.drawThreeTitle = resultSet.getString(24);
        this.pointsTotalTitle = resultSet.getString(25);
        this.pointsOneTitle = resultSet.getString(26);
        this.pointsTwoTitle = resultSet.getString(27);
        this.pointsThreeTitle = resultSet.getString(28);
        this.pointsFourTitle = resultSet.getString(29);
        this.pointsFiveTitle = resultSet.getString(30);
        this.pointsPenaltyTitle = resultSet.getString(31);
        this.hasTestPiece = resultSet.getBoolean(32);
        this.hasOwnChoice = resultSet.getBoolean(33);
        this.hasEntertainments = resultSet.getBoolean(34);
    }

}
