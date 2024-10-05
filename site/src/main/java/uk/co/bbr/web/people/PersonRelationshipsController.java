package uk.co.bbr.web.people;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.services.people.PersonRelationshipService;
import uk.co.bbr.services.people.PersonService;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.people.dao.PersonRelationshipDao;
import uk.co.bbr.services.people.dao.PersonRelationshipTypeDao;
import uk.co.bbr.web.security.annotations.IsBbrMember;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class PersonRelationshipsController {

    private final PersonService personService;
    private final PersonRelationshipService personRelationshipService;

    private static final String REDIRECT_TO_PERSON_RELATIONSHIPS = "redirect:/people/{personSlug}/edit-relationships";

    @IsBbrMember
    @GetMapping("/people/{personSlug:[\\-_a-z\\d]{2,}}/edit-relationships")
    public String personRelationshipsEdit(Model model, @PathVariable("personSlug") String personSlug) {
        Optional<PersonDao> person = this.personService.fetchBySlug(personSlug);
        if (person.isEmpty()) {
            throw NotFoundException.personNotFoundBySlug(personSlug);
        }

        List<PersonRelationshipDao> relationships = this.personRelationshipService.fetchRelationshipsForPerson(person.get());
        List<PersonRelationshipTypeDao> relationshipTypes = this.personRelationshipService.listTypes();

        model.addAttribute("Person", person.get());
        model.addAttribute("PersonRelationships", relationships);
        model.addAttribute("RelationshipTypes", relationshipTypes);
        return "people/person-relationships";
    }



    @IsBbrMember
    @GetMapping("/people/{personSlug:[\\-_a-z\\d]{2,}}/edit-relationships/{relationshipId:\\d+}/delete")
    public String personRelationshipsDelete(@PathVariable("personSlug") String personSlug, @PathVariable("relationshipId") Long relationshipId) {
        Optional<PersonDao> person = this.personService.fetchBySlug(personSlug);
        if (person.isEmpty()) {
            throw NotFoundException.personNotFoundBySlug(personSlug);
        }

        Optional<PersonRelationshipDao> relationship = this.personRelationshipService.fetchById(relationshipId);
        if (relationship.isEmpty()) {
            throw NotFoundException.relationshipNotFoundById(relationshipId);
        }

        this.personRelationshipService.deleteRelationship(relationship.get());

        return REDIRECT_TO_PERSON_RELATIONSHIPS;
    }

    @IsBbrMember
    @PostMapping("/people/{personSlug:[\\-_a-z\\d]{2,}}/edit-relationships/add")
    public String personRelationshipsCreate(@PathVariable("personSlug") String personSlug, @RequestParam("RightPersonSlug") String rightPersonSlug,
                                                                                           @RequestParam("RelationshipTypeId") String relationshipTypeId) {
        Optional<PersonDao> leftPerson = this.personService.fetchBySlug(personSlug);
        if (leftPerson.isEmpty()) {
            throw NotFoundException.personNotFoundBySlug(personSlug);
        }

        Optional<PersonDao> rightPerson = this.personService.fetchBySlug(rightPersonSlug);
        if (rightPerson.isEmpty()) {
            throw NotFoundException.personNotFoundBySlug(personSlug);
        }

        Optional<PersonRelationshipTypeDao> relationshipType = this.personRelationshipService.fetchTypeById(Long.parseLong(relationshipTypeId));
        if (relationshipType.isEmpty()) {
            throw NotFoundException.personRelationshipTypeNotFoundById(relationshipTypeId);
        }

        PersonRelationshipDao newRelationship = new PersonRelationshipDao();
        newRelationship.setLeftPerson(leftPerson.get());
        newRelationship.setRelationship(relationshipType.get());
        newRelationship.setRightPerson(rightPerson.get());

        this.personRelationshipService.createRelationship(newRelationship);

        return REDIRECT_TO_PERSON_RELATIONSHIPS;
    }
}

