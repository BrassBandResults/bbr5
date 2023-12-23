package uk.co.brassbandresults.extract.data;

import lombok.Getter;

import java.sql.ResultSet;
import java.sql.SQLException;

@Getter
public class ContestResultData {

    private final String bandName;
    private final String bandSlug;
    private final String competedAs;
    private final String resultPositionType;
    private final String resultPosition;
    private final String draw;
    private final String drawSecondPart;
    private final String drawThirdPart;
    private final String pointsTotal;
    private final String pointsFirstPart;
    private final String pointsSecondPart;
    private final String pointsThirdPart;
    private final String pointsFourthPart;
    private final String pointsFifthPart;
    private final String pointsPenalty;
    private final String conductor1Slug;
    private final String conductor1FirstNames;
    private final String conductor1Surname;
    private final String conductor2Slug;
    private final String conductor2FirstNames;
    private final String conductor2Surname;
    private final String conductor3Slug;
    private final String conductor3FirstNames;
    private final String conductor3Surname;
    private final String notes;
    private final String bandRegionSlug;
    private final String bandRegionName;
    private final String bandRegionCountryCode;
    private final String resultId;

    //b.name, b.slug, r.band_name, r.result_position_type, r.result_position,
    //r.draw, r.draw_second, r.draw_third,
    //r.points_total, r.points_first, r.points_second, r_points_third, r.points_fourth, r.points_fifth, r.points_penalty,
    //con1.slug as c1_slug, con1.first_names as c1_first_names, con1.surname as c1_surname,
    //con2.slug as c2_slug, con2.first_names as c2_first_names, con2.surname as c2_surname,
    //con3.slug as c3_slug, con3.first_names as c3_first_names, con3.surname as c3_surname,
    //r.notes,
    //reg.slug, reg.name, reg.country_code
    public ContestResultData(ResultSet resultSet) throws SQLException {
        this.bandName = resultSet.getString(1);
        this.bandSlug = resultSet.getString(2);
        this.competedAs = resultSet.getString(3);
        this.resultPositionType = resultSet.getString(4);
        this.resultPosition = resultSet.getString(5);
        this.draw = resultSet.getString(6);
        this.drawSecondPart = resultSet.getString(7);
        this.drawThirdPart = resultSet.getString(8);
        this.pointsTotal = resultSet.getString(9);
        this.pointsFirstPart = resultSet.getString(10);
        this.pointsSecondPart = resultSet.getString(11);
        this.pointsThirdPart = resultSet.getString(12);
        this.pointsFourthPart = resultSet.getString(13);
        this.pointsFifthPart = resultSet.getString(14);
        this.pointsPenalty = resultSet.getString(15);
        this.conductor1Slug = resultSet.getString(16);
        this.conductor1FirstNames = resultSet.getString(17);
        this.conductor1Surname = resultSet.getString(18);
        this.conductor2Slug = resultSet.getString(19);
        this.conductor2FirstNames = resultSet.getString(20);
        this.conductor2Surname = resultSet.getString(21);
        this.conductor3Slug = resultSet.getString(22);
        this.conductor3FirstNames = resultSet.getString(23);
        this.conductor3Surname = resultSet.getString(24);
        this.notes = resultSet.getString(25);
        this.bandRegionSlug = resultSet.getString(26);
        this.bandRegionName = resultSet.getString(27);
        this.bandRegionCountryCode = resultSet.getString(28);
        this.resultId = resultSet.getString(29);
    }
}
