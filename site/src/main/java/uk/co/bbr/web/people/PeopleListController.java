package uk.co.bbr.web.people;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import uk.co.bbr.services.people.PeopleService;
import uk.co.bbr.services.people.dto.PeopleListDto;

@Controller
@RequiredArgsConstructor
public class PeopleListController {

    private final PeopleService peopleService;

    @GetMapping("/people")
    public String peopleListHome(Model model) {
        return peopleListLetter(model, "A");
    }

    @GetMapping("/people/{letter:[A-Z]{1}}")
    public String peopleListLetter(Model model, @PathVariable("letter") String letter) {
        PeopleListDto people = this.peopleService.listPeopleStartingWith(letter);

        model.addAttribute("PeoplePrefixLetter", letter);
        model.addAttribute("People", people);
        return "people/people";
    }
}
