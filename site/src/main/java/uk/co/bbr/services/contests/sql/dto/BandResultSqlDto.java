package uk.co.bbr.services.contests.sql.dto;

import lombok.Getter;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.events.dao.ContestEventDao;
import uk.co.bbr.services.events.dao.ContestResultDao;
import uk.co.bbr.services.events.types.ContestEventDateResolution;
import uk.co.bbr.services.events.types.ResultAwardType;
import uk.co.bbr.services.events.types.ResultPositionType;
import uk.co.bbr.services.framework.sql.AbstractSqlDto;
import uk.co.bbr.services.groups.dao.ContestGroupDao;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.sections.dao.SectionDao;

import java.time.LocalDate;

@Getter
public class BandResultSqlDto extends AbstractSqlDto {

    private final Long contestResultId;
    private final LocalDate eventDate;
    private final String eventDateResolution;
    private final String contestSlug;
    private final String contestName;
    private final Integer resultPosition;
    private final String resultPositionType;
    private final String resultAward;
    private final String bandName;
    private final Integer draw;
    private final Long contestEventId;
    private final String groupSlug;
    private final String groupName;
    private final String conductor1Slug;
    private final String conductor1FirstNames;
    private final String conductor1Surname;
    private final String conductor2Slug;
    private final String conductor2FirstNames;
    private final String conductor2Surname;
    private final String conductor3Slug;
    private final String conductor3FirstNames;
    private final String conductor3Surname;
    private final Long contestSectionId;
    private final String resultNotes;

    public BandResultSqlDto(Object[] columnList) {
        this.contestResultId = this.getLong(columnList,0);
        this.eventDate = this.getLocalDate(columnList, 1);
        this.eventDateResolution = this.getString(columnList, 2);
        this.contestSlug = this.getString(columnList, 3);
        this.contestName = this.getString(columnList, 4);
        this.resultPosition =this.getInteger(columnList,5);
        this.resultPositionType = this.getString(columnList, 6);
        this.resultAward = this.getString(columnList, 7);
        this.bandName = this.getString(columnList, 8);
        this.draw = this.getInteger(columnList,9);
        this.contestEventId = this.getLong(columnList,10);
        this.groupSlug = this.getString(columnList, 11);
        this.groupName = this.getString(columnList, 12);
        this.conductor1Slug = this.getString(columnList, 13);
        this.conductor1FirstNames = this.getString(columnList, 14);
        this.conductor1Surname = this.getString(columnList, 15);
        this.conductor2Slug = this.getString(columnList, 16);
        this.conductor2FirstNames = this.getString(columnList, 17);
        this.conductor2Surname = this.getString(columnList, 18);
        this.conductor3Slug = this.getString(columnList, 19);
        this.conductor3FirstNames = this.getString(columnList, 20);
        this.conductor3Surname = this.getString(columnList, 21);
        this.contestSectionId = this.getLong(columnList, 22);
        this.resultNotes = this.getString(columnList, 23);
    }

    public ContestResultDao toContestResultDao() {
        ContestResultDao eachResult = new ContestResultDao();
        eachResult.setContestEvent(new ContestEventDao());
        eachResult.getContestEvent().setContest(new ContestDao());

        eachResult.setId(this.getContestResultId().longValue());
        eachResult.getContestEvent().setId(this.getContestEventId().longValue());

        eachResult.getContestEvent().setEventDate(this.getEventDate());
        eachResult.getContestEvent().setEventDateResolution(ContestEventDateResolution.fromCode(this.getEventDateResolution()));
        eachResult.getContestEvent().getContest().setSlug(this.getContestSlug());
        eachResult.getContestEvent().getContest().setName(this.getContestName());
        if (this.contestSectionId != null) {
            eachResult.getContestEvent().getContest().setSection(new SectionDao());
            eachResult.getContestEvent().getContest().getSection().setId(this.contestSectionId);
        }

        if (this.getResultPosition() != null) {
            eachResult.setPosition(this.getResultPosition().toString());
        }
        eachResult.setResultPositionType(ResultPositionType.fromCode(this.getResultPositionType()));
        eachResult.setResultAward(ResultAwardType.fromCode(this.resultAward));
        eachResult.setBandName(this.getBandName());
        eachResult.setDraw(this.getDraw());
        eachResult.setNotes(this.getResultNotes());

        if (this.getConductor1Slug() != null) {
            eachResult.setConductor(new PersonDao());
            eachResult.getConductor().setSlug(this.getConductor1Slug());
            eachResult.getConductor().setFirstNames(this.getConductor1FirstNames());
            eachResult.getConductor().setSurname(this.getConductor1Surname());
        }

        if (this.getConductor2Slug() != null) {
            eachResult.setConductorSecond(new PersonDao());
            eachResult.getConductorSecond().setSlug(this.getConductor2Slug());
            eachResult.getConductorSecond().setFirstNames(this.getConductor2FirstNames());
            eachResult.getConductorSecond().setSurname(this.getConductor2Surname());
        }

        if (this.getConductor3Slug() != null) {
            eachResult.setConductorThird(new PersonDao());
            eachResult.getConductorThird().setSlug(this.getConductor3Slug());
            eachResult.getConductorThird().setFirstNames(this.getConductor3FirstNames());
            eachResult.getConductorThird().setSurname(this.getConductor3Surname());
        }

        if (this.getGroupSlug() != null) {
            eachResult.getContestEvent().getContest().setContestGroup(new ContestGroupDao());
            eachResult.getContestEvent().getContest().getContestGroup().setName(this.getGroupName());
            eachResult.getContestEvent().getContest().getContestGroup().setSlug(this.getGroupSlug());
        }

        return eachResult;
    }
}
