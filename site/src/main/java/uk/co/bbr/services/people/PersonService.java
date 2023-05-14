package uk.co.bbr.services.people;

import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.people.dao.PersonAliasDao;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.people.dto.PeopleListDto;
import uk.co.bbr.services.people.sql.dto.PeopleWinnersSqlDto;

import java.time.LocalDate;
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

    PersonDao findMatchingPersonByName(String personName, BandDao band, LocalDate dateContext);

    List<PeopleWinnersSqlDto> fetchContestWinningPeople();

    List<PeopleWinnersSqlDto> fetchContestWinningPeopleBefore(int year);

    List<PeopleWinnersSqlDto> fetchContestWinningPeopleAfter(int year);
}
