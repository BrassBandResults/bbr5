package uk.co.bbr.services.people.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.co.bbr.services.people.dao.PersonAliasDao;

import java.util.List;
import java.util.Optional;

public interface PersonAliasRepository extends JpaRepository<PersonAliasDao, Long> {
    @Query("SELECT a FROM PersonAliasDao a WHERE a.person.id = ?1 ORDER BY a.oldName")
    List<PersonAliasDao> findForPersonId(Long personId);

    @Query("SELECT a FROM PersonAliasDao a WHERE a.person.id = ?1 AND a.oldName = ?2")
    Optional<PersonAliasDao> fetchByNameForPerson(Long personId, String aliasName);

    @Query("SELECT a FROM PersonAliasDao a WHERE a.person.id = ?1 AND a.hidden = false ORDER BY a.oldName")
    List<PersonAliasDao> findVisibleForPersonId(Long id);
}
