package uk.co.bbr.services.people;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import uk.co.bbr.services.framework.ValidationException;
import uk.co.bbr.services.framework.mixins.SlugTools;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.people.dao.PersonRelationshipDao;
import uk.co.bbr.services.people.dao.PersonRelationshipTypeDao;
import uk.co.bbr.services.people.repo.PersonRelationshipRepository;
import uk.co.bbr.services.people.repo.PersonRelationshipTypeRepository;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.web.security.annotations.IsBbrAdmin;
import uk.co.bbr.web.security.annotations.IsBbrMember;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PersonRelationshipServiceImpl implements PersonRelationshipService, SlugTools {

    private final SecurityService securityService;

    private final PersonRelationshipRepository personRelationshipRepository;
    private final PersonRelationshipTypeRepository personRelationshipTypeRepository;

    @Override
    @IsBbrMember
    public PersonRelationshipDao createRelationship(PersonRelationshipDao relationship) {
        if (relationship.getId() != null) {
            throw new ValidationException("ID must not be supplied to create");
        }

        return this.createRelationship(relationship, false);
    }

    @Override
    public PersonRelationshipDao updateRelationship(PersonRelationshipDao relationship) {
        if (relationship.getId() == null) {
            throw new ValidationException("ID required to update");
        }

        relationship.setUpdated(LocalDateTime.now());
        relationship.setUpdatedBy(this.securityService.getCurrentUsername());

        return this.saveRelationship(relationship);
    }

    @Override
    @IsBbrAdmin
    public PersonRelationshipDao migrateRelationship(PersonRelationshipDao relationship) {
        return this.createRelationship(relationship, true);
    }

    private PersonRelationshipDao createRelationship(PersonRelationshipDao relationship, boolean migrating) {
        if (!migrating) {
            relationship.setCreated(LocalDateTime.now());
            relationship.setCreatedBy(this.securityService.getCurrentUsername());
            relationship.setUpdated(LocalDateTime.now());
            relationship.setUpdatedBy(this.securityService.getCurrentUsername());
        }

        return this.saveRelationship(relationship);
    }

    private PersonRelationshipDao saveRelationship(PersonRelationshipDao relationship){
        return this.personRelationshipRepository.saveAndFlush(relationship);
    }

    @Override
    public List<PersonRelationshipDao> fetchRelationshipsForPerson(PersonDao band) {
        return this.personRelationshipRepository.findForPerson(band.getId());
    }

    @Override
    public Optional<PersonRelationshipDao> fetchById(Long relationshipId) {
        return this.personRelationshipRepository.fetchById(relationshipId);
    }

    @Override
    public void deleteRelationship(PersonRelationshipDao personRelationship) {
        this.personRelationshipRepository.delete(personRelationship);
    }

    @Override
    public Optional<PersonRelationshipTypeDao> fetchTypeById(long relationshipTypeId) {
        return this.personRelationshipTypeRepository.findById(relationshipTypeId);
    }

    @Override
    public List<PersonRelationshipTypeDao> listTypes() {
        return this.personRelationshipTypeRepository.findAllOrderByName();
    }

    @Override
    public PersonRelationshipTypeDao fetchTypeByName(String typeName) {
        return this.personRelationshipTypeRepository.fetchByName(typeName);
    }
}
