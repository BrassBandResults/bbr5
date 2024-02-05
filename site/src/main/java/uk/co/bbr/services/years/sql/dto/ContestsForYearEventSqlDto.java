package uk.co.bbr.services.years.sql.dto;

import lombok.Getter;
import uk.co.bbr.services.framework.sql.AbstractSqlDto;

import java.time.LocalDate;

@Getter
public class ContestsForYearEventSqlDto extends AbstractSqlDto {
    private final LocalDate eventDate;
    private final String eventDateResolution;
    private final String contestSlug;
    private final String contestName;
    private final String bandCompetedAs;
    private final String bandSlug;
    private final String bandName;
    private final String bandRegionCountryCode;
    private final String conductor1Slug;
    private final String conductor1FirstNames;
    private final String conductor1Surname;
    private final String conductor2Slug;
    private final String conductor2FirstNames;
    private final String conductor2Surname;
    private final String conductor3Slug;
    private final String conductor3FirstNames;
    private final String conductor3Surname;
    private final Boolean noContest;

    public ContestsForYearEventSqlDto(Object[] columnList) {
        this.eventDate = this.getLocalDate(columnList,0);
        this.eventDateResolution = this.getString(columnList, 1);
        this.contestSlug = this.getString(columnList, 2);
        this.contestName = this.getString(columnList, 3);
        this.noContest = this.getBoolean(columnList,4);
        this.bandCompetedAs = this.getString(columnList, 5);
        this.bandSlug = this.getString(columnList, 6);
        this.bandName = this.getString(columnList, 7);
        this.bandRegionCountryCode = this.getString(columnList, 8);
        this.conductor1Slug = this.getString(columnList, 9);
        this.conductor1FirstNames = this.getString(columnList, 10);
        this.conductor1Surname = this.getString(columnList, 11);
        this.conductor2Slug = this.getString(columnList, 12);
        this.conductor2FirstNames = this.getString(columnList, 13);
        this.conductor2Surname = this.getString(columnList, 14);
        this.conductor3Slug = this.getString(columnList, 15);
        this.conductor3FirstNames = this.getString(columnList, 16);
        this.conductor3Surname = this.getString(columnList, 17);
    }
}
