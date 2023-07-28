package uk.co.bbr.services.lookup;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.framework.mixins.SlugTools;
import uk.co.bbr.services.lookup.sql.dto.FinderSqlDto;
import uk.co.bbr.services.lookup.sql.FinderSql;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonFinderServiceImpl implements PersonFinderService, SlugTools {

    private final EntityManager entityManager;


    @Override
    public String findMatchByName(String personName, String bandSlug, LocalDate dateContext) {
        String personNameUpper = personName.toUpperCase();
        if (bandSlug != null) {
           List<FinderSqlDto> previousConductorsForThisBand = FinderSql.fetchBandConductors(this.entityManager, bandSlug, personNameUpper);
            for (FinderSqlDto eachExistingConductor : previousConductorsForThisBand) {
                if (this.withinDates(eachExistingConductor, dateContext)) {
                    return eachExistingConductor.getSlug();
                }
            }
        }

        List<FinderSqlDto> matchingPeople = FinderSql.personFetchByCombinedNameUpper(this.entityManager, personNameUpper);
        for (FinderSqlDto eachPerson : matchingPeople) {
            if (this.withinDates(eachPerson, dateContext)) {
                return eachPerson.getSlug();
            }
        }

        List<FinderSqlDto> matchingAliases = FinderSql.personAliasFetchByUpperName(this.entityManager, personNameUpper);
        for (FinderSqlDto eachAlias : matchingAliases) {
            if (this.withinDates(eachAlias, dateContext)) {
                return eachAlias.getSlug();
            }
        }

        if (personName.charAt(1) == '.') {
            String initialUpper = personName.substring(0, 1).toUpperCase();
            String surnameUpper = personName.substring(personName.lastIndexOf(" ")).trim().toUpperCase();

            List<FinderSqlDto> matchingInitialPeople = FinderSql.personFetchByInitialAndSurname(this.entityManager, initialUpper, surnameUpper);
            for (FinderSqlDto eachPerson : matchingInitialPeople) {
                if (this.withinDates(eachPerson, dateContext)) {
                    return eachPerson.getSlug();
                }
            }
        }

        return null;
    }

    private boolean withinDates(FinderSqlDto matchingPerson, LocalDate dateContext) {
        if (matchingPerson.getStartDate() != null && dateContext.isBefore(matchingPerson.getStartDate())) {
            return false;
        }

        if (matchingPerson.getEndDate() != null && dateContext.isAfter(matchingPerson.getEndDate())) {
            return false;
        }

        return true;
    }
}
