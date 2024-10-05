package uk.co.bbr.web.bands;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uk.co.bbr.services.bands.BandRehearsalsService;
import uk.co.bbr.services.bands.BandService;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.bands.dao.BandRehearsalDayDao;
import uk.co.bbr.services.bands.types.RehearsalDay;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.web.security.annotations.IsBbrMember;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class BandRehearsalsController {

    private final BandService bandService;
    private final BandRehearsalsService bandRehearsalsService;

    @IsBbrMember
    @GetMapping("/bands/{bandSlug:[\\-_a-z\\d]{2,}}/edit-rehearsals")
    public String bandRehearsalsEdit(Model model, @PathVariable("bandSlug") String bandSlug) {
        Optional<BandDao> band = this.bandService.fetchBySlug(bandSlug);
        if (band.isEmpty()) {
            throw NotFoundException.bandNotFoundBySlug(bandSlug);
        }

        BandRehearsalDayDao monday = null;
        BandRehearsalDayDao tuesday = null;
        BandRehearsalDayDao wednesday = null;
        BandRehearsalDayDao thursday = null;
        BandRehearsalDayDao friday = null;
        BandRehearsalDayDao saturday = null;
        BandRehearsalDayDao sunday = null;

        List<BandRehearsalDayDao> rehearsalDays = this.bandRehearsalsService.fetchRehearsalDays(band.get());
        for (BandRehearsalDayDao eachDay : rehearsalDays) {
            switch (eachDay.getDay()) {
                case MONDAY -> monday = eachDay;
                case TUESDAY -> tuesday = eachDay;
                case WEDNESDAY -> wednesday = eachDay;
                case THURSDAY -> thursday = eachDay;
                case FRIDAY -> friday = eachDay;
                case SATURDAY -> saturday = eachDay;
                case SUNDAY -> sunday = eachDay;
            }
        }

        model.addAttribute("Band", band.get());
        model.addAttribute("Monday", monday);
        model.addAttribute("Tuesday", tuesday);
        model.addAttribute("Wednesday", wednesday);
        model.addAttribute("Thursday", thursday);
        model.addAttribute("Friday", friday);
        model.addAttribute("Saturday", saturday);
        model.addAttribute("Sunday", sunday);
        return "bands/band-rehearsals";
    }

    @IsBbrMember
    @PostMapping("/bands/{bandSlug:[\\-_a-z\\d]{2,}}/edit-rehearsals")
    public String bandRehearsalsPost(@PathVariable("bandSlug") String bandSlug,
                                     @RequestParam("monday-checkbox") Optional<Boolean> mondayCheckbox, @RequestParam("monday-details") String mondayDetails,
                                     @RequestParam("tuesday-checkbox") Optional<Boolean> tuesdayCheckbox, @RequestParam("tuesday-details") String tuesdayDetails,
                                     @RequestParam("wednesday-checkbox") Optional<Boolean> wednesdayCheckbox, @RequestParam("wednesday-details") String wednesdayDetails,
                                     @RequestParam("thursday-checkbox") Optional<Boolean> thursdayCheckbox, @RequestParam("thursday-details") String thursdayDetails,
                                     @RequestParam("friday-checkbox") Optional<Boolean> fridayCheckbox, @RequestParam("friday-details") String fridayDetails,
                                     @RequestParam("saturday-checkbox") Optional<Boolean> saturdayCheckbox, @RequestParam("saturday-details") String saturdayDetails,
                                     @RequestParam("sunday-checkbox") Optional<Boolean> sundayCheckbox, @RequestParam("sunday-details") String sundayDetails) {
        Optional<BandDao> band = this.bandService.fetchBySlug(bandSlug);
        if (band.isEmpty()) {
            throw NotFoundException.bandNotFoundBySlug(bandSlug);
        }

        this.bandRehearsalsService.deleteRehearsalDays(band.get());

        if (mondayCheckbox.isPresent()) {
            this.bandRehearsalsService.createRehearsalDay(band.get(), RehearsalDay.MONDAY, mondayDetails);
        }
        if (tuesdayCheckbox.isPresent()) {
            this.bandRehearsalsService.createRehearsalDay(band.get(), RehearsalDay.TUESDAY, tuesdayDetails);
        }
        if (wednesdayCheckbox.isPresent()) {
            this.bandRehearsalsService.createRehearsalDay(band.get(), RehearsalDay.WEDNESDAY, wednesdayDetails);
        }
        if (thursdayCheckbox.isPresent()) {
            this.bandRehearsalsService.createRehearsalDay(band.get(), RehearsalDay.THURSDAY, thursdayDetails);
        }
        if (fridayCheckbox.isPresent()) {
            this.bandRehearsalsService.createRehearsalDay(band.get(), RehearsalDay.FRIDAY, fridayDetails);
        }
        if (saturdayCheckbox.isPresent()) {
            this.bandRehearsalsService.createRehearsalDay(band.get(), RehearsalDay.SATURDAY, saturdayDetails);
        }
        if (sundayCheckbox.isPresent()) {
            this.bandRehearsalsService.createRehearsalDay(band.get(), RehearsalDay.SUNDAY, sundayDetails);
        }

        return "redirect:/bands/{bandSlug}";
    }

}

