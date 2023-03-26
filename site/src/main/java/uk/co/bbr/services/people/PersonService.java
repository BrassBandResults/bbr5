package uk.co.bbr.services.people;

import uk.co.bbr.services.people.dao.PersonAliasDao;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.people.dto.PeopleListDto;

import java.util.List;
import java.util.Optional;

public interface PersonService {
    PersonDao create(PersonDao person);
    PersonDao migrate(PersonDao person);
    PersonDao create(String surname, String firstNames);

    void createAlternativeName(PersonDao person, PersonAliasDao previousName);
    void migrateAlternativeName(PersonDao person, PersonAliasDao previousName);

    Optional<PersonDao> fetchBySlug(String personSlug);

    Optional<PersonDao> fetchById(long personId);

    List<PersonAliasDao> findAllAliases(PersonDao person);
    List<PersonAliasDao> findVisibleAliases(PersonDao person);

    PeopleListDto listPeopleStartingWith(String prefix);

    Optional<PersonAliasDao> aliasExists(PersonDao person, String aliasName);


    int fetchAdjudicationCount(PersonDao person);

    int fetchComposerCount(PersonDao person);

    int fetchArrangerCount(PersonDao person);
}
