package uk.co.bbr.services.people;

import uk.co.bbr.services.people.dao.PersonAlternativeNameDao;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.people.dto.PeopleListDto;

import java.util.List;

public interface PeopleService {
    PersonDao create(PersonDao person);

    void createAlternativeName(PersonDao person, PersonAlternativeNameDao previousName);

    PersonDao fetchBySlug(String personSlug);

    PersonDao fetchById(long personId);

    List<PersonAlternativeNameDao> fetchAlternateNames(PersonDao person);

    PeopleListDto listPeopleStartingWith(String prefix);
}
