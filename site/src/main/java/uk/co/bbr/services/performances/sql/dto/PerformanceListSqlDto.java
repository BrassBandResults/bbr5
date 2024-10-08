package uk.co.bbr.services.performances.sql.dto;

import lombok.Getter;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.events.dao.ContestEventDao;
import uk.co.bbr.services.events.dao.ContestResultDao;
import uk.co.bbr.services.events.types.ContestEventDateResolution;
import uk.co.bbr.services.events.types.ResultPositionType;
import uk.co.bbr.services.framework.sql.AbstractSqlDto;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.performances.dao.PerformanceDao;
import uk.co.bbr.services.performances.types.Instrument;
import uk.co.bbr.services.regions.dao.RegionDao;

import java.time.LocalDate;

@Getter
public class PerformanceListSqlDto extends AbstractSqlDto {

    private final LocalDate eventDate;
    private final String dateResolution;
    private final String contestName;
    private final String contestSlug;
    private final String competedAs;
    private final String bandName;
    private final String bandSlug;
    private final String regionName;
    private final String regionSlug;
    private final String regionCountryCode;
    private final String conductorSurname;
    private final String conductorFirstNames;
    private final String conductorSlug;
    private final Integer resultPosition;
    private final String resultPositionType;
    private final Integer instrument;
    private final Long resultId;
    private final Long performanceId;

    public PerformanceListSqlDto(Object[] columnList) {
        this.eventDate = this.getLocalDate(columnList, 0);
        this.dateResolution = this.getString(columnList, 1);
        this.contestName = this.getString(columnList, 2);
        this.contestSlug = this.getString(columnList, 3);
        this.competedAs = this.getString(columnList, 4);
        this.bandName = this.getString(columnList, 5);
        this.bandSlug = this.getString(columnList, 6);
        this.regionName = this.getString(columnList, 7);
        this.regionSlug = this.getString(columnList, 8);
        this.regionCountryCode = this.getString(columnList, 9);
        this.conductorSurname = this.getString(columnList, 10);
        this.conductorFirstNames = this.getString(columnList, 11);
        this.conductorSlug = this.getString(columnList, 12);
        this.resultPosition = this.getInteger(columnList, 13);
        this.resultPositionType = this.getString(columnList, 14);
        this.instrument = this.getInteger(columnList, 15);
        this.resultId = this.getLong(columnList, 16);
        this.performanceId = this.getLong(columnList, 17);
    }

    public PerformanceDao asPerformance() {
        PerformanceDao returnDetails = new PerformanceDao();
        returnDetails.setId(this.performanceId);
        returnDetails.setInstrument(Instrument.fromCode(this.instrument));
        returnDetails.setResult(new ContestResultDao());

        returnDetails.getResult().setId(this.resultId);
        returnDetails.getResult().setPosition(String.valueOf(this.resultPosition));
        returnDetails.getResult().setResultPositionType(ResultPositionType.fromCode(this.resultPositionType));
        returnDetails.getResult().setBandName(this.competedAs);
        returnDetails.getResult().setBand(new BandDao());
        returnDetails.getResult().getBand().setName(this.bandName);
        returnDetails.getResult().getBand().setSlug(this.bandSlug);
        if (this.regionSlug != null) {
            returnDetails.getResult().getBand().setRegion(new RegionDao());
            returnDetails.getResult().getBand().getRegion().setSlug(this.regionSlug);
            returnDetails.getResult().getBand().getRegion().setName(this.regionName);
            returnDetails.getResult().getBand().getRegion().setCountryCode(this.regionCountryCode);
        }
        if (this.conductorSlug != null) {
            returnDetails.getResult().setConductor(new PersonDao());
            returnDetails.getResult().getConductor().setSlug(this.conductorSlug);
            returnDetails.getResult().getConductor().setSurname(this.conductorSurname);
            returnDetails.getResult().getConductor().setFirstNames(this.conductorFirstNames);
        }
        returnDetails.getResult().setContestEvent(new ContestEventDao());
        returnDetails.getResult().getContestEvent().setEventDate(this.eventDate);
        returnDetails.getResult().getContestEvent().setEventDateResolution(ContestEventDateResolution.fromCode(this.dateResolution));
        returnDetails.getResult().getContestEvent().setContest(new ContestDao());
        returnDetails.getResult().getContestEvent().setName(this.contestName);
        returnDetails.getResult().getContestEvent().getContest().setName(this.contestName);
        returnDetails.getResult().getContestEvent().getContest().setSlug(this.contestSlug);

        return returnDetails;
    }

}
