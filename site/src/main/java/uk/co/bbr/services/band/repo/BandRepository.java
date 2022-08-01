package uk.co.bbr.services.band.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.co.bbr.services.band.dao.BandDao;

import java.util.List;

public interface BandRepository extends JpaRepository<BandDao, Long> {

    @Query("SELECT b FROM BandDao b ORDER BY b.name")
    List<BandDao> findAll();
    @Query("SELECT b FROM BandDao b WHERE UPPER(b.name) LIKE UPPER(CONCAT(:prefix, '%')) ORDER BY b.name")
    List<BandDao> findByPrefix(String prefix);

    @Query("SELECT b FROM BandDao b " +
            "WHERE b.name LIKE UPPER('0%')" +
            "OR b.name LIKE UPPER('1%') " +
            "OR b.name LIKE UPPER('2%') " +
            "OR b.name LIKE UPPER('3%') " +
            "OR b.name LIKE UPPER('4%') " +
            "OR b.name LIKE UPPER('5%') " +
            "OR b.name LIKE UPPER('6%') " +
            "OR b.name LIKE UPPER('7%') " +
            "OR b.name LIKE UPPER('8%') " +
            "OR b.name LIKE UPPER('9%') ORDER BY b.name")
    List<BandDao> findWithNumberPrefix();
}
