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

    @Query("SELECT p FROM PieceDao p " +
            "WHERE p.name LIKE UPPER('0%')" +
            "OR p.name LIKE UPPER('1%') " +
            "OR p.name LIKE UPPER('2%') " +
            "OR p.name LIKE UPPER('3%') " +
            "OR p.name LIKE UPPER('4%') " +
            "OR p.name LIKE UPPER('5%') " +
            "OR p.name LIKE UPPER('6%') " +
            "OR p.name LIKE UPPER('7%') " +
            "OR p.name LIKE UPPER('8%') " +
            "OR p.name LIKE UPPER('9%') ORDER BY p.name")
    List<PieceDao> findWithNumberPrefix();
}
