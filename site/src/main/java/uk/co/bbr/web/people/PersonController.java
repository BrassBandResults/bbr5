package uk.co.bbr.web.people;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import uk.co.bbr.services.contests.ContestResultService;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.services.people.PersonService;
import uk.co.bbr.services.people.dao.PersonAliasDao;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.people.dto.ConductingDetailsDto;
import uk.co.bbr.services.pieces.PieceService;
import uk.co.bbr.services.pieces.dao.PieceDao;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class PersonController {

    private final PersonService personService;
    private final ContestResultService contestResultService;
    private final PieceService pieceService;

    @GetMapping("/people/{slug:[\\-a-z\\d]{2,}}")
    public String personConducting(Model model, @PathVariable("slug") String slug) {
        Optional<PersonDao> person = this.personService.fetchBySlug(slug);
        if (person.isEmpty()) {
            throw NotFoundException.personNotFoundBySlug(slug);
        }

        List<PersonAliasDao> previousNames = this.personService.findVisibleAliases(person.get());
        ConductingDetailsDto personConductingResults = this.contestResultService.findResultsForConductor(person.get());
        int adjudicationsCount = this.personService.fetchAdjudicationCount(person.get());
        int composerCount = this.personService.fetchComposerCount(person.get());
        int arrangerCount = this.personService.fetchArrangerCount(person.get());

        model.addAttribute("Person", person.get());
        model.addAttribute("PreviousNames", previousNames);
        model.addAttribute("ConductingResults", personConductingResults.getBandResults());
        model.addAttribute("WhitResults", personConductingResults.getBandWhitResults());
        model.addAttribute("ResultsCount", personConductingResults.getBandResults().size());
        model.addAttribute("WhitCount", personConductingResults.getBandWhitResults().size());
        model.addAttribute("AdjudicationsCount", adjudicationsCount);
        model.addAttribute("PieceCount", composerCount + arrangerCount);

        return "people/person";
    }

    @GetMapping("/people/{slug:[\\-a-z\\d]{2,}}/whits")
    public String personWhitFriday(Model model, @PathVariable("slug") String slug) {
        Optional<PersonDao> person = this.personService.fetchBySlug(slug);
        if (person.isEmpty()) {
            throw NotFoundException.personNotFoundBySlug(slug);
        }

        List<PersonAliasDao> previousNames = this.personService.findVisibleAliases(person.get());
        ConductingDetailsDto personConductingResults = this.contestResultService.findResultsForConductor(person.get());
        int adjudicationsCount = this.personService.fetchAdjudicationCount(person.get());
        int composerCount = this.personService.fetchComposerCount(person.get());
        int arrangerCount = this.personService.fetchArrangerCount(person.get());

        model.addAttribute("Person", person.get());
        model.addAttribute("PreviousNames", previousNames);
        model.addAttribute("ConductingResults", personConductingResults.getBandResults());
        model.addAttribute("WhitResults", personConductingResults.getBandWhitResults());
        model.addAttribute("ResultsCount", personConductingResults.getBandResults().size());
        model.addAttribute("WhitCount", personConductingResults.getBandWhitResults().size());
        model.addAttribute("AdjudicationsCount", adjudicationsCount);
        model.addAttribute("PieceCount", composerCount + arrangerCount);

        return "people/person-whits";
    }

    @GetMapping("/people/{slug:[\\-a-z\\d]{2,}}/pieces")
    public String personPieces(Model model, @PathVariable("slug") String slug) {
        Optional<PersonDao> person = this.personService.fetchBySlug(slug);
        if (person.isEmpty()) {
            throw NotFoundException.personNotFoundBySlug(slug);
        }

        List<PersonAliasDao> previousNames = this.personService.findVisibleAliases(person.get());
        List<PieceDao> personPieces = this.pieceService.findPiecesForPerson(person.get());
        ConductingDetailsDto personConductingResults = this.contestResultService.findResultsForConductor(person.get());
        int adjudicationsCount = this.personService.fetchAdjudicationCount(person.get());
        int composerCount = this.personService.fetchComposerCount(person.get());
        int arrangerCount = this.personService.fetchArrangerCount(person.get());

        model.addAttribute("Person", person.get());
        model.addAttribute("PreviousNames", previousNames);
        model.addAttribute("ResultsCount", personConductingResults.getBandResults().size());
        model.addAttribute("WhitCount", personConductingResults.getBandWhitResults().size());
        model.addAttribute("AdjudicationsCount", adjudicationsCount);
        model.addAttribute("PieceCount", composerCount + arrangerCount);
        model.addAttribute("Pieces", personPieces);

        return "people/person-pieces";
    }
}
