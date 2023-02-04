package uk.co.bbr.web.contests;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import uk.co.bbr.services.contests.ContestTagService;
import uk.co.bbr.services.contests.dao.ContestTagDao;
import uk.co.bbr.services.contests.dto.ContestTagDetailsDto;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ContestTagsController {

    private final ContestTagService contestTagService;

    @GetMapping("/tags")
    public String contestTagsListHome(Model model) {
        return contestTagsListLetter(model, "A");
    }

    @GetMapping("/tags/{letter:[A-Z0-9]{1}}")
    public String contestTagsListLetter(Model model, @PathVariable("letter") String letter) {
        List<ContestTagDao> tags = this.contestTagService.listTagsStartingWith(letter);

        model.addAttribute("TagPrefixLetter", letter);
        model.addAttribute("Tags", tags);
        return "contests/tags/tags";
    }

    @GetMapping("/tags/ALL")
    public String contestTagsListAll(Model model) {
        List<ContestTagDao> tags = this.contestTagService.listTagsStartingWith("ALL");

        model.addAttribute("TagPrefixLetter", "ALL");
        model.addAttribute("Tags", tags);
        return "contests/tags/tags";
    }

    @GetMapping("/tags/{slug}")
    public String showSpecificContestTag(Model model, @PathVariable("slug") String slug) {
        ContestTagDetailsDto tagDetails = this.contestTagService.fetchDetailsBySlug(slug);

        model.addAttribute("ContestTag", tagDetails);
        return "contests/tags/tag";
    }

}
