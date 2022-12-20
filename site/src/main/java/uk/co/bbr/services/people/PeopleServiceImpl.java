package uk.co.bbr.services.people;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.services.framework.ValidationException;
import uk.co.bbr.services.framework.mixins.SlugTools;
import uk.co.bbr.services.people.dao.PersonAlternativeNameDao;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.people.dto.PeopleListDto;
import uk.co.bbr.services.people.repo.PersonAlternativeNameRepository;
import uk.co.bbr.services.people.repo.PersonRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PeopleServiceImpl implements PeopleService, SlugTools {

    private final PersonRepository personRepository;
    private final PersonAlternativeNameRepository personAlternativeNameRepository;

    @Override
    public PersonDao create(PersonDao person) {
        // validation
        if (person.getId() != null) {
            throw new ValidationException("Can't create with specific id");
        }

        if (person.getSurname() == null || person.getSurname().trim().length() == 0) {
            throw new ValidationException("Person surname must be specified");
        }

        // defaults
        if (person.getSlug() == null || person.getSlug().trim().length() == 0) {
            person.setSlug(slugify(person.getName()));
        }

        return this.personRepository.saveAndFlush(person);
    }

    @Override
    public PersonDao create(String surname, String firstNames) {
        PersonDao person = new PersonDao();
        person.setSurname(surname);
        person.setFirstNames(firstNames);
        return this.create(person);
    }

    @Override
    public void createAlternativeName(PersonDao person, PersonAlternativeNameDao previousName) {
        previousName.setPerson(person);
        this.personAlternativeNameRepository.saveAndFlush(previousName);
    }

    @Override
    public PersonDao fetchBySlug(String personSlug) {
        Optional<PersonDao> matchedPerson = this.personRepository.fetchBySlug(personSlug);
        if (matchedPerson.isEmpty()) {
            throw new NotFoundException("Person with slug " + personSlug + " not found");
        }
        return matchedPerson.get();
    }

    @Override
    public PersonDao fetchById(long personId) {
        Optional<PersonDao> matchedPerson = this.personRepository.fetchById(personId);
        if (matchedPerson.isEmpty()) {
            throw new NotFoundException("Person with id " + personId + " not found");
        }
        return matchedPerson.get();
    }

    @Override
    public List<PersonAlternativeNameDao> fetchAlternateNames(PersonDao person) {
        return this.personAlternativeNameRepository.findForPersonId(person.getId());
    }

    @Override
    public PeopleListDto listPeopleStartingWith(String prefix) {
        List<PersonDao> peopleToReturn;

        switch (prefix.toUpperCase()) {
            case "ALL" -> peopleToReturn = this.personRepository.findAll();
            default -> {
                if (prefix.trim().length() != 1) {
                    throw new UnsupportedOperationException("Prefix must be a single character");
                }
                peopleToReturn = this.personRepository.findByPrefix(prefix.trim().toUpperCase());
            }
        }

        long allBandsCount = this.personRepository.count();

        return new PeopleListDto(peopleToReturn.size(), allBandsCount, prefix, peopleToReturn);
    }


}
