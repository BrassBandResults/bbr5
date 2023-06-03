package uk.co.bbr.web.people;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.services.people.PersonService;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.people.dto.ConductorCompareDto;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class ConductorCompareController {
    private final PersonService personService;

    @GetMapping("/people/COMPARE-CONDUCTORS")
    public String compareConductorsHome() {
        return "people/compare/select";
    }

    @GetMapping("/people/COMPARE-CONDUCTORS/{leftSlug:[\\-a-z\\d]{2,}}")
    public String compareConductorToAnother(Model model, @PathVariable("leftSlug") String leftSlug) {
        Optional<PersonDao> leftPerson = this.personService.fetchBySlug(leftSlug);
        if (leftPerson.isEmpty()) {
            throw NotFoundException.personNotFoundBySlug(leftSlug);
        }

        model.addAttribute("LeftPerson", leftPerson.get());

        return "people/compare/select-one";
    }

    @GetMapping("/people/COMPARE-CONDUCTORS/{leftSlug:[\\-a-z\\d]{2,}}/{rightSlug:[\\-a-z\\d]{2,}}")
    public String compareConductorsDisplay(Model model, @PathVariable("leftSlug") String leftSlug, @PathVariable("rightSlug") String rightSlug) {
        Optional<PersonDao> leftPerson = this.personService.fetchBySlug(leftSlug);
        if (leftPerson.isEmpty()) {
            throw NotFoundException.personNotFoundBySlug(leftSlug);
        }

        Optional<PersonDao> rightPerson = this.personService.fetchBySlug(rightSlug);
        if (rightPerson.isEmpty()) {
            throw NotFoundException.personNotFoundBySlug(rightSlug);
        }

        ConductorCompareDto compareConductors = this.personService.compareConductors(leftPerson.get(), rightPerson.get());

        model.addAttribute("LeftPerson", leftPerson.get());
        model.addAttribute("RightPerson", rightPerson.get());
        model.addAttribute("LeftPersonWins", compareConductors.getLeftPersonWins());
        model.addAttribute("RightPersonWins", compareConductors.getRightPersonWins());
        model.addAttribute("LeftPersonPercent", compareConductors.getLeftPersonPercent());
        model.addAttribute("RightPersonPercent", compareConductors.getRightPersonPercent());
        model.addAttribute("Results", compareConductors.getResults());

        return "people/compare/conductors";
    }
}
