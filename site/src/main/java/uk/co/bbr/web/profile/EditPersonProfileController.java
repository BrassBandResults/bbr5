package uk.co.bbr.web.profile;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.services.people.PersonService;
import uk.co.bbr.services.people.dao.PersonProfileDao;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.dao.SiteUserDao;
import uk.co.bbr.web.profile.forms.EditProfileForm;
import uk.co.bbr.web.security.annotations.IsBbrPro;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class EditPersonProfileController {

    private final SecurityService securityService;
    private final PersonService personService;

    @IsBbrPro
    @GetMapping("/profile/people-profiles/{personSlug:[\\-a-z\\d]{2,}}/edit")
    public String personProfileGet(Model model, @PathVariable String personSlug) {
        SiteUserDao currentUser = this.securityService.getCurrentUser();
        Optional<PersonProfileDao> personProfile = this.personService.fetchProfileByPersonSlugAndOwner(personSlug, currentUser);
        if (personProfile.isEmpty()) {
            throw NotFoundException.personNotFoundBySlug(personSlug);
        }

        EditProfileForm form = new EditProfileForm(personProfile.get());

        model.addAttribute("PersonProfile", personProfile.get());
        model.addAttribute("Form", form);

        return "profile/edit-person-profile";
    }

    @IsBbrPro
    @PostMapping("/profile/people-profiles/{personSlug:[\\-a-z\\d]{2,}}/edit")
    public String personProfilePost(Model model, @Valid @ModelAttribute("Form") EditProfileForm submittedForm, BindingResult bindingResult, @PathVariable("personSlug") String personSlug) {
        SiteUserDao currentUser = this.securityService.getCurrentUser();
        Optional<PersonProfileDao> personProfile = this.personService.fetchProfileByPersonSlugAndOwner(personSlug, currentUser);
        if (personProfile.isEmpty()) {
            throw NotFoundException.personNotFoundBySlug(personSlug);
        }

        submittedForm.validate(bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("PersonProfile", personProfile.get());

            return "profile/edit-person-profile";
        }

        PersonProfileDao existingProfile = personProfile.get();

        existingProfile.setProfile(submittedForm.getProfile());
        existingProfile.setAddress(submittedForm.getAddress());
        existingProfile.setEmail(submittedForm.getEmail());
        existingProfile.setQualifications(submittedForm.getQualifications());
        existingProfile.setHomePhone(submittedForm.getHomePhone());
        existingProfile.setMobilePhone(submittedForm.getMobilePhone());
        existingProfile.setTitle(submittedForm.getTitle());
        existingProfile.setWebsite(submittedForm.getWebsite());

        this.personService.update(existingProfile);

        return "redirect:/profile/people-profiles";
    }
}
