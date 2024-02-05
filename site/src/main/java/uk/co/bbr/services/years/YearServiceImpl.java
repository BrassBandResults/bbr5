package uk.co.bbr.services.years;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.events.dao.ContestEventDao;
import uk.co.bbr.services.events.dao.ContestResultDao;
import uk.co.bbr.services.events.types.ContestEventDateResolution;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.regions.dao.RegionDao;
import uk.co.bbr.services.years.sql.YearSql;
import uk.co.bbr.services.years.sql.dto.ContestsForYearEventSqlDto;
import uk.co.bbr.services.years.sql.dto.YearListEntrySqlDto;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class YearServiceImpl implements YearService {
    private final EntityManager entityManager;

    @Override
    public List<YearListEntrySqlDto> fetchFullYearList() {
        return YearSql.selectYearsPageData(this.entityManager);
    }

    @Override
    public List<ContestResultDao> fetchEventsForYear(String year) {
        List<ContestResultDao> returnData = new ArrayList<>();

        List<ContestsForYearEventSqlDto> thisYearData = YearSql.selectEventsForYear(this.entityManager, year);

        for (ContestsForYearEventSqlDto eachSqlEvent : thisYearData) {
            ContestResultDao eachWinner = new ContestResultDao();
            eachWinner.setContestEvent(new ContestEventDao());
            eachWinner.getContestEvent().setContest(new ContestDao());
            eachWinner.getContestEvent().setPieces(new ArrayList<>());
            eachWinner.setPieces(new ArrayList<>());

            eachWinner.getContestEvent().setEventDate(eachSqlEvent.getEventDate());
            eachWinner.getContestEvent().setEventDateResolution(ContestEventDateResolution.fromCode(eachSqlEvent.getEventDateResolution()));
            eachWinner.getContestEvent().getContest().setSlug(eachSqlEvent.getContestSlug());
            eachWinner.getContestEvent().getContest().setName(eachSqlEvent.getContestName());
            eachWinner.getContestEvent().setNoContest(eachSqlEvent.getNoContest());
            eachWinner.setBandName(eachSqlEvent.getBandCompetedAs());

            if (eachSqlEvent.getBandSlug() != null) {
                eachWinner.setBand(new BandDao());
                eachWinner.getBand().setSlug(eachSqlEvent.getBandSlug());
                eachWinner.getBand().setName(eachSqlEvent.getBandName());
                eachWinner.getBand().setRegion(new RegionDao());
                eachWinner.getBand().getRegion().setCountryCode(eachSqlEvent.getBandRegionCountryCode());
            }

            if (eachSqlEvent.getConductor1Slug() != null) {
                eachWinner.setConductor(new PersonDao());
                eachWinner.getConductor().setSlug(eachSqlEvent.getConductor1Slug());
                eachWinner.getConductor().setFirstNames(eachSqlEvent.getConductor1FirstNames());
                eachWinner.getConductor().setSurname(eachSqlEvent.getConductor1Surname());
            }

            if (eachSqlEvent.getConductor2Slug() != null) {
                eachWinner.setConductorSecond(new PersonDao());
                eachWinner.getConductorSecond().setSlug(eachSqlEvent.getConductor2Slug());
                eachWinner.getConductorSecond().setFirstNames(eachSqlEvent.getConductor2FirstNames());
                eachWinner.getConductorSecond().setSurname(eachSqlEvent.getConductor2Surname());
            }

            if (eachSqlEvent.getConductor3Slug() != null) {
                eachWinner.setConductorThird(new PersonDao());
                eachWinner.getConductorThird().setSlug(eachSqlEvent.getConductor3Slug());
                eachWinner.getConductorThird().setFirstNames(eachSqlEvent.getConductor3FirstNames());
                eachWinner.getConductorThird().setSurname(eachSqlEvent.getConductor3Surname());
            }
            returnData.add(eachWinner);
        }

        return returnData;
    }
}
