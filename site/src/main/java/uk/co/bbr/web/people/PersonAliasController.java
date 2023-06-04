package uk.co.bbr.web.people;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.services.people.PersonAliasService;
import uk.co.bbr.services.people.PersonService;
import uk.co.bbr.services.people.dao.PersonAliasDao;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.web.security.annotations.IsBbrMember;
import uk.co.bbr.web.security.annotations.IsBbrPro;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class PersonAliasController {

    private final PersonService personService;
    private final PersonAliasService personAliasService;

    private static final String REDIRECT_TO_PERSON_ALIASES = "redirect:/people/{personSlug}/edit-aliases";

    @IsBbrMember
    @GetMapping("/people/{personSlug:[\\-a-z\\d]{2,}}/edit-aliases")
    public String bandAliasEdit(Model model, @PathVariable("personSlug") String personSlug) {
        Optional<PersonDao> person = this.personService.fetchBySlug(personSlug);
        if (person.isEmpty()) {
            throw NotFoundException.personNotFoundBySlug(personSlug);
        }

        List<PersonAliasDao> previousNames = this.personAliasService.findAllAliases(person.get());

        model.addAttribute("Person", person.get());
        model.addAttribute("PreviousNames", previousNames);
        return "people/person-aliases";
    }

    @IsBbrMember
    @GetMapping("/people/{personSlug:[\\-a-z\\d]{2,}}/edit-aliases/{aliasId:\\d+}/hide")
    public String bandAliasHide(@PathVariable("personSlug") String personSlug, @PathVariable("aliasId") Long aliasId) {
        Optional<PersonDao> person = this.personService.fetchBySlug(personSlug);
        if (person.isEmpty()) {
            throw NotFoundException.personNotFoundBySlug(personSlug);
        }

        this.personAliasService.hideAlias(person.get(), aliasId);

        return REDIRECT_TO_PERSON_ALIASES;
    }

    @IsBbrMember
    @GetMapping("/people/{personSlug:[\\-a-z\\d]{2,}}/edit-aliases/{aliasId:\\d+}/show")
    public String bandAliasShow(@PathVariable("personSlug") String personSlug, @PathVariable("aliasId") Long aliasId) {
        Optional<PersonDao> person = this.personService.fetchBySlug(personSlug);
        if (person.isEmpty()) {
            throw NotFoundException.personNotFoundBySlug(personSlug);
        }

        this.personAliasService.showAlias(person.get(), aliasId);

        return REDIRECT_TO_PERSON_ALIASES;
    }

    @IsBbrMember
    @GetMapping("/people/{personSlug:[\\-a-z\\d]{2,}}/edit-aliases/{aliasId:\\d+}/delete")
    public String bandAliasDelete(@PathVariable("personSlug") String personSlug, @PathVariable("aliasId") Long aliasId) {
        Optional<PersonDao> person = this.personService.fetchBySlug(personSlug);
        if (person.isEmpty()) {
            throw NotFoundException.personNotFoundBySlug(personSlug);
        }

        this.personAliasService.deleteAlias(person.get(), aliasId);

        return REDIRECT_TO_PERSON_ALIASES;
    }

    @IsBbrMember
    @PostMapping("/people/{personSlug:[\\-a-z\\d]{2,}}/edit-aliases/add")
    public String bandAliasShow(@PathVariable("personSlug") String personSlug, @RequestParam("oldName") String oldName) {
        Optional<PersonDao> person = this.personService.fetchBySlug(personSlug);
        if (person.isEmpty()) {
            throw NotFoundException.personNotFoundBySlug(personSlug);
        }

        PersonAliasDao previousName = new PersonAliasDao();
        previousName.setOldName(oldName);
        previousName.setHidden(false);
        this.personAliasService.createAlias(person.get(), previousName);

        return REDIRECT_TO_PERSON_ALIASES;
    }
}

