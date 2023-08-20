package uk.co.bbr.services.people.sql.dto;

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
        this.eventName = this.getString(columnList, 0);
        this.eventDate = this.getLocalDate(columnList, 1);
        this.eventDateResolution = this.getString(columnList, 2);
        this.contestSlug = this.getString(columnList, 3);
        this.bandCompetedAs = this.getString(columnList, 4);
        this.bandSlug = this.getString(columnList, 5);
        this.bandName = this.getString(columnList, 6);
        this.bandRegionName = this.getString(columnList, 7);
        this.bandRegionSlug = this.getString(columnList, 8);
        this.bandRegionCode = this.getString(columnList, 9);
        this.resultPosition = this.getInteger(columnList, 10);
        this.resultPositionType = this.getString(columnList, 11);
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
