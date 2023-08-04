package uk.co.bbr.services.people;

import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.people.dao.PersonRelationshipDao;
import uk.co.bbr.services.people.dao.PersonRelationshipTypeDao;

import java.util.List;
import java.util.Optional;

public interface PersonRelationshipService {

    PersonRelationshipDao createRelationship(PersonRelationshipDao relationship);
    PersonRelationshipDao createRelationship(PersonDao leftPerson, PersonDao rightPerson, PersonRelationshipTypeDao relationship);

    List<PersonRelationshipDao> fetchRelationshipsForPerson(PersonDao band);
    Optional<PersonRelationshipDao> fetchById(Long relationshipId);
    void deleteRelationship(PersonRelationshipDao personRelationship);
    Optional<PersonRelationshipTypeDao> fetchTypeById(long relationshipTypeId);
    List<PersonRelationshipTypeDao> listTypes();

    PersonRelationshipTypeDao fetchTypeByName(String typeName);
}
