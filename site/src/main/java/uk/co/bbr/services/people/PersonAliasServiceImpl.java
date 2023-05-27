package uk.co.bbr.services.people;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.contests.ContestResultService;
import uk.co.bbr.services.contests.repo.ContestAdjudicatorRepository;
import uk.co.bbr.services.framework.ValidationException;
import uk.co.bbr.services.framework.mixins.SlugTools;
import uk.co.bbr.services.people.dao.PersonAliasDao;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.people.dto.PeopleListDto;
import uk.co.bbr.services.people.repo.PersonAliasRepository;
import uk.co.bbr.services.people.repo.PersonRepository;
import uk.co.bbr.services.people.sql.PeopleBandsSql;
import uk.co.bbr.services.people.sql.PeopleCountSql;
import uk.co.bbr.services.people.sql.PeopleWinnersSql;
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
public class PersonAliasServiceImpl implements PersonAliasService, SlugTools {
    private final PersonAliasRepository personAliasRepository;
    private final SecurityService securityService;

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
            previousName.setCreatedBy(this.securityService.getCurrentUsername());
            previousName.setUpdated(LocalDateTime.now());
            previousName.setUpdatedBy(this.securityService.getCurrentUsername());
        }
        this.personAliasRepository.saveAndFlush(previousName);
    }

    @Override
    public List<PersonAliasDao> findAllAliases(PersonDao person) {
        return this.personAliasRepository.findForPersonId(person.getId());
    }

    @Override
    public List<PersonAliasDao> findVisibleAliases(PersonDao person) {
        return this.personAliasRepository.findVisibleForPersonId(person.getId());
    }

    @Override
    public Optional<PersonAliasDao> aliasExists(PersonDao person, String aliasName) {
        String name = person.simplifyPersonFullName(aliasName);
        return this.personAliasRepository.fetchByNameForPerson(person.getId(), name);
    }
}
