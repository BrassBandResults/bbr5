package uk.co.bbr.services.pieces;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import uk.co.bbr.services.pieces.dao.PieceAliasDao;
import uk.co.bbr.services.pieces.dao.PieceDao;
import uk.co.bbr.services.security.JwtService;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.ex.AuthenticationFailedException;
import uk.co.bbr.web.LoginMixin;
import uk.co.bbr.web.security.support.TestUser;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@SpringBootTest(properties = {  "spring.config.location=classpath:test-application.yml",
                                "spring.datasource.url=jdbc:h2:mem:pieces-create-piece-alias-services-tests-admin-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CreatePieceAliasTests implements LoginMixin {

    @Autowired private PieceService pieceService;
    @Autowired private SecurityService securityService;
    @Autowired private JwtService jwtService;

    @Test
    void createAlternativeNameWorksSuccessfully() throws AuthenticationFailedException {
        // arrange
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        PieceDao piece = new PieceDao();
        piece.setNotes(" Notes ");
        piece.setName("Piece Name");
        piece.setOldId(" 123 ");
        PieceDao returnedPerson = this.pieceService.create(piece);

        PieceAliasDao altName = new PieceAliasDao();
        altName.setName("  Another Name  ");

        // act
        this.pieceService.createAlternativeName(piece, altName);

        // assert
        List<PieceAliasDao> altNames = this.pieceService.fetchAlternateNames(piece);
        assertEquals(1, altNames.size());
        assertEquals("Another Name", altNames.get(0).getName());
        assertEquals(piece.getName(), altNames.get(0).getPiece().getName());

        logoutTestUser();
    }
}
