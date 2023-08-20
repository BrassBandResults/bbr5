package uk.co.bbr.services.bands.sql.dto;

import lombok.Getter;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.events.dao.ContestEventDao;
import uk.co.bbr.services.events.types.ContestEventDateResolution;
import uk.co.bbr.services.framework.sql.AbstractSqlDto;
import uk.co.bbr.services.people.dao.PersonDao;

import java.time.LocalDate;

@Getter
public class CompareBandsSqlDto extends AbstractSqlDto {

    private final Integer leftResult;
    private final String leftResultType;
    private final Integer rightResult;
    private final String rightResultType;
    private final LocalDate eventDate;
    private final String contestSlug;
    private final String contestName;
    private final String eventDateResolution;
    private final String leftBandName;
    private final String rightBandName;
    private final String leftConductorFirstNames;
    private final String leftConductorSurname;
    private final String leftConductorKnownFor;
    private final String leftConductorSlug;
    private final String rightConductorFirstNames;
    private final String rightConductorSurname;
    private final String rightConductorKnownFor;
    private final String rightConductorSlug;

    public CompareBandsSqlDto(Object[] columnList) {
        this.leftResult = this.getInteger(columnList,0);
        this.leftResultType = this.getString(columnList, 1);
        this.rightResult = this.getInteger(columnList,2);
        this.rightResultType = this.getString(columnList, 3);
        this.eventDate = this.getLocalDate(columnList, 4);
        this.contestSlug = this.getString(columnList, 5);
        this.contestName = this.getString(columnList, 6);
        this.eventDateResolution = this.getString(columnList, 7);
        this.leftBandName = this.getString(columnList, 8);
        this.rightBandName = this.getString(columnList, 9);
        this.leftConductorFirstNames = this.getString(columnList, 10);
        this.leftConductorSurname = this.getString(columnList, 11);
        this.leftConductorKnownFor = this.getString(columnList, 12);
        this.leftConductorSlug = this.getString(columnList, 13);
        this.rightConductorFirstNames = this.getString(columnList, 14);
        this.rightConductorSurname = this.getString(columnList, 15);
        this.rightConductorKnownFor = this.getString(columnList, 16);
        this.rightConductorSlug = this.getString(columnList, 17);
    }

    public ContestEventDao getEvent() {
        ContestDao contest = new ContestDao();
        contest.setSlug(this.contestSlug);
        contest.setName(this.contestName);

        ContestEventDao event = new ContestEventDao();
        event.setEventDate(this.eventDate);
        event.setEventDateResolution(ContestEventDateResolution.fromCode(this.eventDateResolution));
        event.setContest(contest);

        return event;
    }

    public PersonDao getLeftConductor() {
        if (this.leftConductorSlug == null || this.leftConductorSlug.strip().length() == 0) {
            return null;
        }

        PersonDao person = new PersonDao();
        person.setSurname(this.leftConductorSurname);
        person.setFirstNames(this.leftConductorFirstNames);
        person.setSlug(this.leftConductorSlug);
        person.setKnownFor(this.leftConductorKnownFor);
        return person;
    }

    public PersonDao getRightConductor() {
        if (this.rightConductorSlug == null || this.rightConductorSlug.strip().length() == 0) {
            return null;
        }

        PersonDao person = new PersonDao();
        person.setSurname(this.rightConductorSurname);
        person.setFirstNames(this.rightConductorFirstNames);
        person.setSlug(this.rightConductorSlug);
        person.setKnownFor(this.rightConductorKnownFor);

        return person;
    }

    public String getLeftClass() {
        if (this.leftResult != null && this.rightResult != null && this.leftResult < this.rightResult) {
            return "bg-warning-subtle";
        }
        return "";
    }

    public String getRightClass() {
        if (this.rightResult != null && this.leftResult != null && this.rightResult < this.leftResult) {
            return "bg-warning-subtle";
        }
        return "";
    }

    public Integer getLeftResult() {
        if (this.leftResult == null) {
            return 0;
        }
        return this.leftResult;
    }

    public Integer getRightResult() {
        if (this.rightResult == null) {
            return 0;
        }
        return this.rightResult;
    }
}
