package uk.co.bbr.services.people;

import uk.co.bbr.services.people.dao.PersonAliasDao;
import uk.co.bbr.services.people.dao.PersonDao;

import java.util.List;
import java.util.Optional;

public interface PersonAliasService {
    PersonAliasDao createAlias(PersonDao person, PersonAliasDao previousName);
    PersonAliasDao migrateAlias(PersonDao person, PersonAliasDao previousName);

    List<PersonAliasDao> findAllAliases(PersonDao person);
    List<PersonAliasDao> findVisibleAliases(PersonDao person);

    Optional<PersonAliasDao> aliasExists(PersonDao person, String aliasName);

    void deleteAlias(PersonDao person, Long aliasId);

    void showAlias(PersonDao person, Long aliasId);

    void hideAlias(PersonDao person, Long aliasId);
}
