package uk.co.bbr.services.people;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.events.ResultService;
import uk.co.bbr.services.framework.mixins.SlugTools;
import uk.co.bbr.services.people.dao.PersonAliasDao;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.people.repo.PersonAliasRepository;
import uk.co.bbr.services.people.repo.PersonRepository;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PersonFinderServiceImpl implements PersonFinderService, SlugTools {

    private final ResultService contestResultService;
    private final PersonRepository personRepository;
    private final PersonAliasRepository personAliasRepository;
    private final EntityManager entityManager;


    @Override
    public PersonDao findMatchByName(String personName, BandDao band, LocalDate dateContext) {
        Set<PersonDao> previousConductorsForThisBand = this.contestResultService.fetchBandConductors(band);
        for (PersonDao eachExistingConductor : previousConductorsForThisBand) {
            if (eachExistingConductor.matchesName(personName)) {
                if (this.withinDates(eachExistingConductor, dateContext)) {
                    return eachExistingConductor;
                }
            }
        }

        List<PersonDao> matchingPeople = this.personRepository.fetchByCombinedNameUpper(personName.toUpperCase());
        for (PersonDao eachPerson : matchingPeople) {
            if (this.withinDates(eachPerson, dateContext)) {
                return eachPerson;
            }
        }

        List<PersonAliasDao> matchingAliases = this.personAliasRepository.fetchByUpperName(personName.toUpperCase());
        for (PersonAliasDao eachAlias : matchingAliases) {
            PersonDao person = eachAlias.getPerson();
            if (this.withinDates(person, dateContext)) {
                return person;
            }
        }

        if (personName.charAt(1) == '.') {
            String initialUpper = personName.substring(0, 1).toUpperCase();
            String surnameUpper = personName.substring(personName.lastIndexOf(" ")).trim().toUpperCase();

            List<PersonDao> matchingInitialPeople = this.personRepository.fetchByInitialAndSurname(initialUpper + "%", surnameUpper);
            for (PersonDao eachPerson : matchingInitialPeople) {
                if (this.withinDates(eachPerson, dateContext)) {
                    return eachPerson;
                }
            }
        }




        return null;
    }

    private boolean withinDates(PersonDao matchingPerson, LocalDate dateContext) {
        if (matchingPerson.getStartDate() != null && dateContext.isBefore(matchingPerson.getStartDate())) {
            return false;
        }

        if (matchingPerson.getEndDate() != null && dateContext.isAfter(matchingPerson.getEndDate())) {
            return false;
        }

        return true;
    }
}
