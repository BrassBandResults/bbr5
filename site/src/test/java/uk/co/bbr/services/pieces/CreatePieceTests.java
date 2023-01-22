package uk.co.bbr.services.pieces;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import uk.co.bbr.services.framework.ValidationException;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.pieces.dao.PieceDao;
import uk.co.bbr.services.pieces.types.PieceCategory;
import uk.co.bbr.services.security.JwtService;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.ex.AuthenticationFailedException;
import uk.co.bbr.web.LoginMixin;
import uk.co.bbr.web.security.support.TestUser;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest(properties = { "spring.config.name=piece-create-tests-h2", "spring.datasource.url=jdbc:h2:mem:piece-create-tests-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CreatePieceTests implements LoginMixin {

    @Autowired private PieceService pieceService;
    @Autowired private SecurityService securityService;
    @Autowired private JwtService jwtService;

    @Test
    void createPieceWorksSuccessfully() throws AuthenticationFailedException {
        // arrange
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        PieceDao piece = new PieceDao();
        piece.setNotes(" Notes ");
        piece.setName(" Piece Name ");
        piece.setOldId(" 123 ");
        piece.setYear("1982");

        // act
        PieceDao returnedPiece = this.pieceService.create(piece);

        // assert
        assertEquals("Piece Name", returnedPiece.getName());
        assertEquals("Notes", returnedPiece.getNotes());
        assertEquals("123", returnedPiece.getOldId());
        assertEquals("1982", returnedPiece.getYear());
        assertEquals(PieceCategory.TEST_PIECE, returnedPiece.getCategory());

        logoutTestUser();
    }

    @Test
    void testCreatedPieceCanBeFetchedByIdSuccessfully() throws AuthenticationFailedException {
        // arrange
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        PieceDao piece = new PieceDao();
        piece.setName(" Piece Name  2  ");
        piece.setOldId("432 ");
        piece.setYear("  1980  ");
        piece.setCategory(PieceCategory.MARCH);
        PieceDao savedPiece = this.pieceService.create(piece);

        // act
        Optional<PieceDao> returnedPiece = this.pieceService.fetchById(savedPiece.getId());

        // assert
        assertTrue(returnedPiece.isPresent());
        assertFalse(returnedPiece.isEmpty());
        assertEquals("Piece Name 2", returnedPiece.get().getName());
        assertNull(returnedPiece.get().getNotes());
        assertEquals("432", returnedPiece.get().getOldId());
        assertEquals("1980", returnedPiece.get().getYear());
        assertEquals(PieceCategory.MARCH, returnedPiece.get().getCategory());

        logoutTestUser();
    }

    @Test
    void testCreatedPieceCanBeFetchedBySlugSuccessfully() throws AuthenticationFailedException {
        // arrange
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        PieceDao piece = new PieceDao();
        piece.setNotes(" Notes Section ");
        piece.setName(" Piece Title ");
        piece.setOldId(" 43233 ");
        piece.setYear("1981 ");
        piece.setCategory(PieceCategory.HYMN);
        PieceDao savedPiece = this.pieceService.create(piece);

        // act
        Optional<PieceDao> returnedPiece = this.pieceService.fetchBySlug(savedPiece.getSlug());

        // assert
        assertTrue(returnedPiece.isPresent());
        assertFalse(returnedPiece.isEmpty());
        assertEquals("Piece Title", returnedPiece.get().getName());
        assertEquals("Notes Section", returnedPiece.get().getNotes());
        assertEquals("43233", returnedPiece.get().getOldId());
        assertEquals("1981", returnedPiece.get().getYear());
        assertEquals(PieceCategory.HYMN, returnedPiece.get().getCategory());

        logoutTestUser();
    }

    @Test
    void testCreatingPieceWithDuplicateSlugFails() throws AuthenticationFailedException {
        // arrange
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        PieceDao piece = this.pieceService.create(" PIECE    1 ");

        // act
        ValidationException ex = assertThrows(ValidationException.class, ()-> {this.pieceService.create("Piece 1");});

        // assert
        assertEquals("Piece with slug piece-1 already exists.", ex.getMessage());

        logoutTestUser();
    }
}
