package uk.co.bbr.web.contests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import uk.co.bbr.services.bands.BandService;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.contests.ContestEventService;
import uk.co.bbr.services.contests.ContestGroupService;
import uk.co.bbr.services.contests.ContestResultService;
import uk.co.bbr.services.contests.ContestService;
import uk.co.bbr.services.contests.ContestTagService;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.contests.dao.ContestEventDao;
import uk.co.bbr.services.contests.dao.ContestGroupDao;
import uk.co.bbr.services.contests.dao.ContestTagDao;
import uk.co.bbr.services.contests.types.ContestEventDateResolution;
import uk.co.bbr.services.people.PersonService;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.regions.RegionService;
import uk.co.bbr.services.regions.dao.RegionDao;
import uk.co.bbr.services.security.JwtService;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.ex.AuthenticationFailedException;
import uk.co.bbr.web.LoginMixin;
import uk.co.bbr.web.security.support.TestUser;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest(properties = { "spring.config.name=contest-event-web-tests-admin-h2", "spring.datasource.url=jdbc:h2:mem:contest-event-web-tests-admin-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE", "spring.jpa.database-platform=org.hibernate.dialect.SQLServerDialect"},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ContestEventWebTests implements LoginMixin {

    @Autowired private SecurityService securityService;
    @Autowired private JwtService jwtService;
    @Autowired private ContestService contestService;
    @Autowired private RegionService regionService;
    @Autowired private BandService bandService;
    @Autowired private PersonService personService;
    @Autowired private ContestEventService contestEventService;
    @Autowired private ContestResultService contestResultService;
    @Autowired private RestTemplate restTemplate;
    @LocalServerPort private int port;

    @BeforeAll
    void setupContests() throws AuthenticationFailedException {
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        RegionDao yorkshire = this.regionService.fetchBySlug("yorkshire").get();

        BandDao rtb = this.bandService.create("Rothwell Temperance Band", yorkshire);
        BandDao blackDyke = this.bandService.create("Black Dyke", yorkshire);
        BandDao grimethorpe = this.bandService.create("Grimethorpe", yorkshire);
        BandDao ybs = this.bandService.create("YBS Band", yorkshire);

        PersonDao davidRoberts = this.personService.create("Roberts", "David");
        PersonDao johnRoberts = this.personService.create("Roberts", "John");
        PersonDao duncanBeckley = this.personService.create("Beckley", "Duncan");
        PersonDao davidChilds = this.personService.create("Childs", "David");

        ContestDao yorkshireArea = this.contestService.create("Yorkshire Area");
        ContestEventDao yorkshireArea2010 = this.contestEventService.create(yorkshireArea, LocalDate.of(2010, 3, 1));
        this.contestResultService.addResult(yorkshireArea2010, "1", blackDyke, davidRoberts);
        this.contestResultService.addResult(yorkshireArea2010, "2", rtb, johnRoberts);
        this.contestResultService.addResult(yorkshireArea2010, "3", grimethorpe, duncanBeckley);

        logoutTestUser();
    }

    @Test
    void testGetContestEventWorksSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/contests/yorkshire-area/2010-03-01", String.class);
        assertNotNull(response);
        assertTrue(response.contains("<title>Yorkshire Area - 01 Mar 2010 - Brass Band Results</title>"));
        assertTrue(response.contains(">Yorkshire Area<"));
        assertTrue(response.contains("<h3>Mon 01 Mar 2010</h3>"));

        assertTrue(response.contains("<span>1</span>"));
        assertTrue(response.contains("<span>2</span>"));
        assertTrue(response.contains("<span>3</span>"));
        assertFalse(response.contains("<span>4</span>"));

        assertTrue(response.contains(">Rothwell Temperance Band<"));
        assertTrue(response.contains(">Black Dyke<"));
        assertTrue(response.contains(">Grimethorpe<"));
        assertFalse(response.contains(">YBS Band<"));

        assertTrue(response.contains(">David Roberts<"));
        assertTrue(response.contains(">John Roberts<"));
        assertTrue(response.contains(">Duncan Beckley<"));
        assertFalse(response.contains(">David Childs<"));
    }


    @Test
    void testGetContestEventWithIncorrectSlugReturns404() {
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () -> this.restTemplate.getForObject("http://localhost:" + this.port + "/contests/not-a-real-contest/2010-03-01", String.class));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }



}
