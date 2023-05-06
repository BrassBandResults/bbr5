package uk.co.bbr.web.pieces;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;
import uk.co.bbr.services.people.PersonService;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.pieces.PieceService;
import uk.co.bbr.services.pieces.types.PieceCategory;
import uk.co.bbr.services.security.JwtService;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.ex.AuthenticationFailedException;
import uk.co.bbr.web.LoginMixin;
import uk.co.bbr.web.security.support.TestUser;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest(properties = { "spring.config.name=piece-list-web-tests-admin-h2", "spring.datasource.url=jdbc:h2:mem:piece-list-web-tests-admin-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE", "spring.jpa.database-platform=org.hibernate.dialect.SQLServerDialect"},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PieceListWebTests implements LoginMixin {

    @Autowired private SecurityService securityService;
    @Autowired private JwtService jwtService;
    @Autowired private PieceService pieceService;
    @Autowired private PersonService personService;
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
        this.pieceService.create("Contest Music", PieceCategory.TEST_PIECE, composer1);
        this.pieceService.create("Contest Test Piece", PieceCategory.TEST_PIECE, composer1);
        this.pieceService.create("Hootenanny", PieceCategory.ENTERTAINMENT, composer3);
        this.pieceService.create("T'Wizard", PieceCategory.MARCH, composer4);
        this.pieceService.create("Aardvark", PieceCategory.ENTERTAINMENT, composer2);
        this.pieceService.create("1st Class", PieceCategory.ENTERTAINMENT, composer2);

        logoutTestUser();
    }

    @Test
    void testGetPiecesListForMultiplePeopleWorksSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/pieces", String.class);
        assertNotNull(response);
        assertTrue(response.contains("<h2>Pieces starting with A</h2>"));
        assertTrue(response.contains("Showing 1 of 7 pieces."));

        assertTrue(response.contains("Aardvark"));
        assertFalse(response.contains("Hootenanny"));
        assertFalse(response.contains("Contest Music"));
        assertFalse(response.contains("Journey To The Centre Of The Earth"));
        assertFalse(response.contains("1st Class"));
    }

    @Test
    void testGetPiecesListForOnePersonWorksSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/pieces/C", String.class);
        assertNotNull(response);
        assertTrue(response.contains("<h2>Pieces starting with C</h2>"));
        assertTrue(response.contains("Showing 2 of 7 pieces."));

        assertFalse(response.contains("Aardvark"));
        assertTrue(response.contains("Contest Music"));
        assertTrue(response.contains("Contest Test Piece"));
        assertFalse(response.contains("Journey To The Centre Of The Earth"));
        assertFalse(response.contains("1st Class"));
    }

    @Test
    void testGetPiecesListForSpecificLetterWorksSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/pieces/J", String.class);
        assertNotNull(response);
        assertTrue(response.contains("<h2>Pieces starting with J</h2>"));
        assertTrue(response.contains("Showing 1 of 7 pieces."));

        assertFalse(response.contains("Aardvark"));
        assertFalse(response.contains("Hootenanny"));
        assertFalse(response.contains("Contest Music"));
        assertFalse(response.contains("Contest Test Piece"));
        assertTrue(response.contains("Journey To The Centre Of The Earth"));
        assertFalse(response.contains("1st Class"));
    }

    @Test
    void testGetPiecesListForNumbersWorksSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/pieces/0", String.class);
        assertNotNull(response);
        assertTrue(response.contains("<h2>Pieces starting with numbers</h2>"));
        assertTrue(response.contains("Showing 1 of 7 pieces."));

        assertFalse(response.contains("Aardvark"));
        assertFalse(response.contains("Hootenanny"));
        assertFalse(response.contains("Contest Music"));
        assertFalse(response.contains("Contest Test Piece"));
        assertFalse(response.contains("Journey To The Centre Of The Earth"));
        assertTrue(response.contains("1st Class"));
    }

    @Test
    void testGetAllPiecesListWorksSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/pieces/ALL", String.class);
        assertNotNull(response);
        assertTrue(response.contains("<h2>All Pieces</h2>"));
        assertTrue(response.contains("Showing 7 of 7 pieces."));

        assertTrue(response.contains("Aardvark"));
        assertTrue(response.contains("Contest Music"));
        assertTrue(response.contains("Contest Test Piece"));
        assertTrue(response.contains("Journey To The Centre Of The Earth"));
        assertTrue(response.contains("T&#39;Wizard"));
        assertTrue(response.contains("1st Class"));
    }
}


