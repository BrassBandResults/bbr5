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

import java.sql.Date;
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
        Date tempEventDate = (Date)columnList[0];
        this.eventDate = tempEventDate.toLocalDate();
        this.eventDateResolution = (String)columnList[1];
        this.contestSlug = (String)columnList[2];
        this.resultPosition = (Integer)columnList[3];
        this.resultPositionType = (String)columnList[4];
        this.competedAs = (String)columnList[5];
        this.bandName = (String)columnList[6];
        this.bandSlug = (String)columnList[7];
        this.bandRegionName = (String)columnList[8];
        this.bandRegionSlug = (String)columnList[9];
        this.bandCountryCode = (String)columnList[10];
        this.draw = (Integer)columnList[11];
        this.pointsTotal = (String)columnList[12];
        this.conductor1Slug = (String)columnList[13];
        this.conductor1FirstNames = (String)columnList[14];
        this.conductor1Surname = (String)columnList[15];
        this.conductor2Slug = (String)columnList[16];
        this.conductor2FirstNames = (String)columnList[17];
        this.conductor2Surname = (String)columnList[18];
        this.conductor3Slug = (String)columnList[19];
        this.conductor3FirstNames = (String)columnList[20];
        this.conductor3Surname = (String)columnList[21];
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
            result.setConductor(conductor2);
        }

        if (this.conductor3Slug != null) {
            PersonDao conductor3 = new PersonDao();
            conductor3.setSurname(this.conductor3Surname);
            conductor3.setFirstNames(this.conductor3FirstNames);
            conductor3.setSlug(this.conductor3Slug);
            result.setConductor(conductor3);
        }

        return result;
    }
}
