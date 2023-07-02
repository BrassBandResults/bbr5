package uk.co.bbr.web.people;

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
import uk.co.bbr.services.events.ContestEventService;
import uk.co.bbr.services.events.ResultService;
import uk.co.bbr.services.contests.ContestService;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.events.dao.ContestEventDao;
import uk.co.bbr.services.events.dao.ContestResultDao;
import uk.co.bbr.services.groups.ContestGroupService;
import uk.co.bbr.services.groups.dao.ContestGroupDao;
import uk.co.bbr.services.people.PersonService;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.pieces.PieceService;
import uk.co.bbr.services.pieces.types.PieceCategory;
import uk.co.bbr.services.security.JwtService;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.ex.AuthenticationFailedException;
import uk.co.bbr.services.tags.ContestTagService;
import uk.co.bbr.services.tags.dao.ContestTagDao;
import uk.co.bbr.web.LoginMixin;
import uk.co.bbr.web.security.support.TestUser;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest(properties = {  "spring.config.location=classpath:test-application.yml",
        "spring.datasource.url=jdbc:h2:mem:people-details-web-tests-admin-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PeopleDetailsWebTests implements LoginMixin {

    @Autowired private SecurityService securityService;
    @Autowired private JwtService jwtService;
    @Autowired private PersonService personService;
    @Autowired private ContestService contestService;
    @Autowired private ContestGroupService contestGroupService;
    @Autowired private ContestTagService contestTagService;
    @Autowired private ContestEventService contestEventService;
    @Autowired private ResultService contestResultService;
    @Autowired private BandService bandService;
    @Autowired private PieceService pieceService;
    @Autowired private RestTemplate restTemplate;
    @LocalServerPort private int port;

    @BeforeAll
    void setupPeople() throws AuthenticationFailedException {
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        this.personService.create("Childs", "David");
        this.personService.create("Childs", "Nick");
        this.personService.create("Childs", "Bob");
        PersonDao davidRoberts = this.personService.create("Roberts", "David");

        BandDao rtb = this.bandService.create("Rothwell Temperance");

        ContestDao yorkshireArea = this.contestService.create("Yorkshire Area");
        ContestEventDao yorkshireArea2010 = this.contestEventService.create(yorkshireArea, LocalDate.of(2010, 3, 3));

        ContestDao broadoakWhitFriday = this.contestService.create("Broadoak (Whit Friday)");
        ContestEventDao broadoak2011 = this.contestEventService.create(broadoakWhitFriday, LocalDate.of(2011, 6, 7));

        ContestResultDao yorkshireAreaResult = new ContestResultDao();
        yorkshireAreaResult.setBand(rtb);
        yorkshireAreaResult.setBandName("Rothwell Temperance");
        yorkshireAreaResult.setConductor(davidRoberts);
        yorkshireAreaResult.setPosition("1");
        yorkshireAreaResult.setDraw(5);
        this.contestResultService.addResult(yorkshireArea2010, yorkshireAreaResult);

        ContestResultDao whitFridayResult = new ContestResultDao();
        whitFridayResult.setBand(rtb);
        whitFridayResult.setBandName("Rothwell Temps");
        whitFridayResult.setConductor(davidRoberts);
        whitFridayResult.setPosition("2");
        whitFridayResult.setDraw(4);
        this.contestResultService.addResult(broadoak2011, whitFridayResult);

        ContestGroupDao yorkshireGroup = this.contestGroupService.create("Yorkshire Group");
        yorkshireArea = this.contestService.addContestToGroup(yorkshireArea, yorkshireGroup);

        ContestTagDao yorkshireTag = this.contestTagService.create("Yorkshire Tag");
        this.contestService.addContestTag(yorkshireArea, yorkshireTag);

        ContestTagDao yorkshireGroupTag = this.contestTagService.create("Yorkshire Group Tag");
        this.contestGroupService.addGroupTag(yorkshireGroup, yorkshireGroupTag);

        this.pieceService.create("Bandance", PieceCategory.TEST_PIECE, davidRoberts);

        logoutTestUser();
    }

    @Test
    void testGetPersonDetailsPageWorksCorrectly() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/people/david-roberts", String.class);
        assertNotNull(response);
        assertTrue(response.contains("<title>David Roberts - Person - Brass Band Results</title>"));
        assertTrue(response.contains("<h2>David Roberts</h2>"));

        assertTrue(response.contains(">Yorkshire Area<"));
        assertTrue(response.contains(">03 Mar 2010<"));
        assertFalse(response.contains(">Broadoak (Whit Friday)<"));
        assertFalse(response.contains(">07 Jun 2011<"));
        assertFalse(response.contains(">Bandance<"));
    }

    @Test
    void testGetPersonDetailsPageFailsWithInvalidSlug() {
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () -> this.restTemplate.getForObject("http://localhost:" + this.port + "/people/not-a-real-person", String.class));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void testGetPersonWhitsPageWorksCorrectly() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/people/david-roberts/whits", String.class);
        assertNotNull(response);
        assertTrue(response.contains("<title>David Roberts - Person - Brass Band Results</title>"));
        assertTrue(response.contains("<h2>David Roberts</h2>"));

        assertFalse(response.contains(">Yorkshire Area<"));
        assertFalse(response.contains(">03 Mar 2010<"));
        assertTrue(response.contains(">Broadoak (Whit Friday)<"));
        assertTrue(response.contains(">07 Jun 2011<"));
        assertFalse(response.contains(">Bandance<"));
    }

    @Test
    void testGetPersonWhitsPageFailsWithInvalidSlug() {
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () -> this.restTemplate.getForObject("http://localhost:" + this.port + "/people/not-a-real-person/whits", String.class));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void testGetPersonPiecesPageWorksCorrectly() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/people/david-roberts/pieces", String.class);
        assertNotNull(response);
        assertTrue(response.contains("<title>David Roberts - Person - Brass Band Results</title>"));
        assertTrue(response.contains("<h2>David Roberts</h2>"));

        assertFalse(response.contains(">Yorkshire Area<"));
        assertFalse(response.contains(">03 Mar 2010<"));
        assertFalse(response.contains(">Broadoak (Whit Friday)<"));
        assertFalse(response.contains(">7 Jun 2011<"));
        assertTrue(response.contains(">Bandance<"));
    }

    @Test
    void testGetPersonPiecesPageFailsWithInvalidSlug() {
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () -> this.restTemplate.getForObject("http://localhost:" + this.port + "/people/not-a-real-person/pieces", String.class));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

}
