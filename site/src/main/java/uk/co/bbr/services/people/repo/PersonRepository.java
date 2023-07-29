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

    @Query("SELECT COUNT(p) FROM PersonDao p")
    int countPeople();

    @Query("SELECT p FROM PersonDao p WHERE p.id = (SELECT MAX(p1.id) FROM PersonDao p1)")
    PersonDao fetchLatestPerson();
}
