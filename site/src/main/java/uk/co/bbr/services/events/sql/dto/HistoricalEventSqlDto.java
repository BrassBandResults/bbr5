package uk.co.bbr.services.events.sql.dto;

import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.events.dao.ContestEventDao;
import uk.co.bbr.services.events.dao.ContestResultDao;
import uk.co.bbr.services.events.types.ContestEventDateResolution;
import uk.co.bbr.services.framework.sql.AbstractSqlDto;
import uk.co.bbr.services.regions.dao.RegionDao;

import java.time.LocalDate;
import java.util.ArrayList;

public class HistoricalEventSqlDto extends AbstractSqlDto {

    private final LocalDate eventDate;
    private final String contestSlug;
    private final String bandName;
    private final String bandSlug;
    private final String eventName;
    private final String bandCompetedAs;
    private final String regionName;
    private final String regionSlug;
    private final String regionCountryCode;


    public HistoricalEventSqlDto(Object[] columnList) {

        // e.name, c.slug, e.date_of_event, r.band_name, b.slug, b.name, reg.name, reg.slug, reg.country_code

        this.eventName = this.getString(columnList, 0);
        this.contestSlug  = this.getString(columnList, 1);
        this.eventDate = this.getLocalDate(columnList, 2);
        this.bandCompetedAs = this.getString(columnList, 3);
        this.bandSlug = this.getString(columnList, 4);
        this.bandName = this.getString(columnList, 5);
        this.regionName = this.getString(columnList, 6);
        this.regionSlug = this.getString(columnList, 7);
        this.regionCountryCode = this.getString(columnList, 8);
    }

    public ContestResultDao toResult() {
        ContestResultDao result = new ContestResultDao();
        result.setContestEvent(new ContestEventDao());
        result.getContestEvent().setContest(new ContestDao());
        result.setPieces(new ArrayList<>());

        result.getContestEvent().setName(this.eventName);
        result.getContestEvent().setEventDate(this.eventDate);
        result.getContestEvent().setEventDateResolution(ContestEventDateResolution.EXACT_DATE);
        result.getContestEvent().getContest().setSlug(this.contestSlug);
        result.getContestEvent().getContest().setName(this.eventName);

        result.setBandName(this.bandCompetedAs);
        result.setBand(new BandDao());
        result.getBand().setName(this.bandName);
        result.getBand().setSlug(this.bandSlug);
        if (this.regionSlug != null) {
            result.getBand().setRegion(new RegionDao());
            result.getBand().getRegion().setName(this.regionName);
            result.getBand().getRegion().setSlug(this.regionSlug);
            result.getBand().getRegion().setCountryCode(this.regionCountryCode);
        }

        return result;
    }
}
