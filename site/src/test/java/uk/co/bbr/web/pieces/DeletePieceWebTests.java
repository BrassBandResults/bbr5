package uk.co.bbr.web.pieces;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;
import uk.co.bbr.services.bands.BandService;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.contests.ContestService;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.events.ContestEventService;
import uk.co.bbr.services.events.ResultService;
import uk.co.bbr.services.events.dao.ContestEventDao;
import uk.co.bbr.services.events.dao.ContestResultDao;
import uk.co.bbr.services.people.PersonService;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.pieces.PieceService;
import uk.co.bbr.services.pieces.dao.PieceAliasDao;
import uk.co.bbr.services.pieces.dao.PieceDao;
import uk.co.bbr.services.security.JwtService;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.UserService;
import uk.co.bbr.services.security.dao.SiteUserDao;
import uk.co.bbr.services.security.ex.AuthenticationFailedException;
import uk.co.bbr.web.LoginMixin;
import uk.co.bbr.web.security.support.TestUser;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest(properties = {  "spring.config.location=classpath:test-application.yml",
        "spring.datasource.url=jdbc:h2:mem:pieces-delete-piece-web-tests-admin-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DeletePieceWebTests implements LoginMixin {

    @Autowired private SecurityService securityService;
    @Autowired private JwtService jwtService;
    @Autowired private ContestService contestService;
    @Autowired private ResultService resultService;
    @Autowired private BandService bandService;
    @Autowired private PersonService personService;
    @Autowired private ContestEventService contestEventService;
    @Autowired private PieceService pieceService;
    @Autowired private UserService userService;
    @Autowired private CsrfTokenRepository csrfTokenRepository;
    @Autowired private RestTemplate restTemplate;
    @LocalServerPort private int port;

    @BeforeAll
    void setupUser() {
        this.securityService.createUser(TestUser.TEST_MEMBER.getUsername(), TestUser.TEST_MEMBER.getPassword(), TestUser.TEST_MEMBER.getEmail());
        loginTestUserByWeb(TestUser.TEST_MEMBER, this.restTemplate, this.csrfTokenRepository, this.port);
    }

    @Test
    void testDeletePieceWithNoLinksSucceeds() throws AuthenticationFailedException {
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        this.pieceService.create("Piece 1");

        Optional<PieceDao> beforeDelete = this.pieceService.fetchBySlug("piece-1");
        assertTrue(beforeDelete.isPresent());

        ResponseEntity<String> response = this.restTemplate.getForEntity("http://localhost:" + this.port + "/pieces/piece-1/delete", String.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains(">Pieces starting with A<"));

        Optional<PieceDao> afterDelete = this.pieceService.fetchBySlug("piece-1");
        assertTrue(afterDelete.isEmpty());
    }

    @Test
    void testDeletePieceWithAliasesSucceeds() throws AuthenticationFailedException {
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        PieceDao piece = this.pieceService.create("Piece 2");
        PieceAliasDao alternateName = new PieceAliasDao();
        alternateName.setName("Piece Alias");
        this.pieceService.createAlternativeName(piece, alternateName);

        Optional<PieceDao> beforeDelete = this.pieceService.fetchBySlug("piece-2");
        assertTrue(beforeDelete.isPresent());

        ResponseEntity<String> response = this.restTemplate.getForEntity("http://localhost:" + this.port + "/pieces/piece-2/delete", String.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains(">Pieces starting with A<"));

        Optional<PieceDao> afterDelete = this.pieceService.fetchBySlug("piece-2");
        assertTrue(afterDelete.isEmpty());
    }

    @Test
    void testDeletePieceWithSetTestsFailsAsExpected() throws AuthenticationFailedException {
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        PieceDao piece = this.pieceService.create("Piece 3");
        ContestDao contest = this.contestService.create("Yorkshire Area 3");
        ContestEventDao contestEvent = this.contestEventService.create(contest, LocalDate.of(2012, 4, 1));
        this.contestEventService.addTestPieceToContest(contestEvent, piece);

        Optional<PieceDao> beforeDelete = this.pieceService.fetchBySlug("piece-3");
        assertTrue(beforeDelete.isPresent());

        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/pieces/piece-3/delete", String.class);
        assertNotNull(response);
        assertTrue(response.contains("This piece is used and cannot be deleted."));

        Optional<PieceDao> afterDelete = this.pieceService.fetchBySlug("piece-3");
        assertFalse(afterDelete.isEmpty());
    }

    @Test
    void testDeletePieceWithOwnChoiceFailsAsExpected() throws AuthenticationFailedException {
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        PieceDao piece = this.pieceService.create("Piece 4");
        ContestDao contest = this.contestService.create("Yorkshire Area 4");
        ContestEventDao contestEvent = this.contestEventService.create(contest, LocalDate.of(2012, 4, 1));
        BandDao band = this.bandService.create("Rothwell Temperance 4");
        PersonDao conductor = this.personService.create("Conductor", "A");
        ContestResultDao result = this.resultService.addResult(contestEvent, "1", band, conductor);
        this.resultService.addPieceToResult(result, piece);

        Optional<PieceDao> beforeDelete = this.pieceService.fetchBySlug("piece-4");
        assertTrue(beforeDelete.isPresent());

        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/pieces/piece-4/delete", String.class);
        assertNotNull(response);
        assertTrue(response.contains("This piece is used and cannot be deleted."));

        Optional<PieceDao> afterDelete = this.pieceService.fetchBySlug("piece-4");
        assertFalse(afterDelete.isEmpty());
    }
}
