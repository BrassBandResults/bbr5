package uk.co.bbr.services.people;

import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.people.dao.PersonAliasDao;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.people.dto.PeopleListDto;
import uk.co.bbr.services.people.sql.dto.PeopleBandsSqlDto;
import uk.co.bbr.services.people.sql.dto.PeopleWinnersSqlDto;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PersonAliasService {
    void createAlternativeName(PersonDao person, PersonAliasDao previousName);
    void migrateAlternativeName(PersonDao person, PersonAliasDao previousName);

    List<PersonAliasDao> findAllAliases(PersonDao person);
    List<PersonAliasDao> findVisibleAliases(PersonDao person);

    Optional<PersonAliasDao> aliasExists(PersonDao person, String aliasName);
}
