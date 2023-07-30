package uk.co.bbr.web.pieces;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;
import uk.co.bbr.services.bands.BandService;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.events.ContestEventService;
import uk.co.bbr.services.events.ResultService;
import uk.co.bbr.services.contests.ContestService;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.events.dao.ContestEventDao;
import uk.co.bbr.services.events.dao.ContestResultDao;
import uk.co.bbr.services.people.PersonService;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.pieces.PieceService;
import uk.co.bbr.services.pieces.dao.PieceDao;
import uk.co.bbr.services.pieces.types.PieceCategory;
import uk.co.bbr.services.security.JwtService;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.ex.AuthenticationFailedException;
import uk.co.bbr.web.LoginMixin;
import uk.co.bbr.web.security.support.TestUser;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest(properties = {  "spring.config.location=classpath:test-application.yml",
        "spring.datasource.url=jdbc:h2:mem:pieces-list-web-tests-admin-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PieceListWebTests implements LoginMixin {

    @Autowired private SecurityService securityService;
    @Autowired private JwtService jwtService;
    @Autowired private BandService bandService;
    @Autowired private PieceService pieceService;
    @Autowired private PersonService personService;
    @Autowired private ContestService contestService;
    @Autowired private ContestEventService contestEventService;
    @Autowired private ResultService contestResultService;
    @Autowired private RestTemplate restTemplate;
    @Autowired private CsrfTokenRepository csrfTokenRepository;
    @LocalServerPort private int port;

    @BeforeAll
    void setupUser() {
        loginTestUserByWeb(TestUser.TEST_MEMBER, this.restTemplate, this.csrfTokenRepository, this.port);
    }

    @BeforeAll
    void setupPieces() throws AuthenticationFailedException {
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        PersonDao composer1 = this.personService.create("Composer", "1");
        PersonDao composer2 = this.personService.create("Composer", "2");
        PersonDao composer3 = this.personService.create("Composer", "3");
        PersonDao composer4 = this.personService.create("Composer", "4");

        PieceDao journey = this.pieceService.create("Journey To The Centre Of The Earth", PieceCategory.TEST_PIECE, composer2);
        this.pieceService.create("Contest Music", PieceCategory.TEST_PIECE, composer1);
        this.pieceService.create("Contest Test Piece", PieceCategory.TEST_PIECE, composer1);
        PieceDao hootenanny = this.pieceService.create("Hootenanny", PieceCategory.ENTERTAINMENT, composer3);
        this.pieceService.create("T'Wizard", PieceCategory.MARCH, composer4);
        this.pieceService.create("Aardvark", PieceCategory.ENTERTAINMENT, composer2);
        this.pieceService.create("1st Class", PieceCategory.ENTERTAINMENT, composer2);

        BandDao rtb = this.bandService.create("Rothwell Temperance Band");

        ContestDao ownChoiceContest = this.contestService.create("Own Choice Contest");
        ContestEventDao ownChoiceContestEvent = this.contestEventService.create(ownChoiceContest, LocalDate.of(2020, 3, 1));
        ContestResultDao ownChoiceResult = new ContestResultDao();
        ownChoiceResult.setConductor(composer1);
        ownChoiceResult.setBand(rtb);
        ownChoiceResult.setBandName("Rothwell Temps");
        ownChoiceResult = this.contestResultService.addResult(ownChoiceContestEvent, ownChoiceResult);
        this.contestResultService.addPieceToResult(ownChoiceResult, hootenanny);

        ContestDao setTestContest = this.contestService.create("Set Test Contest");
        ContestEventDao setTestContestEvent = this.contestEventService.create(setTestContest, LocalDate.of(2020, 4, 1));
        this.contestEventService.addTestPieceToContest(setTestContestEvent, journey);

        logoutTestUser();
    }

    @Test
    void testGetPiecesListForMultiplePeopleWorksSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/pieces", String.class);
        assertNotNull(response);
        assertTrue(response.contains("<title>Pieces - Brass Band Results</title>"));
        assertTrue(response.contains("<h2>Pieces starting with A</h2>"));
        assertTrue(response.contains("Showing 1 of 7 pieces."));

        assertTrue(response.contains("Aardvark"));
        assertFalse(response.contains("Hootenanny"));
        assertFalse(response.contains("Contest Music"));
        assertFalse(response.contains("Journey To The Centre Of The Earth"));
        assertFalse(response.contains("1st Class"));

        assertFalse(response.contains(">1<"));
    }

    @Test
    void testGetPiecesListForOnePersonWorksSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/pieces/C", String.class);
        assertNotNull(response);
        assertTrue(response.contains("<title>Pieces - Brass Band Results</title>"));
        assertTrue(response.contains("<h2>Pieces starting with C</h2>"));
        assertTrue(response.contains("Showing 2 of 7 pieces."));

        assertFalse(response.contains("Aardvark"));
        assertTrue(response.contains("Contest Music"));
        assertTrue(response.contains("Contest Test Piece"));
        assertFalse(response.contains("Journey To The Centre Of The Earth"));
        assertFalse(response.contains("1st Class"));

        assertFalse(response.contains(">1<"));
    }

    @Test
    void testGetPiecesListForSpecificLetterWorksSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/pieces/J", String.class);
        assertNotNull(response);
        assertTrue(response.contains("<title>Pieces - Brass Band Results</title>"));
        assertTrue(response.contains("<h2>Pieces starting with J</h2>"));
        assertTrue(response.contains("Showing 1 of 7 pieces."));

        assertFalse(response.contains("Aardvark"));
        assertFalse(response.contains("Hootenanny"));
        assertFalse(response.contains("Contest Music"));
        assertFalse(response.contains("Contest Test Piece"));
        assertTrue(response.contains("Journey To The Centre Of The Earth"));
        assertFalse(response.contains("1st Class"));

        // Journey To The Centre of the Earth should have set test
        assertTrue(response.contains(">1<"));
    }

    @Test
    void testGetPiecesListForSpecificLetterWithOwnChoiceWorksSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/pieces/H", String.class);
        assertNotNull(response);
        assertTrue(response.contains("<title>Pieces - Brass Band Results</title>"));
        assertTrue(response.contains("<h2>Pieces starting with H</h2>"));
        assertTrue(response.contains("Showing 1 of 7 pieces."));

        assertFalse(response.contains("Aardvark"));
        assertTrue(response.contains("Hootenanny"));
        assertFalse(response.contains("Contest Music"));
        assertFalse(response.contains("Contest Test Piece"));
        assertFalse(response.contains("Journey To The Centre Of The Earth"));
        assertFalse(response.contains("1st Class"));

        // Hootenanny should be own choice
        assertTrue(response.contains(">1<"));
    }

    @Test
    void testGetPiecesListForNumbersWorksSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/pieces/0", String.class);
        assertNotNull(response);
        assertTrue(response.contains("<title>Pieces - Brass Band Results</title>"));
        assertTrue(response.contains("<h2>Pieces starting with numbers</h2>"));
        assertTrue(response.contains("Showing 1 of 7 pieces."));

        assertFalse(response.contains("Aardvark"));
        assertFalse(response.contains("Hootenanny"));
        assertFalse(response.contains("Contest Music"));
        assertFalse(response.contains("Contest Test Piece"));
        assertFalse(response.contains("Journey To The Centre Of The Earth"));
        assertTrue(response.contains("1st Class"));

        assertFalse(response.contains(">1<"));
    }

    @Test
    void testGetAllPiecesListWorksSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/pieces/ALL", String.class);
        assertNotNull(response);
        assertTrue(response.contains("<title>Pieces - Brass Band Results</title>"));
        assertTrue(response.contains("<h2>All Pieces</h2>"));
        assertTrue(response.contains("Showing 7 of 7 pieces."));

        assertTrue(response.contains("Aardvark"));
        assertTrue(response.contains("Contest Music"));
        assertTrue(response.contains("Contest Test Piece"));
        assertTrue(response.contains("Journey To The Centre Of The Earth"));
        assertTrue(response.contains("T&#39;Wizard"));
        assertTrue(response.contains("1st Class"));

        assertTrue(response.contains(">1<"));
    }
}


