package uk.co.bbr.services.people.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.co.bbr.services.people.dao.PersonAliasDao;

import java.util.List;
import java.util.Optional;

public interface PersonAliasRepository extends JpaRepository<PersonAliasDao, Long> {
    @Query("SELECT a FROM PersonAliasDao a WHERE a.person.id = :personId ORDER BY a.oldName")
    List<PersonAliasDao> findForPersonId(Long personId);

    @Query("SELECT a FROM PersonAliasDao a WHERE a.person.id = :personId AND a.oldName = :aliasName")
    Optional<PersonAliasDao> fetchByNameForPerson(Long personId, String aliasName);

    @Query("SELECT a FROM PersonAliasDao a WHERE a.person.id = :personId AND a.hidden = false ORDER BY a.oldName")
    List<PersonAliasDao> findVisibleForPersonId(Long personId);

    @Query("SELECT a FROM PersonAliasDao a WHERE UPPER(a.oldName) = :upperCaseName")
    Optional<PersonAliasDao> fetchByUpperName(String upperCaseName);

    @Query("SELECT a FROM PersonAliasDao a WHERE a.person.id = :personId AND a.id = :aliasId")
    Optional<PersonAliasDao> fetchByIdForPerson(Long personId, Long aliasId);
}
