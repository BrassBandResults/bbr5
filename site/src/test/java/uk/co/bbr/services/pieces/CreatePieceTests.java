package uk.co.bbr.services.pieces;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import uk.co.bbr.services.pieces.dao.PieceDao;
import uk.co.bbr.services.pieces.types.PieceCategory;
import uk.co.bbr.web.LoginMixin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest(properties = { "spring.config.name=piece-create-tests-h2", "spring.datasource.url=jdbc:h2:mem:piece-create-tests-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CreatePieceTests implements LoginMixin {

    @Autowired private PieceService pieceService;

    @Test
    void createPieceWorksSuccessfully() {
        // arrange
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
    }

    @Test
    void testCreatedPieceCanBeFetchedByIdSuccessfully() {
        // arrange
        PieceDao piece = new PieceDao();
        piece.setName(" Piece Name   ");
        piece.setOldId("432 ");
        piece.setYear("  1980  ");
        piece.setCategory(PieceCategory.MARCH);
        PieceDao savedPiece = this.pieceService.create(piece);

        // act
        PieceDao returnedPiece = this.pieceService.fetchById(savedPiece.getId());

        // assert
        assertEquals("Piece Name", returnedPiece.getName());
        assertNull(returnedPiece.getNotes());
        assertEquals("432", returnedPiece.getOldId());
        assertEquals("1980", returnedPiece.getYear());
        assertEquals(PieceCategory.MARCH, returnedPiece.getCategory());
    }

    @Test
    void testCreatedPieceCanBeFetchedBySlugSuccessfully() {
        // arrange
        PieceDao piece = new PieceDao();
        piece.setNotes(" Notes Section ");
        piece.setName(" Piece Title ");
        piece.setOldId(" 43233 ");
        piece.setYear("1981 ");
        piece.setCategory(PieceCategory.HYMN);
        PieceDao savedPiece = this.pieceService.create(piece);

        // act
        PieceDao returnedPiece = this.pieceService.fetchBySlug(savedPiece.getSlug());

        // assert
        assertEquals("Piece Title", returnedPiece.getName());
        assertEquals("Notes Section", returnedPiece.getNotes());
        assertEquals("43233", returnedPiece.getOldId());
        assertEquals("1981", returnedPiece.getYear());
        assertEquals(PieceCategory.HYMN, returnedPiece.getCategory());
    }
}
