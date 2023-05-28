package uk.co.bbr.services.people.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.co.bbr.services.people.dao.PersonDao;

import java.util.List;
import java.util.Optional;

public interface PersonRepository extends JpaRepository<PersonDao, Long> {
    @Query("SELECT p FROM PersonDao p WHERE p.slug = ?1")
    Optional<PersonDao> fetchBySlug(String personSlug);

    @Query("SELECT p FROM PersonDao p WHERE p.id = ?1")
    Optional<PersonDao> fetchById(long personId);

    @Query("SELECT p FROM PersonDao p ORDER BY p.surname, p.firstNames")
    List<PersonDao> findAllOrderBySurname();

    @Query("SELECT p FROM PersonDao p WHERE UPPER(p.surname) LIKE UPPER(CONCAT(:prefix, '%'))  ORDER BY p.surname, p.firstNames")
    List<PersonDao> findByPrefixOrderBySurname(String prefix);

    @Query("SELECT p FROM PersonDao p WHERE UPPER(p.combinedName) = :personNameUpper")
    Optional<PersonDao> fetchByCombinedName(String personNameUpper);

    @Query("SELECT p FROM PersonDao p WHERE UPPER(p.combinedName) LIKE :searchStringUpper")
    List<PersonDao> lookupByPrefix(String searchStringUpper);
}
