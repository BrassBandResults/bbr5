package uk.co.brassbandresults.extract.data;

import lombok.Getter;

import java.sql.ResultSet;
import java.sql.SQLException;

@Getter
public class PieceData {
    private final String name;
    private final String slug;
    private final String year;
    private final String composerSlug;
    private final String composerFirstNames;
    private final String composerSurname;
    private final String arrangerSlug;
    private final String arrangerFirstNames;
    private final String arrangerSurname;
    private final String notes;
    private final String andOr;
    private final String resultId;
    private final String ordering;

    //p.name, p.slug, p.piece_year,
    //c.slug, c.first_names, c.surname,
    //a.slug. a.first_names, a.surname,
    //p.notes, p.and_or
    public PieceData(ResultSet resultSet) throws SQLException {
        this.name = resultSet.getString(1);
        this.slug = resultSet.getString(2);
        this.year = resultSet.getString(3);
        this.composerSlug = resultSet.getString(4);
        this.composerFirstNames = resultSet.getString(5);
        this.composerSurname = resultSet.getString(6);
        this.arrangerSlug = resultSet.getString(7);
        this.arrangerFirstNames = resultSet.getString(8);
        this.arrangerSurname = resultSet.getString(9);
        this.notes = resultSet.getString(10);
        this.andOr = resultSet.getString(11);
        this.resultId = resultSet.getString(12);
        this.ordering = resultSet.getString(13);
    }
}
