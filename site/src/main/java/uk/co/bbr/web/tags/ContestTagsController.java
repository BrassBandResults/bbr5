package uk.co.bbr.web.tags;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.services.tags.ContestTagService;
import uk.co.bbr.services.tags.dao.ContestTagDao;
import uk.co.bbr.services.tags.dto.ContestTagDetailsDto;
import uk.co.bbr.web.security.annotations.IsBbrMember;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class ContestTagsController {

    private final ContestTagService contestTagService;

    @GetMapping("/tags/{slug:[\\-a-z\\d]{2,}}")
    public String showSpecificContestTag(Model model, @PathVariable("slug") String slug) {
        ContestTagDetailsDto tagDetails = this.contestTagService.fetchDetailsBySlug(slug);

        model.addAttribute("ContestTag", tagDetails);
        return "tags/tag";
    }

    @IsBbrMember
    @GetMapping("/tags/{slug:[\\-a-z\\d]{2,}}/delete")
    public String deleteContestTag(@PathVariable("slug") String slug) {
        Optional<ContestTagDao> tag = this.contestTagService.fetchBySlug(slug);
        if (tag.isEmpty()) {
            throw NotFoundException.tagNotFoundBySlug(slug);
        }

        this.contestTagService.deleteTag(tag.get());

        return "redirect:/tags/ALL";
    }

}
