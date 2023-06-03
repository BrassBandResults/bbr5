package uk.co.bbr.services.people.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.co.bbr.services.people.dao.PersonRelationshipDao;

import java.util.List;
import java.util.Optional;

public interface PersonRelationshipRepository extends JpaRepository<PersonRelationshipDao, Long> {
    @Query("SELECT r FROM PersonRelationshipDao r WHERE r.leftPerson.id = :personId OR r.rightPerson.id = :personId ORDER BY r.leftPerson.combinedName, r.rightPerson.combinedName")
    List<PersonRelationshipDao> findForPerson(Long personId);

    @Query("SELECT r FROM PersonRelationshipDao r WHERE r.id = :relationshipId")
    Optional<PersonRelationshipDao> fetchById(Long relationshipId);
}
