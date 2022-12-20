package uk.co.bbr.services.pieces;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import uk.co.bbr.services.pieces.dao.PieceAlternativeNameDao;
import uk.co.bbr.services.pieces.dao.PieceDao;
import uk.co.bbr.web.LoginMixin;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@SpringBootTest(properties = { "spring.config.name=piece-alternative-tests-h2", "spring.datasource.url=jdbc:h2:mem:piece-alternative-tests-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AlternativeNamePieceTests implements LoginMixin {

    @Autowired private PieceService pieceService;

    @Test
    void createAlternativeNameWorksSuccessfully() {
        // arrange
        PieceDao piece = new PieceDao();
        piece.setNotes(" Notes ");
        piece.setName("Piece Name");
        piece.setOldId(" 123 ");
        PieceDao returnedPerson = this.pieceService.create(piece);

        PieceAlternativeNameDao altName = new PieceAlternativeNameDao();
        altName.setName("  Another Name  ");

        // act
        this.pieceService.createAlternativeName(piece, altName);

        // assert
        List<PieceAlternativeNameDao> altNames = this.pieceService.fetchAlternateNames(piece);
        assertEquals(1, altNames.size());
        assertEquals("Another Name", altNames.get(0).getName());
        assertEquals(piece.getName(), altNames.get(0).getPiece().getName());
    }
}
