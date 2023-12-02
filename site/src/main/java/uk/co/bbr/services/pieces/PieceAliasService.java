package uk.co.bbr.services.pieces;

import uk.co.bbr.services.pieces.dao.PieceAliasDao;
import uk.co.bbr.services.pieces.dao.PieceDao;

import java.util.List;
import java.util.Optional;

public interface PieceAliasService {
    PieceAliasDao createAlias(PieceDao person, PieceAliasDao previousName);
    List<PieceAliasDao> findAllAliases(PieceDao person);
    List<PieceAliasDao> findVisibleAliases(PieceDao person);

    Optional<PieceAliasDao> aliasExists(PieceDao person, String aliasName);

    void deleteAlias(PieceDao person, Long aliasId);

    void showAlias(PieceDao person, Long aliasId);

    void hideAlias(PieceDao person, Long aliasId);
}
