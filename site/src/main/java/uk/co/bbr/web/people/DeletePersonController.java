package uk.co.bbr.web.people;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import uk.co.bbr.services.bands.types.ResultSetCategory;
import uk.co.bbr.services.events.PersonResultService;
import uk.co.bbr.services.events.dao.ContestAdjudicatorDao;
import uk.co.bbr.services.events.dto.ResultDetailsDto;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.services.people.PersonService;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.pieces.PieceService;
import uk.co.bbr.services.pieces.dao.PieceDao;
import uk.co.bbr.web.security.annotations.IsBbrMember;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class DeletePersonController {

    private final PersonService personService;
    private final PieceService pieceService;
    private final PersonResultService personResultService;

    @IsBbrMember
    @GetMapping("/people/{personSlug:[\\-_a-z\\d]{2,}}/delete")
    public String deletePerson(Model model, @PathVariable("personSlug") String personSlug) {
        Optional<PersonDao> person = this.personService.fetchBySlug(personSlug);
        if (person.isEmpty()) {
            throw NotFoundException.personNotFoundBySlug(personSlug);
        }

        ResultDetailsDto personConductingResults = this.personResultService.findResultsForConductor(person.get(), ResultSetCategory.ALL);
        List<ContestAdjudicatorDao> adjudications = this.personService.fetchAdjudications(person.get());
        List<PieceDao> personPieces = this.pieceService.findPiecesForPerson(person.get());

        boolean blocked = !personConductingResults.getBandAllResults().isEmpty() || !adjudications.isEmpty() || !personPieces.isEmpty();

        if (blocked) {
            model.addAttribute("Person", person.get());
            model.addAttribute("ConductingResults", personConductingResults.getBandAllResults());
            model.addAttribute("Adjudications", adjudications);
            model.addAttribute("Pieces", personPieces);

            return "people/delete-person-blocked";
        }

        this.personService.delete(person.get());

        return "redirect:/people";
    }
}
