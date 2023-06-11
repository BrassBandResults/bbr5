package uk.co.bbr.web.pieces;

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest(properties = { "spring.config.name=piece-single-web-tests-admin-h2", "spring.datasource.url=jdbc:h2:mem:piece-single-web-tests-admin-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE", "spring.jpa.database-platform=org.hibernate.dialect.SQLServerDialect"},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PieceWebTests implements LoginMixin {

    @Autowired private SecurityService securityService;
    @Autowired private JwtService jwtService;
    @Autowired private BandService bandService;
    @Autowired private PersonService personService;
    @Autowired private PieceService pieceService;
    @Autowired private ContestService contestService;
    @Autowired private ContestEventService contestEventService;
    @Autowired private ResultService contestResultService;
    @Autowired private RestTemplate restTemplate;
    @LocalServerPort private int port;



    @BeforeAll
    void setupPieces() throws AuthenticationFailedException {
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        PersonDao composer1 = this.personService.create("Composer", "1");
        PersonDao composer2 = this.personService.create("Composer", "2");
        PersonDao composer3 = this.personService.create("Composer", "3");
        PersonDao composer4 = this.personService.create("Composer", "4");

        this.pieceService.create("Journey To The Centre Of The Earth", PieceCategory.TEST_PIECE, composer2);
        PieceDao contestMusic = this.pieceService.create("Contest Music", PieceCategory.TEST_PIECE, composer1);
        this.pieceService.create("Contest Test Piece", PieceCategory.TEST_PIECE, composer1);
        PieceDao hootenanny = this.pieceService.create("Hootenanny", PieceCategory.ENTERTAINMENT, composer3);
        this.pieceService.create("T'Wizard", PieceCategory.MARCH, composer4);
        this.pieceService.create("Aardvark", PieceCategory.ENTERTAINMENT, composer2);

        BandDao rtb = this.bandService.create("Rothwell Temperance");
        PersonDao davidRoberts = this.personService.create("Roberts", "David");

        ContestDao yorkshireArea = this.contestService.create("Yorkshire Area");
        ContestEventDao yorkshireArea2011 = this.contestEventService.create(yorkshireArea, LocalDate.of(2011, 3, 1));
        ContestResultDao yorkshireResult = this.contestResultService.addResult(yorkshireArea2011, "1", rtb, davidRoberts);

        ContestDao midlandsArea = this.contestService.create("Midlands Area");
        ContestEventDao midlandsArea2011 = this.contestEventService.create(midlandsArea, LocalDate.of(2011, 3, 1));
        ContestResultDao midlandsResult = this.contestResultService.addResult(midlandsArea2011, "1", rtb, davidRoberts);


        this.contestResultService.addPieceToResult(midlandsResult, contestMusic);
        this.contestEventService.addTestPieceToContest(yorkshireArea2011, hootenanny);

        logoutTestUser();
    }

    @Test
    void testSinglePiecePageWorksWithTestPieceSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/pieces/hootenanny", String.class);
        assertNotNull(response);
        assertTrue(response.contains("<title>Hootenanny - Piece - Brass Band Results</title>"));
        assertTrue(response.contains("Hootenanny"));

        assertTrue(response.contains(">Yorkshire Area<"));
    }

    @Test
    void testSinglePiecePageWorksWithResultPieceSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/pieces/contest-music", String.class);
        assertNotNull(response);

        assertTrue(response.contains("<title>Contest Music - Piece - Brass Band Results</title>"));
        assertTrue(response.contains("Contest Music"));

        assertTrue(response.contains(">Midlands Area<"));
    }

    @Test
    void testGetPersonDetailsPageFailsWithInvalidSlug() {
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () -> this.restTemplate.getForObject("http://localhost:" + this.port + "/pieces/not-a-real-piece", String.class));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }
}


