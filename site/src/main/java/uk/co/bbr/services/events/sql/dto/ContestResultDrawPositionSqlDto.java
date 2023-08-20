package uk.co.bbr.services.events.sql.dto;

import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.events.dao.ContestEventDao;
import uk.co.bbr.services.events.dao.ContestResultDao;
import uk.co.bbr.services.events.types.ContestEventDateResolution;
import uk.co.bbr.services.events.types.ResultPositionType;
import uk.co.bbr.services.framework.sql.AbstractSqlDto;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.regions.dao.RegionDao;

import java.time.LocalDate;

public class ContestResultDrawPositionSqlDto  extends AbstractSqlDto {

    private final LocalDate eventDate;
    private final String eventDateResolution;
    private final String contestSlug;
    private final Integer resultPosition;
    private final String resultPositionType;
    private final String bandName;
    private final String bandSlug;
    private final Integer draw;
    private final String conductor1Slug;
    private final String conductor1FirstNames;
    private final String conductor1Surname;
    private final String conductor2Slug;
    private final String conductor2FirstNames;
    private final String conductor2Surname;
    private final String conductor3Slug;
    private final String conductor3FirstNames;
    private final String conductor3Surname;
    private final String competedAs;
    private final String bandRegionName;
    private final String bandRegionSlug;
    private final String bandCountryCode;
    private final String pointsTotal;

    public ContestResultDrawPositionSqlDto(Object[] columnList) {
        this.eventDate = this.getLocalDate(columnList, 0);
        this.eventDateResolution = this.getString(columnList, 1);
        this.contestSlug = this.getString(columnList, 2);
        this.resultPosition = this.getInteger(columnList,3);
        this.resultPositionType = this.getString(columnList, 4);
        this.competedAs = this.getString(columnList, 5);
        this.bandName = this.getString(columnList, 6);
        this.bandSlug = this.getString(columnList, 7);
        this.bandRegionName = this.getString(columnList, 8);
        this.bandRegionSlug = this.getString(columnList, 9);
        this.bandCountryCode = this.getString(columnList, 10);
        this.draw = this.getInteger(columnList,11);
        this.pointsTotal = this.getString(columnList, 12);
        this.conductor1Slug = this.getString(columnList, 13);
        this.conductor1FirstNames = this.getString(columnList, 14);
        this.conductor1Surname = this.getString(columnList, 15);
        this.conductor2Slug = this.getString(columnList, 16);
        this.conductor2FirstNames = this.getString(columnList, 17);
        this.conductor2Surname = this.getString(columnList, 18);
        this.conductor3Slug = this.getString(columnList, 19);
        this.conductor3FirstNames = this.getString(columnList, 20);
        this.conductor3Surname = this.getString(columnList, 21);
    }

    public ContestResultDao getResult() {
        ContestResultDao result = new ContestResultDao();
        result.setContestEvent(new ContestEventDao());
        result.getContestEvent().setContest(new ContestDao());
        result.setBand(new BandDao());
        result.getBand().setRegion(new RegionDao());

        result.getContestEvent().setEventDate(this.eventDate);
        result.getContestEvent().setEventDateResolution(ContestEventDateResolution.fromCode(this.eventDateResolution));
        result.getContestEvent().getContest().setSlug(this.contestSlug);
        result.setPosition(String.valueOf(this.resultPosition));
        result.setResultPositionType(ResultPositionType.fromCode(this.resultPositionType));
        result.setBandName(this.competedAs);
        result.getBand().setName(this.bandName);
        result.getBand().setSlug(this.bandSlug);
        result.getBand().setRegion(new RegionDao());
        result.getBand().getRegion().setName(this.bandRegionName);
        result.getBand().getRegion().setSlug(this.bandRegionSlug);
        result.getBand().getRegion().setCountryCode(this.bandCountryCode);
        result.setDraw(this.draw);
        result.setPointsTotal(this.pointsTotal);

        if (this.conductor1Slug != null) {
            PersonDao conductor1 = new PersonDao();
            conductor1.setSurname(this.conductor1Surname);
            conductor1.setFirstNames(this.conductor1FirstNames);
            conductor1.setSlug(this.conductor1Slug);
            result.setConductor(conductor1);
        }

        if (this.conductor2Slug != null) {
            PersonDao conductor2 = new PersonDao();
            conductor2.setSurname(this.conductor2Surname);
            conductor2.setFirstNames(this.conductor2FirstNames);
            conductor2.setSlug(this.conductor2Slug);
            result.setConductorSecond(conductor2);
        }

        if (this.conductor3Slug != null) {
            PersonDao conductor3 = new PersonDao();
            conductor3.setSurname(this.conductor3Surname);
            conductor3.setFirstNames(this.conductor3FirstNames);
            conductor3.setSlug(this.conductor3Slug);
            result.setConductorThird(conductor3);
        }

        return result;
    }
}
