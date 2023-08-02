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

import java.math.BigInteger;
import java.sql.Date;
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
        this.resultId = columnList[0] instanceof BigInteger ? ((BigInteger)columnList[0]).longValue() : (Integer)columnList[0];
        this.contestName = (String)columnList[1];
        this.contestSlug = (String)columnList[2];
        Date tempEventDate = (Date)columnList[3];
        this.eventDate = tempEventDate.toLocalDate();
        this.eventDateResolution = (String)columnList[4];
        this.competedAs = (String)columnList[5];
        this.bandName = (String)columnList[6];
        this.bandSlug = (String)columnList[7];
        this.regionName = (String)columnList[8];
        this.regionSlug = (String)columnList[9];
        this.countryCode = (String)columnList[10];
        this.resultPosition = columnList[11] instanceof BigInteger ? ((BigInteger)columnList[11]).intValue() : (Integer)columnList[11];
        this.resultPositionType = (String)columnList[12];
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
