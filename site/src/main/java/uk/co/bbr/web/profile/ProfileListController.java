package uk.co.bbr.web.profile;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import uk.co.bbr.services.people.PersonService;
import uk.co.bbr.services.people.dao.PersonProfileDao;
import uk.co.bbr.web.security.annotations.IsBbrAdmin;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ProfileListController {

    private final PersonService personService;

    @IsBbrAdmin
    @GetMapping("/people-profiles")
    public String publicUserPage(Model model) {

        List<PersonProfileDao> allProfiles = this.personService.fetchAllProfiles();

        model.addAttribute("Profiles", allProfiles);

        return "people/all-profiles";
    }
}
