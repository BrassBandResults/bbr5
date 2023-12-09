package uk.co.bbr.web.people;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import uk.co.bbr.services.people.PersonService;
import uk.co.bbr.services.people.dto.PeopleListDto;
import uk.co.bbr.web.security.annotations.IsBbrSuperuser;

@Controller
@RequiredArgsConstructor
public class PeopleListController {

    private final PersonService personService;

    @GetMapping("/people")
    public String peopleListHome(Model model) {
        return peopleListLetter(model, "A");
    }

    @GetMapping("/people/{letter:[A-Z]}")
    public String peopleListLetter(Model model, @PathVariable("letter") String letter) {
        PeopleListDto people = this.personService.listPeopleStartingWith(letter);

        model.addAttribute("PeoplePrefixLetter", letter);
        model.addAttribute("People", people);
        return "people/people";
    }

    @IsBbrSuperuser
    @GetMapping("/people/UNUSED")
    public String peopleDoneNothing(Model model) {
        PeopleListDto people = this.personService.listPeopleDoneNothing();

        model.addAttribute("PeoplePrefixLetter", "UNUSED");
        model.addAttribute("People", people);
        return "people/people";
    }
}
