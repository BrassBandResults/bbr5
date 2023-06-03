package uk.co.bbr.services.bands.sql.dto;

import lombok.Getter;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.contests.dao.ContestEventDao;
import uk.co.bbr.services.contests.types.ContestEventDateResolution;
import uk.co.bbr.services.framework.sql.AbstractSqlDto;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.regions.dao.RegionDao;

import java.sql.Date;
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
        this.leftResult = (Integer)columnList[0];
        this.leftResultType = (String)columnList[1];
        this.rightResult = (Integer)columnList[2];
        this.rightResultType = (String)columnList[3];
        Date tempEventDate = (Date)columnList[4];
        this.eventDate = tempEventDate.toLocalDate();
        this.contestSlug = (String)columnList[5];
        this.contestName = (String)columnList[6];
        this.eventDateResolution = (String)columnList[7];
        this.leftBandName = (String)columnList[8];
        this.rightBandName = (String)columnList[9];
        this.leftConductorFirstNames = (String)columnList[10];
        this.leftConductorSurname = (String)columnList[11];
        this.leftConductorKnownFor = (String)columnList[12];
        this.leftConductorSlug = (String)columnList[13];
        this.rightConductorFirstNames = (String)columnList[14];
        this.rightConductorSurname = (String)columnList[15];
        this.rightConductorKnownFor = (String)columnList[16];
        this.rightConductorSlug = (String)columnList[17];
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
        if (this.leftConductorSlug == null || this.leftConductorSlug.trim().length() == 0) {
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
        if (this.rightConductorSlug == null || this.rightConductorSlug.trim().length() == 0) {
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
        if (this.leftResult < this.rightResult) {
            return "bg-warning-subtle";
        }
        return "";
    }

    public String getRightClass() {
        if (this.rightResult < this.leftResult) {
            return "bg-warning-subtle";
        }
        return "";
    }
}
