package uk.co.bbr.web.people;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import uk.co.bbr.services.bands.BandService;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.events.ContestEventService;
import uk.co.bbr.services.events.ContestResultService;
import uk.co.bbr.services.contests.ContestService;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.events.dao.ContestEventDao;
import uk.co.bbr.services.events.dao.ContestResultDao;
import uk.co.bbr.services.people.PersonService;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.pieces.PieceService;
import uk.co.bbr.services.pieces.types.PieceCategory;
import uk.co.bbr.services.security.JwtService;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.ex.AuthenticationFailedException;
import uk.co.bbr.web.LoginMixin;
import uk.co.bbr.web.security.support.TestUser;

import java.time.LocalDate;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest(properties = { "spring.config.name=compare-conductors-web-tests-admin-h2", "spring.datasource.url=jdbc:h2:mem:compare-conductors-web-tests-admin-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE", "spring.jpa.database-platform=org.hibernate.dialect.SQLServerDialect"},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CompareConductorsWebTests implements LoginMixin {

    @Autowired private SecurityService securityService;
    @Autowired private JwtService jwtService;
    @Autowired private PersonService personService;
    @Autowired private ContestService contestService;
    @Autowired private ContestEventService contestEventService;
    @Autowired private ContestResultService contestResultService;
    @Autowired private BandService bandService;
    @Autowired private PieceService pieceService;
    @Autowired private RestTemplate restTemplate;
    @Autowired private CsrfTokenRepository csrfTokenRepository;
    @LocalServerPort private int port;

    @BeforeAll
    void setupUser() {
        this.securityService.createUser(TestUser.TEST_PRO.getUsername(), TestUser.TEST_PRO.getPassword(), TestUser.TEST_PRO.getEmail());
        this.securityService.makeUserPro(TestUser.TEST_PRO.getUsername());

        loginTestUserByWeb(TestUser.TEST_PRO, this.restTemplate, this.csrfTokenRepository, this.port);
    }

    @BeforeAll
    void setupPeople() throws AuthenticationFailedException {
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        this.personService.create("Childs", "David");
        PersonDao nickChilds = this.personService.create("Childs", "Nick");
        this.personService.create("Childs", "Bob");
        PersonDao davidRoberts = this.personService.create("Roberts", "David");

        BandDao rtb = this.bandService.create("Rothwell Temperance");
        BandDao dyke = this.bandService.create("Black Dyke");

        ContestDao yorkshireArea = this.contestService.create("Yorkshire Area");
        ContestEventDao yorkshireArea2010 = this.contestEventService.create(yorkshireArea, LocalDate.of(2010, 3, 3));

        ContestDao broadoakWhitFriday = this.contestService.create("Broadoak (Whit Friday)");
        ContestEventDao broadoak2011 = this.contestEventService.create(broadoakWhitFriday, LocalDate.of(2011, 6, 7));

        ContestResultDao yorkshireAreaResult1 = new ContestResultDao();
        yorkshireAreaResult1.setBand(rtb);
        yorkshireAreaResult1.setBandName("Rothwell Temperance");
        yorkshireAreaResult1.setConductor(davidRoberts);
        yorkshireAreaResult1.setPosition("1");
        yorkshireAreaResult1.setDraw(5);
        this.contestResultService.addResult(yorkshireArea2010, yorkshireAreaResult1);

        ContestResultDao yorkshireAreaResult2 = new ContestResultDao();
        yorkshireAreaResult2.setBand(dyke);
        yorkshireAreaResult2.setBandName("Black Dyke");
        yorkshireAreaResult2.setConductor(nickChilds);
        yorkshireAreaResult2.setPosition("2");
        yorkshireAreaResult2.setDraw(4);
        this.contestResultService.addResult(yorkshireArea2010, yorkshireAreaResult2);

        ContestResultDao whitFridayResult = new ContestResultDao();
        whitFridayResult.setBand(rtb);
        whitFridayResult.setBandName("Rothwell Temps");
        whitFridayResult.setConductor(davidRoberts);
        whitFridayResult.setPosition("2");
        whitFridayResult.setDraw(4);
        this.contestResultService.addResult(broadoak2011, whitFridayResult);

        this.pieceService.create("Bandance", PieceCategory.TEST_PIECE, davidRoberts);

        logoutTestUser();
    }

    @Test
    void testGetPersonDetailsPageWorksCorrectly() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/people/COMPARE-CONDUCTORS", String.class);
        assertNotNull(response);

        assertTrue(response.contains("Compare Conductors"));
        assertTrue(response.contains("First conductor:"));
        assertTrue(response.contains("Second conductor:"));
    }

    @Test
    void testGetPersonDetailsPageWithOnePersonPopulatedWorksCorrectly() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/people/COMPARE-CONDUCTORS/david-roberts", String.class);
        assertNotNull(response);

        assertTrue(response.contains("Compare Conductors"));
        assertTrue(response.contains("David Roberts"));
        assertTrue(response.contains("First conductor:"));
        assertTrue(response.contains("Second conductor:"));
    }

    @Test
    void testGetPersonDetailsPageWithOneInvalidPersonFailsAsExpected() {
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () -> this.restTemplate.getForObject("http://localhost:" + this.port + "/people/COMPARE-CONDUCTORS/not-a-real-person", String.class));
        assertTrue(Objects.requireNonNull(ex.getMessage()).contains("404"));
    }

    @Test
    void testResultPageWorksCorrectly() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/people/COMPARE-CONDUCTORS/david-roberts/nick-childs", String.class);
        assertNotNull(response);

        assertTrue(response.contains("Compare "));
        assertTrue(response.contains("David Roberts"));
        assertTrue(response.contains("Nick Childs"));
        assertTrue(response.contains("Black Dyke"));
        assertTrue(response.contains("Rothwell Temperance"));
        assertFalse(response.contains("Broadoak"));
    }

    @Test
    void testGetPersonDetailsPageWithLeftInvalidPersonFailsAsExpected() {
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () -> this.restTemplate.getForObject("http://localhost:" + this.port + "/people/COMPARE-CONDUCTORS/not-a-real-person/nick-childs", String.class));
        assertTrue(Objects.requireNonNull(ex.getMessage()).contains("404"));
    }

    @Test
    void testGetPersonDetailsPageWithRightInvalidPersonFailsAsExpected() {
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () -> this.restTemplate.getForObject("http://localhost:" + this.port + "/people/COMPARE-CONDUCTORS/david-roberts/not-a-real-person", String.class));
        assertTrue(Objects.requireNonNull(ex.getMessage()).contains("404"));
    }
}
