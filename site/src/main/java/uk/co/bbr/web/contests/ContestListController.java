package uk.co.bbr.web.contests;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import uk.co.bbr.services.contests.ContestService;
import uk.co.bbr.services.contests.dto.ContestListDto;
import uk.co.bbr.web.security.annotations.IsBbrMember;

@Controller
@RequiredArgsConstructor
public class ContestListController {

    private final ContestService contestService;

    @GetMapping("/contests")
    public String contestListHome(Model model) {
        return contestListLetter(model, "A");
    }

    @GetMapping("/contests/{letter:[A-Z0-9]}")
    public String contestListLetter(Model model, @PathVariable("letter") String letter) {
        ContestListDto contests = this.contestService.listContestsStartingWith(letter);

        model.addAttribute("ContestPrefixLetter", letter);
        model.addAttribute("Contests", contests);
        return "contests/contests";
    }

    @IsBbrMember
    @GetMapping("/contests/ALL")
    public String contestListAll(Model model) {
        ContestListDto contests = this.contestService.listContestsStartingWith("ALL");

        model.addAttribute("ContestPrefixLetter", "ALL");
        model.addAttribute("Contests", contests);
        return "contests/contests";
    }
}
