package uk.co.bbr.services.people.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.co.bbr.services.people.dao.PersonProfileDao;

import java.util.List;
import java.util.Optional;

public interface PersonProfileRepository extends JpaRepository<PersonProfileDao, Long> {

    @Query("SELECT p FROM PersonProfileDao p WHERE p.person.id = :personId")
    Optional<PersonProfileDao> fetchProfileForUser(Long personId);

    @Query("SELECT p FROM PersonProfileDao p ORDER BY p.person.surname")
    List<PersonProfileDao> fetchAll();

    @Query("SELECT p FROM PersonProfileDao p WHERE p.createdBy = :ownerUsername ORDER BY p.person.surname")
    List<PersonProfileDao> fetchForOwner(String ownerUsername);
}
