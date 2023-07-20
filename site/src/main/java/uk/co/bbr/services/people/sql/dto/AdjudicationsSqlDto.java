package uk.co.bbr.services.people.sql.dto;

import lombok.Getter;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.events.dao.ContestAdjudicatorDao;
import uk.co.bbr.services.events.dao.ContestEventDao;
import uk.co.bbr.services.events.types.ContestEventDateResolution;
import uk.co.bbr.services.framework.sql.AbstractSqlDto;
import uk.co.bbr.services.regions.dao.RegionDao;

import java.sql.Date;
import java.time.LocalDate;

@Getter
public class AdjudicationsSqlDto  extends AbstractSqlDto {
    private final String eventName;
    private final LocalDate eventDate;
    private final String eventDateResolution;
    private final String contestSlug;
    private final String winnerCompetedAs;
    private final String winnerSlug;
    private final String winnerName;
    private final String winnerRegionName;
    private final String winnerRegionSlug;
    private final String winnerRegionCode;

    public AdjudicationsSqlDto(Object[] columnList) {
        this.eventName = (String) columnList[0];
        Date tempEventDate = (Date) columnList[1];
        this.eventDate = tempEventDate.toLocalDate();
        this.eventDateResolution = (String) columnList[2];
        this.contestSlug = (String) columnList[3];
        this.winnerCompetedAs = (String) columnList[4];
        this.winnerSlug = (String) columnList[5];
        this.winnerName = (String) columnList[6];
        this.winnerRegionName = (String) columnList[7];
        this.winnerRegionSlug = (String) columnList[8];
        this.winnerRegionCode = (String) columnList[9];
    }

    public ContestAdjudicatorDao buildAdjudicationDao() {
        ContestAdjudicatorDao result = new ContestAdjudicatorDao();
        result.setContestEvent(new ContestEventDao());
        result.getContestEvent().setName(this.eventName);
        result.getContestEvent().setEventDate(this.eventDate);
        result.getContestEvent().setEventDateResolution(ContestEventDateResolution.fromCode(this.eventDateResolution));
        result.getContestEvent().setContest(new ContestDao());
        result.getContestEvent().getContest().setSlug(this.contestSlug);
        if (this.winnerSlug != null) {
            result.setWinner(new BandDao());
            result.getWinner().setName(this.winnerName);
            result.getWinner().setSlug(this.winnerSlug);
            result.getWinner().setRegion(new RegionDao());
            result.getWinner().getRegion().setName(this.winnerRegionName);
            result.getWinner().getRegion().setSlug(this.winnerRegionSlug);
            result.getWinner().getRegion().setCountryCode(this.winnerRegionCode);
        }
        return result;
    }
}
