package uk.co.bbr.services.people;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.services.framework.mixins.SlugTools;
import uk.co.bbr.services.people.dao.PersonAliasDao;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.people.repo.PersonAliasRepository;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.web.security.annotations.IsBbrAdmin;
import uk.co.bbr.web.security.annotations.IsBbrMember;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PersonAliasServiceImpl implements PersonAliasService, SlugTools {
    private final PersonAliasRepository personAliasRepository;
    private final SecurityService securityService;

    @Override
    @IsBbrMember
    public PersonAliasDao createAlias(PersonDao person, PersonAliasDao previousName) {
        return this.createAlternativeName(person, previousName, false);
    }

    private PersonAliasDao createAlternativeName(PersonDao person, PersonAliasDao previousName, boolean migrating) {
        previousName.setPerson(person);
        if (!migrating) {
            previousName.setCreated(LocalDateTime.now());
            previousName.setCreatedBy(this.securityService.getCurrentUsername());
            previousName.setUpdated(LocalDateTime.now());
            previousName.setUpdatedBy(this.securityService.getCurrentUsername());
        }
        return this.personAliasRepository.saveAndFlush(previousName);
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

    @Override
    public void showAlias(PersonDao band, Long aliasId) {
        Optional<PersonAliasDao> previousName = this.personAliasRepository.fetchByIdForPerson(band.getId(), aliasId);
        if (previousName.isEmpty()) {
            throw NotFoundException.bandAliasNotFoundByIds(band.getSlug(), aliasId);
        }
        previousName.get().setHidden(false);
        this.personAliasRepository.saveAndFlush(previousName.get());
    }

    @Override
    public void hideAlias(PersonDao band, Long aliasId) {
        Optional<PersonAliasDao> previousName = this.personAliasRepository.fetchByIdForPerson(band.getId(), aliasId);
        if (previousName.isEmpty()) {
            throw NotFoundException.bandAliasNotFoundByIds(band.getSlug(), aliasId);
        }
        previousName.get().setHidden(true);
        this.personAliasRepository.saveAndFlush(previousName.get());
    }

    @Override
    public void deleteAlias(PersonDao band, Long aliasId) {
        Optional<PersonAliasDao> previousName = this.personAliasRepository.fetchByIdForPerson(band.getId(), aliasId);
        if (previousName.isEmpty()) {
            throw NotFoundException.bandAliasNotFoundByIds(band.getSlug(), aliasId);
        }
        this.personAliasRepository.delete(previousName.get());

    }
}
