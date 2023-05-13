package uk.co.bbr.services.contests.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.co.bbr.services.contests.dao.ContestAdjudicatorDao;

import java.util.List;

public interface ContestAdjudicatorRepository extends JpaRepository<ContestAdjudicatorDao, Long> {
    @Query("SELECT a FROM ContestAdjudicatorDao a WHERE a.contestEvent.id = :contestEventId")
    List<ContestAdjudicatorDao> fetchForEvent(Long contestEventId);

    @Query("SELECT count(a) FROM ContestAdjudicatorDao a WHERE a.adjudicator.id = :personId")
    int fetchAdjudicationCountForPerson(Long personId);
}
