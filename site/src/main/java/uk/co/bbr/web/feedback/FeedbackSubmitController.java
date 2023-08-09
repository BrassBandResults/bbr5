package uk.co.bbr.web.feedback;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uk.co.bbr.services.feedback.FeedbackService;

import jakarta.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
@RequiredArgsConstructor
public class FeedbackSubmitController {
    private final FeedbackService feedbackService;


    @PostMapping("/feedback")
    public String feedback(HttpServletRequest request, @RequestParam("x_url") String url, @RequestParam("x_owner") String ownerUsercode, @RequestParam("feedback") String feedback, @RequestParam("url") String honeyTrapUrl) {
        if (honeyTrapUrl.strip().length() > 0) {
            return "redirect:/feedback/thanks?next=/&t=h";
        }

        String referrer = request.getHeader("referer");
        if (!referrer.contains("//localhost") && !referrer.contains("brassbandresults.co")) {
            return "redirect:/feedback/thanks?next=/&t=r1";
        }

        if (!referrer.strip().equalsIgnoreCase(url.strip())) {
            return "redirect:/feedback/thanks?next=/&t=r2";
        }

        final String URL_PATTERN = "^[^#]*?://.*?(/.*)$";
        Pattern pattern = Pattern.compile(URL_PATTERN);
        Matcher matcher = pattern.matcher(url.strip());
        String offset = url.strip();
        if (matcher.find()) {
            offset = matcher.group(1);
        }

        String browserName = request.getHeader("User-Agent");
        String ip = request.getHeader("X-FORWARDED-FOR");
        if (ip == null) {
            ip = request.getRemoteAddr();
        }

        this.feedbackService.submit(offset, referrer, ownerUsercode.strip(), feedback.strip(), browserName, ip);

        return "redirect:/feedback/thanks?next=" + offset;
    }


    @GetMapping("/feedback/thanks")
    public String thanks(Model model, @RequestParam("next") String next) {
        model.addAttribute("FeedbackOffset", next);

        return "feedback/thanks";
    }
}
