package uk.co.bbr.web.people;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.services.people.PersonService;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.pieces.PieceService;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class EditPersonController {

    private final PersonService personService;
    private final PieceService pieceService;

    @GetMapping("/people/{slug:[\\-a-z\\d]{2,}}/edit")
    public String personConducting(Model model, @PathVariable("slug") String slug) {
        Optional<PersonDao> person = this.personService.fetchBySlug(slug);
        if (person.isEmpty()) {
            throw new NotFoundException("Person with slug " + slug + " not found");
        }

        model.addAttribute("Person", person.get());

        return "people/edit";
    }
}
