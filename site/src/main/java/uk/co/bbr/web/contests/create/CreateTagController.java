package uk.co.bbr.web.contests.create;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import uk.co.bbr.services.contests.ContestTagService;
import uk.co.bbr.web.contests.forms.TagCreateForm;
import uk.co.bbr.web.security.annotations.IsBbrMember;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class CreateTagController {

    private final ContestTagService contestTagService;


    @IsBbrMember
    @GetMapping("/create/tag")
    public String createGet(Model model) {

        model.addAttribute("TagForm", new TagCreateForm());

        return "contests/tags/create";
    }

    @IsBbrMember
    @PostMapping("/create/tag")
    public String createPost(Model model, @Valid @ModelAttribute("TagForm") TagCreateForm submittedTag, BindingResult bindingResult) {

        submittedTag.validate(bindingResult);

        if (bindingResult.hasErrors()) {
            return "contests/tags/create";
        }

        this.contestTagService.create(submittedTag.getName());

        return "redirect:/tags/ALL";
    }
}
