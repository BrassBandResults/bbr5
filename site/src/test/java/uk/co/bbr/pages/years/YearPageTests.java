package uk.co.bbr.pages.years;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;
import uk.co.bbr.services.bands.BandService;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.contests.ContestEventService;
import uk.co.bbr.services.contests.ContestResultService;
import uk.co.bbr.services.contests.ContestService;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.contests.dao.ContestEventDao;
import uk.co.bbr.services.contests.dao.ContestResultDao;
import uk.co.bbr.services.contests.types.ContestEventDateResolution;
import uk.co.bbr.services.people.PersonService;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.regions.RegionService;
import uk.co.bbr.services.regions.dao.RegionDao;
import uk.co.bbr.services.security.JwtService;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.ex.AuthenticationFailedException;
import uk.co.bbr.services.years.YearService;
import uk.co.bbr.services.years.sql.dto.YearListEntrySqlDto;
import uk.co.bbr.web.LoginMixin;
import uk.co.bbr.web.security.support.TestUser;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest(properties = { "spring.config.name=year-page-tests-admin-h2", "spring.datasource.url=jdbc:h2:mem:year-page-tests-admin-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE", "spring.jpa.database-platform=org.hibernate.dialect.SQLServerDialect"},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class YearPageTests implements LoginMixin {

    @Autowired private SecurityService securityService;
    @Autowired private JwtService jwtService;
    @Autowired private RegionService regionService;
    @Autowired private BandService bandService;
    @Autowired private ContestService contestService;
    @Autowired private PersonService personService;
    @Autowired private ContestEventService contestEventService;
    @Autowired private ContestResultService contestResultService;
    @Autowired private YearService yearService;

    @BeforeAll
    void setupData() throws AuthenticationFailedException {
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        RegionDao yorkshire = this.regionService.fetchBySlug("yorkshire").get();

        BandDao rtb = this.bandService.create("Rothwell Temperance Band", yorkshire);
        BandDao notRtb = this.bandService.create("Not RTB", yorkshire);

        PersonDao davidRoberts = this.personService.create("Roberts", "David");
        PersonDao johnRoberts = this.personService.create("Roberts", "John");
        PersonDao duncanBeckley = this.personService.create("Beckley", "Duncan");

        ContestDao yorkshireArea = this.contestService.create("Yorkshire Area");
        ContestDao midlandsArea = this.contestService.create("Midlands Area");

        ContestEventDao yorkshireArea2000 = this.contestEventService.create(yorkshireArea, LocalDate.of(2000, 03, 01));
        yorkshireArea2000.setEventDateResolution(ContestEventDateResolution.MONTH_AND_YEAR);
        yorkshireArea2000 = this.contestEventService.update(yorkshireArea2000);
        ContestEventDao yorkshireArea2002 = this.contestEventService.create(yorkshireArea, LocalDate.of(2002, 03, 07));
        ContestEventDao yorkshireArea2001 = this.contestEventService.create(yorkshireArea, LocalDate.of(2001, 03, 05));
        ContestEventDao yorkshireArea2004 = this.contestEventService.create(yorkshireArea, LocalDate.of(2004, 03, 1));
        ContestEventDao yorkshireArea2003 = this.contestEventService.create(yorkshireArea, LocalDate.of(2003, 03, 10));
        ContestEventDao midlandsArea2004 = this.contestEventService.create(midlandsArea, LocalDate.of(2004, 03, 7));
        yorkshireArea2004.setEventDateResolution(ContestEventDateResolution.YEAR);
        yorkshireArea2004 = this.contestEventService.update(yorkshireArea2004);

        this.contestResultService.addResult(yorkshireArea2000, "1", rtb, davidRoberts);
        this.contestResultService.addResult(yorkshireArea2000, "2", notRtb, johnRoberts);

        this.contestResultService.addResult(yorkshireArea2001, "2", rtb, davidRoberts);
        this.contestResultService.addResult(yorkshireArea2001, "1", notRtb, johnRoberts);

        this.contestResultService.addResult(yorkshireArea2002, "5", rtb, johnRoberts);
        this.contestResultService.addResult(yorkshireArea2002, "2", notRtb, davidRoberts);

        this.contestResultService.addResult(yorkshireArea2003, "3", notRtb, davidRoberts);

        this.contestResultService.addResult(yorkshireArea2004, "1", rtb, davidRoberts);
        this.contestResultService.addResult(yorkshireArea2004, "5", notRtb, duncanBeckley);

        logoutTestUser();
    }

    @Test
    void testGetYearsPageReturnsSuccessfully() {
        List<YearListEntrySqlDto> years = this.yearService.fetchFullYearList();

        assertEquals(5, years.size());

        assertEquals(2004, years.get(0).getYear());
        assertEquals(2, years.get(0).getBandCount());
        assertEquals(1, years.get(0).getEventCount());
        assertEquals(2003, years.get(1).getYear());
        assertEquals(1, years.get(1).getBandCount());
        assertEquals(1, years.get(1).getEventCount());
        assertEquals(2002, years.get(2).getYear());
        assertEquals(2, years.get(2).getBandCount());
        assertEquals(1, years.get(2).getEventCount());
        assertEquals(2001, years.get(3).getYear());
        assertEquals(2, years.get(3).getBandCount());
        assertEquals(1, years.get(3).getEventCount());
        assertEquals(2000, years.get(4).getYear());
        assertEquals(2, years.get(4).getBandCount());
        assertEquals(1, years.get(4).getEventCount());
    }

    @Test
    void testGetPageForYearReturnsSuccessfully() {
        List<ContestResultDao> yearEvents = this.yearService.fetchEventsForYear("2004");

        assertEquals(2, yearEvents.size());

        assertEquals("Yorkshire Area", yearEvents.get(0).getContestEvent().getContest().getName());
        assertEquals("Rothwell Temperance Band", yearEvents.get(0).getBandName());

        assertEquals("Midlands Area", yearEvents.get(1).getContestEvent().getContest().getName());
        assertNull(yearEvents.get(1).getBandName());
    }
}

