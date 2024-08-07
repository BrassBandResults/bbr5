package uk.co.bbr.web.tags;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import uk.co.bbr.services.tags.ContestTagService;
import uk.co.bbr.services.tags.dao.ContestTagDao;
import uk.co.bbr.web.security.annotations.IsBbrMember;
import uk.co.bbr.web.security.annotations.IsBbrSuperuser;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class TagsListController {

    private final ContestTagService contestTagService;

    @GetMapping("/tags")
    public String contestTagsListHome(Model model) {
        return contestTagsListLetter(model, "A");
    }

    @GetMapping("/tags/{letter:[A-Z0-9]}")
    public String contestTagsListLetter(Model model, @PathVariable("letter") String letter) {
        List<ContestTagDao> tags = this.contestTagService.listTagsStartingWith(letter);

        model.addAttribute("TagPrefixLetter", letter);
        model.addAttribute("Tags", tags);
        return "tags/tags";
    }

    @IsBbrMember
    @GetMapping("/tags/ALL")
    public String contestTagsListAll(Model model) {
        List<ContestTagDao> tags = this.contestTagService.listTagsStartingWith("ALL");

        model.addAttribute("TagPrefixLetter", "ALL");
        model.addAttribute("Tags", tags);
        return "tags/tags";
    }

    @IsBbrSuperuser
    @GetMapping("/tags/UNUSED")
    public String contestUnusedTags(Model model) {
        List<ContestTagDao> tags = this.contestTagService.listUnusedTags();

        model.addAttribute("TagPrefixLetter", "UNUSED");
        model.addAttribute("Tags", tags);
        return "tags/tags";
    }
}
