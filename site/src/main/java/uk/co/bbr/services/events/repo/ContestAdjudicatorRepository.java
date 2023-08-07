package uk.co.bbr.services.events.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.co.bbr.services.events.dao.ContestAdjudicatorDao;

import java.util.List;
import java.util.Optional;

public interface ContestAdjudicatorRepository extends JpaRepository<ContestAdjudicatorDao, Long> {
    @Query("SELECT a FROM ContestAdjudicatorDao a WHERE a.contestEvent.id = :contestEventId ORDER BY a.adjudicator.surname")
    List<ContestAdjudicatorDao> fetchForEvent(Long contestEventId);

    @Query("SELECT count(a) FROM ContestAdjudicatorDao a WHERE a.adjudicator.id = :personId")
    int fetchAdjudicationCountForPerson(Long personId);

    @Query("SELECT a FROM ContestAdjudicatorDao a WHERE a.contestEvent.id = :contestEventId AND a.id = :adjudicatorId")
    Optional<ContestAdjudicatorDao> fetchForEventAndAdjudicator(Long contestEventId, Long adjudicatorId);
}
