package uk.co.bbr.services.people;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.events.dao.ContestAdjudicatorDao;
import uk.co.bbr.services.events.dao.ContestResultDao;
import uk.co.bbr.services.events.repo.ContestAdjudicatorRepository;
import uk.co.bbr.services.framework.ValidationException;
import uk.co.bbr.services.framework.mixins.SlugTools;
import uk.co.bbr.services.people.dao.PersonAliasDao;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.people.dao.PersonProfileDao;
import uk.co.bbr.services.people.dao.PersonRelationshipDao;
import uk.co.bbr.services.people.dto.ConductorCompareDto;
import uk.co.bbr.services.people.dto.PeopleListDto;
import uk.co.bbr.services.people.repo.PersonAliasRepository;
import uk.co.bbr.services.people.repo.PersonProfileRepository;
import uk.co.bbr.services.people.repo.PersonRelationshipRepository;
import uk.co.bbr.services.people.repo.PersonRepository;
import uk.co.bbr.services.people.sql.AdjudicatorSql;
import uk.co.bbr.services.people.sql.PeopleBandsSql;
import uk.co.bbr.services.people.sql.PeopleCompareSql;
import uk.co.bbr.services.people.sql.PeopleCountSql;
import uk.co.bbr.services.people.sql.PeopleWinnersSql;
import uk.co.bbr.services.people.sql.dto.AdjudicationsSqlDto;
import uk.co.bbr.services.people.sql.dto.CompareConductorsSqlDto;
import uk.co.bbr.services.people.sql.dto.PeopleBandsSqlDto;
import uk.co.bbr.services.people.sql.dto.PeopleListSqlDto;
import uk.co.bbr.services.people.sql.dto.PeopleWinnersSqlDto;
import uk.co.bbr.services.people.sql.dto.UserAdjudicationsSqlDto;
import uk.co.bbr.services.pieces.repo.PieceRepository;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.dao.SiteUserDao;
import uk.co.bbr.web.security.annotations.IsBbrMember;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PersonServiceImpl implements PersonService, SlugTools {

    private final SecurityService securityService;

    private final EntityManager entityManager;

    private final PersonRepository personRepository;
    private final PersonAliasRepository personAliasRepository;
    private final PersonRelationshipRepository personRelationshipRepository;
    private final ContestAdjudicatorRepository contestAdjudicatorRepository;
    private final PersonProfileRepository personProfileRepository;
    private final PieceRepository pieceRepository;

    @Override
    @IsBbrMember
    public PersonDao create(PersonDao person) {
        this.validateMandatory(person);

        // validation
        if (person.getId() != null) {
            throw new ValidationException("Can't create with specific id");
        }

        // does the slug already exist?
        Optional<PersonDao> slugMatches = this.personRepository.fetchBySlug(person.getSlug());
        if (slugMatches.isPresent()) {
            throw new ValidationException("Person with slug " + person.getSlug() + " already exists.");
        }

        person.setCreated(LocalDateTime.now());
        person.setCreatedBy(this.securityService.getCurrentUsername());
        person.setUpdated(LocalDateTime.now());
        person.setUpdatedBy(this.securityService.getCurrentUsername());

        return this.personRepository.saveAndFlush(person);
    }

    private void validateMandatory(PersonDao person) {
        if (StringUtils.isBlank(person.getSurname())) {
            throw new ValidationException("Person surname must be specified");
        }

        // defaults
        if (StringUtils.isBlank(person.getSlug())) {
            person.setSlug(slugify(person.getName()));
        }
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
    public PersonDao update(PersonDao person) {
        this.validateMandatory(person);

        // validation
        if (person.getId() == null) {
            throw new ValidationException("Can't update without an id");
        }

        // does the slug already exist?
        Optional<PersonDao> slugMatches = this.personRepository.fetchBySlug(person.getSlug());
        if (slugMatches.isPresent() && !slugMatches.get().getId().equals(person.getId())) {
            throw new ValidationException("Person with slug " + person.getSlug() + " already exists.");
        }

        person.setUpdated(LocalDateTime.now());
        person.setUpdatedBy(this.securityService.getCurrentUsername());
        return this.personRepository.saveAndFlush(person);
    }

    @Override
    public PersonProfileDao update(PersonProfileDao personProfile) {
        return this.personProfileRepository.saveAndFlush(personProfile);
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
    @Cacheable(cacheNames = "peopleStartingWith", key = "#prefix", cacheManager = "caffeineCacheManager")
    public PeopleListDto listPeopleStartingWith(String prefix) {
        if (prefix.strip().length() != 1) {
            throw new UnsupportedOperationException("Prefix must be a single character");
        }

        List<PeopleListSqlDto> sqlResults = PeopleCountSql.selectPeopleWhereSurnameStartsWithLetterForList(this.entityManager, prefix);

        List<PersonDao> peopleToReturn = new ArrayList<>();
        for (PeopleListSqlDto eachPerson : sqlResults) {
            peopleToReturn.add(eachPerson.asPerson());
        }

        long allBandsCount = this.personRepository.count();

        return new PeopleListDto(peopleToReturn.size(), allBandsCount, prefix, peopleToReturn);
    }

     @Override
    public int fetchAdjudicationCount(PersonDao person) {
        return this.contestAdjudicatorRepository.fetchAdjudicationCountForPerson(person.getId());
    }

    @Override
    public List<ContestAdjudicatorDao> fetchAdjudications(PersonDao person) {
        List<AdjudicationsSqlDto> adjudicationsSql = AdjudicatorSql.fetchAdjudications(this.entityManager, person.getId());

        List<ContestAdjudicatorDao> result = new ArrayList<>();
        for (AdjudicationsSqlDto adjudication : adjudicationsSql) {
            result.add(adjudication.buildAdjudicationDao());
        }
        return result;
    }

    @Override
    public int fetchComposerCount(PersonDao person) {
        return this.pieceRepository.fetchComposerCountForPerson(person.getId());
    }

    @Override
    public int fetchArrangerCount(PersonDao person) {
        return this.pieceRepository.fetchArrangerCountForPerson(person.getId());
    }

    @Override
    public List<PeopleWinnersSqlDto> fetchContestWinningPeople() {
        return PeopleWinnersSql.selectWinningPeople(this.entityManager);
    }

    @Override
    public List<PeopleWinnersSqlDto> fetchContestWinningPeopleBefore(int year) {
        return PeopleWinnersSql.selectWinningPeopleBefore(this.entityManager, year);
    }

    @Override
    public List<PeopleWinnersSqlDto> fetchContestWinningPeopleAfter(int year) {
        return PeopleWinnersSql.selectWinningPeopleAfter(this.entityManager, year);
    }

    @Override
    public List<PeopleBandsSqlDto> fetchBandsConductedList() {
        return PeopleBandsSql.selectWinningPeople(this.entityManager);
    }

    @Override
    public List<PeopleBandsSqlDto> fetchBandsConductedListBefore(int year) {
        return PeopleBandsSql.selectWinningPeopleBefore(this.entityManager, year);
    }

    @Override
    public List<PeopleBandsSqlDto> fetchBandsConductedListAfter(int year) {
        return PeopleBandsSql.selectWinningPeopleAfter(this.entityManager, year);
    }

    @Override
    public ConductorCompareDto compareConductors(PersonDao leftPerson, PersonDao rightPerson) {
        List<CompareConductorsSqlDto> results = PeopleCompareSql.compareConductors(this.entityManager, leftPerson.getId(), rightPerson.getId());
        return new ConductorCompareDto(results);
    }

    @Override
    public List<ContestResultDao> fetchPersonalAdjudications(SiteUserDao currentUser, PersonDao person) {
        List<UserAdjudicationsSqlDto> adjudicationsSql = AdjudicatorSql.fetchUserAdjudications(this.entityManager, currentUser.getUsercode(), person.getId());

        List<ContestResultDao> result = new ArrayList<>();
        for (UserAdjudicationsSqlDto adjudication : adjudicationsSql) {
            result.add(adjudication.buildContestResultDao());
        }
        return result;
    }

    @Override
    public void delete(PersonDao person) {
        List<PersonAliasDao> aliases = this.personAliasRepository.findForPersonId(person.getId());
        this.personAliasRepository.deleteAll(aliases);

        List<PersonRelationshipDao> relationships = this.personRelationshipRepository.findForPerson(person.getId());
        this.personRelationshipRepository.deleteAll(relationships);

        this.personRepository.delete(person);
    }

    @Override
    public List<PersonProfileDao> fetchAllProfiles() {
        return this.personProfileRepository.fetchAll();
    }

    @Override
    public List<PersonProfileDao> fetchProfilesForOwner(String ownerUsername) {
        return this.personProfileRepository.fetchForOwner(ownerUsername);
    }

    @Override
    public Optional<PersonProfileDao> fetchProfileByPersonSlugAndOwner(String personSlug, SiteUserDao user) {
        if (user.isSuperuser()) {
            return this.personProfileRepository.fetchProfileForPersonSlug(personSlug);
        }
        return this.personProfileRepository.fetchProfileForPersonSlugAndUser(personSlug, user.getUsercode());
    }

    @Override
    public PeopleListDto listPeopleDoneNothing() {
        List<PeopleListSqlDto> sqlResults = PeopleCountSql.selectPeopleWhoHaveDoneNothing(this.entityManager);

        List<PersonDao> peopleToReturn = new ArrayList<>();
        for (PeopleListSqlDto eachPerson : sqlResults) {
            peopleToReturn.add(eachPerson.asPerson());
        }

        long allBandsCount = this.personRepository.count();

        return new PeopleListDto(peopleToReturn.size(), allBandsCount, "NOTHING", peopleToReturn);
    }

    @Override
    public int fetchUserAdjudicationsCount(SiteUserDao user, PersonDao person) {
        if (user == null) {
            return 0;
        }
        return this.fetchPersonalAdjudications(user, person).size();
    }


}
