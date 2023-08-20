package uk.co.bbr.services.performances.sql.dto;

import lombok.Getter;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.events.dao.ContestEventDao;
import uk.co.bbr.services.events.dao.ContestResultDao;
import uk.co.bbr.services.events.types.ContestEventDateResolution;
import uk.co.bbr.services.events.types.ResultPositionType;
import uk.co.bbr.services.framework.sql.AbstractSqlDto;
import uk.co.bbr.services.regions.dao.RegionDao;

import java.time.LocalDate;

@Getter
public class PiecePerformanceSqlDto extends AbstractSqlDto {

    private final Long resultId;
    private final String contestName;
    private final String contestSlug;
    private final LocalDate eventDate;
    private final String eventDateResolution;
    private final String competedAs;
    private final String bandSlug;
    private final String bandName;
    private final String regionSlug;
    private final String regionName;
    private final String countryCode;
    private final Integer resultPosition;
    private final String resultPositionType;

    public PiecePerformanceSqlDto(Object[] columnList) {
        this.resultId = this.getLong(columnList, 0);
        this.contestName = this.getString(columnList, 1);
        this.contestSlug = this.getString(columnList, 2);
        this.eventDate = this.getLocalDate(columnList, 3);
        this.eventDateResolution = this.getString(columnList, 4);
        this.competedAs = this.getString(columnList, 5);
        this.bandName = this.getString(columnList, 6);
        this.bandSlug = this.getString(columnList, 7);
        this.regionName = this.getString(columnList, 8);
        this.regionSlug = this.getString(columnList, 9);
        this.countryCode = this.getString(columnList, 10);
        this.resultPosition = this.getInteger(columnList, 11);
        this.resultPositionType = this.getString(columnList, 12);
    }

    public ContestResultDao asResult() {
        ContestResultDao returnResult = new ContestResultDao();
        returnResult.setId(this.resultId);
        returnResult.setPosition(String.valueOf(this.resultPosition));
        returnResult.setResultPositionType(ResultPositionType.fromCode(this.resultPositionType));
        returnResult.setBandName(this.competedAs);
        returnResult.setBand(new BandDao());
        returnResult.getBand().setName(this.bandName);
        returnResult.getBand().setSlug(this.bandSlug);
        if (this.regionSlug != null) {
            returnResult.getBand().setRegion(new RegionDao());
            returnResult.getBand().getRegion().setSlug(this.regionSlug);
            returnResult.getBand().getRegion().setName(this.regionName);
            returnResult.getBand().getRegion().setCountryCode(this.countryCode);
        }
        returnResult.setContestEvent(new ContestEventDao());
        returnResult.getContestEvent().setEventDate(this.eventDate);
        returnResult.getContestEvent().setEventDateResolution(ContestEventDateResolution.fromCode(this.eventDateResolution));
        returnResult.getContestEvent().setContest(new ContestDao());
        returnResult.getContestEvent().getContest().setName(this.contestName);
        returnResult.getContestEvent().getContest().setSlug(this.contestSlug);

        return returnResult;
    }
}
