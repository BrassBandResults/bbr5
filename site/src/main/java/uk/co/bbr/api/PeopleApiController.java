package uk.co.bbr.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import uk.co.bbr.services.people.PersonService;
import uk.co.bbr.services.people.dao.PersonDao;

import java.util.Optional;

@RestController
@RequestMapping("/api/people")
@RequiredArgsConstructor
public class PeopleApiController {

    private final PersonService personService;

    @GetMapping("/{slug}")
    public Optional<PersonDao> getPerson(@PathVariable("slug") String slug) {
        return this.personService.fetchBySlug(slug);
    }
}
