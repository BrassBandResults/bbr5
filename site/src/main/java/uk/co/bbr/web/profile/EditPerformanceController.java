package uk.co.bbr.web.profile;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.parameters.P;
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
import uk.co.bbr.services.performances.PerformanceService;
import uk.co.bbr.services.performances.dao.PerformanceDao;
import uk.co.bbr.services.performances.types.Instrument;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.dao.SiteUserDao;
import uk.co.bbr.web.profile.forms.EditPerformanceForm;
import uk.co.bbr.web.profile.forms.EditProfileForm;
import uk.co.bbr.web.security.annotations.IsBbrMember;
import uk.co.bbr.web.security.annotations.IsBbrPro;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class EditPerformanceController {

    private final SecurityService securityService;
    private final PerformanceService performanceService;

    @IsBbrMember
    @GetMapping("/profile/performances/{performanceId:\\d+}/{resultId:\\d+}/edit")
    public String performanceEditGet(Model model, @PathVariable Long performanceId, @PathVariable Long resultId) {
        SiteUserDao currentUser = this.securityService.getCurrentUser();
        Optional<PerformanceDao> performance = this.performanceService.fetchPerformance(currentUser, performanceId);
        if (performance.isEmpty()) {
            throw NotFoundException.performanceNotFoundByUserAndId(currentUser.getUsercode(), performanceId);
        }
        if (!performance.get().getResult().getId().equals(resultId)) {
            throw NotFoundException.performanceNotFoundByUserAndId(currentUser.getUsercode(), performanceId);
        }

        EditPerformanceForm form = new EditPerformanceForm(performance.get());

        model.addAttribute("Form", form);
        model.addAttribute("Performance", performance.get());

        return "profile/edit-performance";
    }

    @IsBbrMember
    @PostMapping("/profile/performances/{performanceId:\\d+}/{resultId:\\d+}/edit")
    public String performanceEditPost(Model model, @Valid @ModelAttribute("Form") EditPerformanceForm submittedForm, BindingResult bindingResult, @PathVariable Long performanceId, @PathVariable Long resultId) {
        SiteUserDao currentUser = this.securityService.getCurrentUser();
        Optional<PerformanceDao> performance = this.performanceService.fetchPerformance(currentUser, performanceId);
        if (performance.isEmpty()) {
            throw NotFoundException.performanceNotFoundByUserAndId(currentUser.getUsercode(), performanceId);
        }
        if (!performance.get().getResult().getId().equals(resultId)) {
            throw NotFoundException.performanceNotFoundByUserAndId(currentUser.getUsercode(), performanceId);
        }



        performance.get().setInstrument(Instrument.fromCode(submittedForm.getInstrumentCode()));

        this.performanceService.update(performance.get());

        return "redirect:/profile/performances";
    }

    @IsBbrMember
    @GetMapping("/profile/performances/{performanceId:\\d+}/{resultId:\\d+}/delete")
    public String performanceDelete(Model model, @PathVariable Long performanceId, @PathVariable Long resultId) {
        SiteUserDao currentUser = this.securityService.getCurrentUser();
        Optional<PerformanceDao> performance = this.performanceService.fetchPerformance(currentUser, performanceId);
        if (performance.isEmpty()) {
            throw NotFoundException.performanceNotFoundByUserAndId(currentUser.getUsercode(), performanceId);
        }
        if (!performance.get().getResult().getId().equals(resultId)) {
            throw NotFoundException.performanceNotFoundByUserAndId(currentUser.getUsercode(), performanceId);
        }

        this.performanceService.delete(performance.get());

        return "redirect:/profile/performances";
    }
}
