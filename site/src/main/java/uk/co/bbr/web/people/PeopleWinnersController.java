package uk.co.bbr.web.people;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import uk.co.bbr.services.bands.BandService;
import uk.co.bbr.services.bands.sql.dto.BandWinnersSqlDto;
import uk.co.bbr.services.people.PersonService;
import uk.co.bbr.services.people.sql.dto.PeopleWinnersSqlDto;
import uk.co.bbr.web.security.annotations.IsBbrPro;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class PeopleWinnersController {
    private final PersonService personService;

    @IsBbrPro
    @GetMapping("/people/WINNERS")
    public String showPeopleWinners(Model model) {
        List<PeopleWinnersSqlDto> peopleWinners = this.personService.fetchContestWinningPeople();

        model.addAttribute("WinningPeople", peopleWinners);

        return "people/winners";
    }

    @IsBbrPro
    @GetMapping("/people/WINNERS/before/1950")
    public String showPeopleWinnersBefore1950(Model model) {
        List<PeopleWinnersSqlDto> peopleWinners = this.personService.fetchContestWinningPeopleBefore(1950);

        model.addAttribute("WinningPeople", peopleWinners);

        return "people/winners-before-1950";
    }

    @IsBbrPro
    @GetMapping("/people/WINNERS/after/1950")
    public String showPeopleWinnersAfter1950(Model model) {
        List<PeopleWinnersSqlDto> peopleWinners = this.personService.fetchContestWinningPeopleAfter(1950);

        model.addAttribute("WinningPeople", peopleWinners);

        return "people/winners-after-1950";
    }
}
