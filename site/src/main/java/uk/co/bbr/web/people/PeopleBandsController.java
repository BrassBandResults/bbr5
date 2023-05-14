package uk.co.bbr.web.people;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import uk.co.bbr.services.people.PersonService;
import uk.co.bbr.services.people.sql.dto.PeopleBandsSqlDto;
import uk.co.bbr.services.people.sql.dto.PeopleWinnersSqlDto;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class PeopleBandsController {
    private final PersonService personService;

    @GetMapping("/people/BANDS")
    public String showPeopleBands(Model model) {
        List<PeopleBandsSqlDto> bandsConducted = this.personService.fetchBandsConductedList();

        model.addAttribute("BandsConducted", bandsConducted);

        return "people/bands";
    }

    @GetMapping("/people/BANDS/before/1950")
    public String showPeopleBandsBefore1950(Model model) {
        List<PeopleBandsSqlDto> bandsConducted = this.personService.fetchBandsConductedListBefore(1950);

        model.addAttribute("BandsConducted", bandsConducted);

        return "people/bands-before-1950";
    }

    @GetMapping("/people/BANDS/after/1950")
    public String showPeopleBandsAfter1950(Model model) {
        List<PeopleBandsSqlDto> bandsConducted = this.personService.fetchBandsConductedListAfter(1950);

        model.addAttribute("BandsConducted", bandsConducted);

        return "people/bands-after-1950";
    }
}
