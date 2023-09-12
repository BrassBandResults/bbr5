package uk.co.bbr.services.events.sql.dto;

import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.bands.types.BandStatus;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.events.dao.ContestEventDao;
import uk.co.bbr.services.events.dao.ContestResultDao;
import uk.co.bbr.services.events.types.ContestEventDateResolution;
import uk.co.bbr.services.events.types.ResultAwardType;
import uk.co.bbr.services.events.types.ResultPositionType;
import uk.co.bbr.services.framework.sql.AbstractSqlDto;
import uk.co.bbr.services.groups.dao.ContestGroupDao;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.regions.dao.RegionDao;
import uk.co.bbr.services.sections.dao.SectionDao;

import java.time.LocalDate;
import java.util.ArrayList;

public class EventResultSqlDto extends AbstractSqlDto {

    private final LocalDate eventDate;
    private final String eventDateResolution;
    private final String contestSlug;
    private final Integer resultPosition;
    private final String resultPositionType;
    private final String resultAward;
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
    private final Integer drawSecond;
    private final Integer drawThird;
    private final String pointsFirst;
    private final String pointsSecond;
    private final String pointsThird;
    private final String pointsFourth;
    private final String pointsFifth;
    private final String pointsPenalty;
    private final Long resultId;
    private final String notes;
    private final String contestName;
    private final String groupName;
    private final String groupSlug;
    private final String bandLatitude;
    private final String bandLongitude;
    private final Integer bandStatus;
    private final String sectionSlug;
    private final String sectionTranslationKey;
    private final String createdBy;


    public EventResultSqlDto(Object[] columnList) {
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
        this.drawSecond = this.getInteger(columnList,22);
        this.drawThird = this.getInteger(columnList,23);
        this.pointsFirst = this.getString(columnList, 24);
        this.pointsSecond = this.getString(columnList, 25);
        this.pointsThird = this.getString(columnList, 26);
        this.pointsFourth = this.getString(columnList, 27);
        this.pointsFifth = this.getString(columnList, 28);
        this.pointsPenalty = this.getString(columnList, 29);
        this.resultId = this.getLong(columnList,30);
        this.notes = this.getString(columnList, 31);
        this.contestName = this.getString(columnList, 32);
        this.groupName = this.getString(columnList, 33);
        this.groupSlug = this.getString(columnList, 34);
        this.bandLatitude = this.getString(columnList, 35);
        this.bandLongitude = this.getString(columnList, 36);
        this.bandStatus = this.getInteger(columnList, 37);
        this.sectionSlug = this.getString(columnList, 38);
        this.sectionTranslationKey = this.getString(columnList, 39);
        this.createdBy = this.getString(columnList, 40);
        this.resultAward = this.getString(columnList, 41);
    }

    public ContestResultDao toResult() {
        ContestResultDao result = new ContestResultDao();
        result.setContestEvent(new ContestEventDao());
        result.getContestEvent().setContest(new ContestDao());
        result.setPieces(new ArrayList<>());

        result.getContestEvent().setName(this.contestName);
        result.getContestEvent().setEventDate(this.eventDate);
        result.getContestEvent().setEventDateResolution(ContestEventDateResolution.fromCode(this.eventDateResolution));
        result.getContestEvent().getContest().setSlug(this.contestSlug);
        result.getContestEvent().getContest().setName(this.contestName);
        if (this.resultPositionType != null) {
            result.setId(this.resultId);
            result.setCreatedBy(this.createdBy);
            result.setPosition(String.valueOf(this.resultPosition));
            result.setResultPositionType(ResultPositionType.fromCode(this.resultPositionType));
            result.setBandName(this.competedAs);
            result.setDraw(this.draw);
            result.setDrawSecond(this.drawSecond);
            result.setDrawThird(this.drawThird);
            result.setPointsTotal(this.pointsTotal);
            result.setPointsFirst(this.pointsFirst);
            result.setPointsSecond(this.pointsSecond);
            result.setPointsThird(this.pointsThird);
            result.setPointsFourth(this.pointsFourth);
            result.setPointsFifth(this.pointsFifth);
            result.setPointsPenalty(this.pointsPenalty);
            result.setNotes(this.notes);
            result.setBand(new BandDao());
            result.getBand().setName(this.bandName);
            result.getBand().setSlug(this.bandSlug);
            result.getBand().setLatitude(this.bandLatitude);
            result.getBand().setLongitude(this.bandLongitude);
            result.getBand().setStatus(BandStatus.EXTINCT.fromCode(this.bandStatus));
            if (this.sectionSlug != null) {
                result.getBand().setSection(new SectionDao());
                result.getBand().getSection().setSlug(this.sectionSlug);
                result.getBand().getSection().setTranslationKey(this.sectionTranslationKey);
            }
            if (this.bandRegionSlug != null) {
                result.getBand().setRegion(new RegionDao());
                result.getBand().getRegion().setName(this.bandRegionName);
                result.getBand().getRegion().setSlug(this.bandRegionSlug);
                result.getBand().getRegion().setCountryCode(this.bandCountryCode);
            }
        }
        result.setResultAward(ResultAwardType.fromCode(this.resultAward));


        if (this.conductor1Slug != null) {
            PersonDao conductor1 = new PersonDao();
            conductor1.setSurname(this.conductor1Surname);
            conductor1.setFirstNames(this.conductor1FirstNames);
            conductor1.setSlug(this.conductor1Slug);
            result.setConductor(conductor1);
        } else {
            result.setOriginalConductorName("Unknown");
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

        if (this.groupSlug != null) {
            ContestGroupDao contestGroup = new ContestGroupDao();
            contestGroup.setSlug(this.groupSlug);
            contestGroup.setName(this.groupName);
            result.getContestEvent().getContest().setContestGroup(contestGroup);
        }

        return result;
    }
}
