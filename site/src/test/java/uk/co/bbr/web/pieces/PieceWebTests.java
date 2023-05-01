package uk.co.bbr.web.pieces;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
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
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest(properties = { "spring.config.name=piece-list-web-tests-admin-h2", "spring.datasource.url=jdbc:h2:mem:piece-list-web-tests-admin-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE", "spring.jpa.database-platform=org.hibernate.dialect.SQLServerDialect"},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PieceWebTests implements LoginMixin {

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

        logoutTestUser();
    }

    @Test
    void testSinglePiecePageWorksSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/pieces/hootenanny", String.class);
        assertTrue(response.contains("<h2>Hootenanny</h2>"));
    }

}


