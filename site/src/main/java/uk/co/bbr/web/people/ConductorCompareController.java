package uk.co.bbr.web.people;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import uk.co.bbr.services.contests.ContestService;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.services.people.PersonService;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.people.dto.ConductorCompareDto;
import uk.co.bbr.web.security.annotations.IsBbrPro;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class ConductorCompareController {
    private final PersonService personService;
    private final ContestService contestService;

    @IsBbrPro
    @GetMapping("/people/COMPARE-CONDUCTORS")
    public String compareConductorsHome() {
        return "people/compare/select";
    }

    @IsBbrPro
    @GetMapping("/people/COMPARE-CONDUCTORS/{leftSlug:[\\-a-z\\d]{2,}}")
    public String compareConductorToAnother(Model model, @PathVariable("leftSlug") String leftSlug) {
        Optional<PersonDao> leftPerson = this.personService.fetchBySlug(leftSlug);
        if (leftPerson.isEmpty()) {
            throw NotFoundException.personNotFoundBySlug(leftSlug);
        }

        model.addAttribute("LeftPerson", leftPerson.get());

        return "people/compare/select-one";
    }

    @IsBbrPro
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

    @IsBbrPro
    @GetMapping("/people/COMPARE-CONDUCTORS/{leftSlug:[\\-a-z\\d]{2,}}/{rightSlug:[\\-a-z\\d]{2,}}/{contestSlug:[\\-a-z\\d]{2,}}")
    public String compareConductorsDisplayFilterToContest(Model model, @PathVariable("leftSlug") String leftSlug, @PathVariable("rightSlug") String rightSlug, @PathVariable("contestSlug") String contestSlug) {
        Optional<PersonDao> leftPerson = this.personService.fetchBySlug(leftSlug);
        if (leftPerson.isEmpty()) {
            throw NotFoundException.personNotFoundBySlug(leftSlug);
        }

        Optional<PersonDao> rightPerson = this.personService.fetchBySlug(rightSlug);
        if (rightPerson.isEmpty()) {
            throw NotFoundException.personNotFoundBySlug(rightSlug);
        }

        Optional<ContestDao> contest = this.contestService.fetchBySlug(contestSlug);
        if (contest.isEmpty()) {
            throw NotFoundException.contestNotFoundBySlug(contestSlug);
        }

        ConductorCompareDto compareConductors = this.personService.compareConductors(leftPerson.get(), rightPerson.get());
        ConductorCompareDto filteredResults = new ConductorCompareDto(compareConductors.getResults(), contestSlug);

        model.addAttribute("LeftPerson", leftPerson.get());
        model.addAttribute("RightPerson", rightPerson.get());
        model.addAttribute("LeftPersonWins", filteredResults.getLeftPersonWins());
        model.addAttribute("RightPersonWins", filteredResults.getRightPersonWins());
        model.addAttribute("LeftPersonPercent", filteredResults.getLeftPersonPercent());
        model.addAttribute("RightPersonPercent", filteredResults.getRightPersonPercent());
        model.addAttribute("Results", filteredResults.getResults());
        model.addAttribute("FilteredTo", contest.get().getName());

        return "people/compare/conductors";
    }
}
