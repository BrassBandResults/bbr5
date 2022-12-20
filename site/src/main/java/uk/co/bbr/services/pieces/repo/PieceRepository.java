package uk.co.bbr.services.pieces.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.co.bbr.services.pieces.dao.PieceDao;

import java.util.List;
import java.util.Optional;

public interface PieceRepository extends JpaRepository<PieceDao, Long> {
    @Query("SELECT p FROM PieceDao p WHERE p.slug = ?1")
    Optional<PieceDao> fetchBySlug(String pieceSlug);

    @Query("SELECT p FROM PieceDao p WHERE p.id = ?1")
    Optional<PieceDao> fetchById(long pieceId);

    @Query("SELECT p FROM PieceDao p ORDER BY p.name")
    List<PieceDao> findAll();

    @Query("SELECT p FROM PieceDao p WHERE UPPER(p.name) LIKE UPPER(CONCAT(:prefix, '%'))  ORDER BY p.name")
    List<PieceDao> findByPrefix(String prefix);
}
