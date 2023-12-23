package uk.co.brassbandresults.extract.data;

import lombok.Getter;

import java.sql.ResultSet;
import java.sql.SQLException;

@Getter
public class AdjudicatorData {
    private final String slug;
    private final String firstNames;
    private final String surname;

    public AdjudicatorData(ResultSet resultSet) throws SQLException {
        this.slug = resultSet.getString(1);
        this.firstNames = resultSet.getString(2);
        this.surname = resultSet.getString(3);
    }
}
