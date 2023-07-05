package uk.co.bbr.services.people;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.events.ResultService;
import uk.co.bbr.services.events.repo.ContestAdjudicatorRepository;
import uk.co.bbr.services.framework.ValidationException;
import uk.co.bbr.services.framework.mixins.SlugTools;
import uk.co.bbr.services.people.dao.PersonAliasDao;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.people.dto.ConductorCompareDto;
import uk.co.bbr.services.people.dto.PeopleListDto;
import uk.co.bbr.services.people.repo.PersonAliasRepository;
import uk.co.bbr.services.people.repo.PersonRepository;
import uk.co.bbr.services.people.sql.PeopleBandsSql;
import uk.co.bbr.services.people.sql.PeopleCompareSql;
import uk.co.bbr.services.people.sql.PeopleCountSql;
import uk.co.bbr.services.people.sql.PeopleWinnersSql;
import uk.co.bbr.services.people.sql.dto.CompareConductorsSqlDto;
import uk.co.bbr.services.people.sql.dto.PeopleBandsSqlDto;
import uk.co.bbr.services.people.sql.dto.PeopleWinnersSqlDto;
import uk.co.bbr.services.pieces.repo.PieceRepository;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.web.security.annotations.IsBbrAdmin;
import uk.co.bbr.web.security.annotations.IsBbrMember;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
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
                return eachExistingConductor;
            }
        }

        Optional<PersonDao> matchingPerson = this.personRepository.fetchByCombinedName(personName.toUpperCase());

        if (matchingPerson.isPresent()) {
            return matchingPerson.get();
        }

        Optional<PersonAliasDao> matchingAlias = this.personAliasRepository.fetchByUpperName(personName.toUpperCase());
        return matchingAlias.map(PersonAliasDao::getPerson).orElse(null);
    }
}
