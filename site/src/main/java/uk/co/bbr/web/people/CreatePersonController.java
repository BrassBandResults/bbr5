package uk.co.bbr.web.people;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import uk.co.bbr.services.people.PersonService;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.web.people.forms.PersonEditForm;
import uk.co.bbr.web.security.annotations.IsBbrMember;

@Controller
@RequiredArgsConstructor
public class CreatePersonController {
    private final PersonService personService;


    @IsBbrMember
    @GetMapping("/create/person")
    public String createGet(Model model) {

        PersonEditForm editForm = new PersonEditForm();

        model.addAttribute("Form", editForm);

        return "people/create";
    }

    @IsBbrMember
    @PostMapping("/create/person")
    public String createPost(@Valid @ModelAttribute("Form") PersonEditForm submittedForm, BindingResult bindingResult) {

        submittedForm.validate(bindingResult);

        if (bindingResult.hasErrors()) {
            return "people/create";
        }

        PersonDao newPerson = new PersonDao();

        newPerson.setFirstNames(submittedForm.getFirstNames());
        newPerson.setSurname(submittedForm.getSurname());
        newPerson.setSuffix(submittedForm.getSuffix());
        newPerson.setKnownFor(submittedForm.getKnownFor());
        newPerson.setStartDate(submittedForm.getStartDate());
        newPerson.setEndDate(submittedForm.getEndDate());
        newPerson.setNotes(submittedForm.getNotes());

        this.personService.create(newPerson);

        return "redirect:/people";
    }
}
