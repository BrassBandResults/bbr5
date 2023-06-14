package uk.co.bbr.web.feedback;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uk.co.bbr.services.feedback.FeedbackService;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequiredArgsConstructor
public class FeedbackController {
    private final FeedbackService feedbackService;


    @PostMapping("/feedback")
    public String feedback(HttpServletRequest request, @RequestParam("x_url") String url, @RequestParam("x_owner") String ownerUsercode, @RequestParam("feedback") String feedback, @RequestParam("url") String honeyTrapUrl) {
        if (honeyTrapUrl.trim().length() > 0) {
            return "redirect:/feedback/thanks?next=/&t=h";
        }

        String referrer = request.getHeader("referer");
        if (!referrer.contains("//localhost") && !referrer.contains("brassbandresults.co")) {
            return "redirect:/feedback/thanks?next=/&t=r1";
        }

        if (!referrer.trim().equalsIgnoreCase(url.trim())) {
            return "redirect:/feedback/thanks?next=/&t=r2";
        }

        String offset = url.trim();
        if (offset.startsWith("http://")) {
            offset = offset.substring("http://".length());
        }
        if (offset.startsWith("https://")) {
            offset = offset.substring("https://".length());
        }
        offset = offset.substring(offset.indexOf("/"));

        String browserName = request.getHeader("User-Agent");
        String ip = request.getHeader("X-FORWARDED-FOR");
        if (ip == null) {
            ip = request.getRemoteAddr();
        }

        this.feedbackService.submit(offset, referrer, ownerUsercode.trim(), feedback.trim(), browserName, ip);

        return "redirect:/feedback/thanks?next=" + offset;
    }


    @GetMapping("/feedback/thanks")
    public String thanks(Model model, @RequestParam("next") String next) {
        model.addAttribute("FeedbackOffset", next);

        return "feedback/thanks";
    }
}
