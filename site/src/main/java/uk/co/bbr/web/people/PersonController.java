package uk.co.bbr.web.people;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import uk.co.bbr.services.bands.BandService;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.bands.dao.BandPreviousNameDao;
import uk.co.bbr.services.bands.dto.BandDetailsDto;
import uk.co.bbr.services.contests.ContestResultService;
import uk.co.bbr.services.contests.dao.ContestResultDao;
import uk.co.bbr.services.contests.sql.dto.PersonConductingSqlDto;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.services.people.PersonService;
import uk.co.bbr.services.people.dao.PersonAliasDao;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.people.dto.ConductingDetailsDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class PersonController {

    private final PersonService personService;
    private final ContestResultService contestResultService;

    @GetMapping("/people/{slug:[\\-a-z\\d]{2,}}")
    public String bandDetail(Model model, @PathVariable("slug") String slug) {
        Optional<PersonDao> person = this.personService.fetchBySlug(slug);
        if (person.isEmpty()) {
            throw new NotFoundException("Person with slug " + slug + " not found");
        }

        List<PersonAliasDao> previousNames = this.personService.findVisibleAliases(person.get());
        ConductingDetailsDto personConductingResults = this.contestResultService.findResultsForConductor(person.get());
        int adjudicationsCount = this.personService.fetchAdjudicationCount(person.get());
        int composerCount = this.personService.fetchComposerCount(person.get());
        int arrangerCount = this.personService.fetchArrangerCount(person.get());

        model.addAttribute("Person", person.get());
        model.addAttribute("PreviousNames", previousNames);
        model.addAttribute("ConductingResults", personConductingResults.getBandResults());
        model.addAttribute("ConductingResultsWhitFriday", personConductingResults.getBandWhitResults());
        model.addAttribute("ResultsCount", personConductingResults.getBandResults().size());
        model.addAttribute("WhitCount", personConductingResults.getBandWhitResults().size());
        model.addAttribute("AdjudicationsCount", adjudicationsCount);
        model.addAttribute("PieceCount", composerCount + arrangerCount);

        return "people/person";
    }
}
