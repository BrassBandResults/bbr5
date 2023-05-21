package uk.co.bbr.web.people;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.services.framework.ValidationException;
import uk.co.bbr.services.people.PersonService;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.pieces.PieceService;
import uk.co.bbr.web.people.forms.PersonEditForm;
import uk.co.bbr.web.security.annotations.IsBbrMember;

import javax.validation.Valid;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class EditPersonController {

    private final PersonService personService;

    @IsBbrMember
    @GetMapping("/people/{personSlug:[\\-a-z\\d]{2,}}/edit")
    public String editPersonForm(Model model, @PathVariable("personSlug") String personSlug) {
        Optional<PersonDao> person = this.personService.fetchBySlug(personSlug);
        if (person.isEmpty()) {
            throw new NotFoundException("Person with slug " + personSlug + " not found");
        }

        PersonEditForm personEditDto = new PersonEditForm(person.get());

        model.addAttribute("Person", person.get());
        model.addAttribute("PersonForm", personEditDto);

        return "people/edit";
    }

    @IsBbrMember
    @PostMapping("/people/{personSlug:[\\-a-z\\d]{2,}}/edit")
    public String editPersonSave(Model model, @Valid @ModelAttribute("PersonForm") PersonEditForm submittedPerson, BindingResult bindingResult, @PathVariable("personSlug") String personSlug) {
        Optional<PersonDao> existingPersonOptional = this.personService.fetchBySlug(personSlug);
        if (existingPersonOptional.isEmpty()) {
            throw new NotFoundException("Person with slug " + personSlug + " not found");
        }

        submittedPerson.validate(bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("Person", existingPersonOptional.get());

            return "/people/edit";
        }

        PersonDao existingPerson = existingPersonOptional.get();

        existingPerson.setSurname(submittedPerson.getSurname());
        existingPerson.setFirstNames(submittedPerson.getFirstNames());
        existingPerson.setSuffix(submittedPerson.getSuffix());
        existingPerson.setKnownFor(submittedPerson.getKnownFor());
        existingPerson.setStartDate(submittedPerson.getStartDate());
        existingPerson.setEndDate(submittedPerson.getEndDate());
        existingPerson.setNotes(submittedPerson.getNotes());

        this.personService.update(existingPerson);

        return "redirect:/people/" + personSlug;
    }
}
