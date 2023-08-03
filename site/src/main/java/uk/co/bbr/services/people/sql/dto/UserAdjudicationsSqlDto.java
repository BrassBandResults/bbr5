package uk.co.bbr.services.people.sql.dto;

import lombok.Getter;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.events.dao.ContestAdjudicatorDao;
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
public class UserAdjudicationsSqlDto extends AbstractSqlDto {
    private final String eventName;
    private final LocalDate eventDate;
    private final String eventDateResolution;
    private final String contestSlug;
    private final String bandCompetedAs;
    private final String bandSlug;
    private final String bandName;
    private final String bandRegionName;
    private final String bandRegionSlug;
    private final String bandRegionCode;
    private final Integer resultPosition;
    private final String resultPositionType;

    public UserAdjudicationsSqlDto(Object[] columnList) {
        this.eventName = (String) columnList[0];
        this.eventDate = this.getLocalDate(columnList, 1);
        this.eventDateResolution = (String) columnList[2];
        this.contestSlug = (String) columnList[3];
        this.bandCompetedAs = (String) columnList[4];
        this.bandSlug = (String) columnList[5];
        this.bandName = (String) columnList[6];
        this.bandRegionName = (String) columnList[7];
        this.bandRegionSlug = (String) columnList[8];
        this.bandRegionCode = (String) columnList[9];
        if (columnList[10] != null) {
            this.resultPosition = this.getInteger(columnList, 10);
        } else {
            this.resultPosition = null;
        }
        this.resultPositionType = (String)columnList[11];
    }

    public ContestResultDao buildContestResultDao() {
        ContestResultDao result = new ContestResultDao();
        result.setContestEvent(new ContestEventDao());
        result.getContestEvent().setName(this.eventName);
        result.getContestEvent().setEventDate(this.eventDate);
        result.getContestEvent().setEventDateResolution(ContestEventDateResolution.fromCode(this.eventDateResolution));
        result.getContestEvent().setContest(new ContestDao());
        result.getContestEvent().getContest().setSlug(this.contestSlug);

        result.setPosition(String.valueOf(this.resultPosition));
        result.setResultPositionType(ResultPositionType.fromCode(this.resultPositionType));

        result.setBandName(this.bandCompetedAs);
        result.setBand(new BandDao());
        result.getBand().setSlug(this.bandSlug);
        result.getBand().setName(this.bandName);
        result.getBand().setSlug(this.bandSlug);
        result.getBand().setRegion(new RegionDao());
        result.getBand().getRegion().setName(this.bandRegionName);
        result.getBand().getRegion().setSlug(this.bandRegionSlug);
        result.getBand().getRegion().setCountryCode(this.bandRegionCode);
        return result;
    }
}
