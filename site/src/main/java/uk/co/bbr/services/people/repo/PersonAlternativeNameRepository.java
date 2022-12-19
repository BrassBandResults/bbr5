package uk.co.bbr.services.people.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.co.bbr.services.people.dao.PersonAlternativeNameDao;

import java.util.List;

public interface PersonAlternativeNameRepository extends JpaRepository<PersonAlternativeNameDao, Long> {
    @Query("SELECT a FROM PersonAlternativeNameDao a WHERE a.person.id = ?1")
    List<PersonAlternativeNameDao> findForPersonId(Long personId);
}
