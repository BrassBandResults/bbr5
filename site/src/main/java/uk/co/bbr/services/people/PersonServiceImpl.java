package uk.co.bbr.services.people;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.services.framework.ValidationException;
import uk.co.bbr.services.framework.mixins.SlugTools;
import uk.co.bbr.services.people.dao.PersonAliasDao;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.people.dto.PeopleListDto;
import uk.co.bbr.services.people.repo.PersonAliasRepository;
import uk.co.bbr.services.people.repo.PersonRepository;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.web.security.annotations.IsBbrAdmin;
import uk.co.bbr.web.security.annotations.IsBbrMember;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PersonServiceImpl implements PersonService, SlugTools {

    private final PersonRepository personRepository;
    private final PersonAliasRepository personAliasRepository;
    private final SecurityService securityService;

    @Override
    @IsBbrMember
    public PersonDao create(PersonDao person) {
        return this.create(person, false);
    }

    @Override
    @IsBbrAdmin
    public PersonDao migrate(PersonDao person) {
        return this.create(person, true);
    }

    private PersonDao create(PersonDao person, boolean migrating) {
        // validation
        if (person.getId() != null) {
            throw new ValidationException("Can't create with specific id");
        }

        if (StringUtils.isBlank(person.getSurname())) {
            throw new ValidationException("Person surname must be specified");
        }

        // defaults
        if (StringUtils.isBlank(person.getSlug())) {
            person.setSlug(slugify(person.getName()));
        }

        // does the slug already exist?
        Optional<PersonDao> slugMatches = this.personRepository.fetchBySlug(person.getSlug());
        if (slugMatches.isPresent()) {
            throw new ValidationException("Person with slug " + person.getSlug() + " already exists.");
        }

        if (!migrating) {
            person.setCreated(LocalDateTime.now());
            person.setCreatedBy(this.securityService.getCurrentUser());
            person.setUpdated(LocalDateTime.now());
            person.setUpdatedBy(this.securityService.getCurrentUser());
        }
        return this.personRepository.saveAndFlush(person);
    }

    @Override
    @IsBbrMember
    public PersonDao create(String surname, String firstNames) {
        PersonDao person = new PersonDao();
        person.setSurname(surname);
        person.setFirstNames(firstNames);
        return this.create(person);
    }

    @Override
    @IsBbrMember
    public void createAlternativeName(PersonDao person, PersonAliasDao previousName) {
        this.createAlternativeName(person, previousName, false);
    }

    @Override
    @IsBbrAdmin
    public void migrateAlternativeName(PersonDao person, PersonAliasDao previousName) {
        this.createAlternativeName(person, previousName, true);
    }

    private void createAlternativeName(PersonDao person, PersonAliasDao previousName, boolean migrating) {
        previousName.setPerson(person);
        if (!migrating) {
            previousName.setCreated(LocalDateTime.now());
            previousName.setCreatedBy(this.securityService.getCurrentUser());
            previousName.setUpdated(LocalDateTime.now());
            previousName.setUpdatedBy(this.securityService.getCurrentUser());
        }
        this.personAliasRepository.saveAndFlush(previousName);
    }

    @Override
    public Optional<PersonDao> fetchBySlug(String personSlug) {
        return this.personRepository.fetchBySlug(personSlug);
    }

    @Override
    public Optional<PersonDao> fetchById(long personId) {
        return this.personRepository.fetchById(personId);
    }

    @Override
    public List<PersonAliasDao> findAlternateNames(PersonDao person) {
        return this.personAliasRepository.findForPersonId(person.getId());
    }

    @Override
    public PeopleListDto listPeopleStartingWith(String prefix) {
        List<PersonDao> peopleToReturn;

        switch (prefix.toUpperCase()) {
            case "ALL" -> peopleToReturn = this.personRepository.findAllOrderBySurname();
            default -> {
                if (prefix.trim().length() != 1) {
                    throw new UnsupportedOperationException("Prefix must be a single character");
                }
                peopleToReturn = this.personRepository.findByPrefixOrderBySurname(prefix.trim().toUpperCase());
            }
        }

        long allBandsCount = this.personRepository.count();

        return new PeopleListDto(peopleToReturn.size(), allBandsCount, prefix, peopleToReturn);
    }

    @Override
    public Optional<PersonAliasDao> aliasExists(PersonDao person, String aliasName) {
        String name = person.simplifyName(aliasName);
        return this.personAliasRepository.fetchByNameForPerson(person.getId(), name);
    }


}
