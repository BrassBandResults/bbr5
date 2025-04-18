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

        if (feedback.strip().length() < 4) {
            return "redirect:/feedback/thanks?next=/&t=b";
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

        if (ip.equals("45.153.163.104")) {
            return "redirect:/feedback/thanks?next=/&t=ip";
        }

        String strippedOwnerUsercode = ownerUsercode;
        if (ownerUsercode != null) {
            strippedOwnerUsercode = ownerUsercode.strip();
        }
        String strippedFeedback = feedback;
        if (feedback != null) {
            strippedFeedback = feedback.strip();
        }

        this.feedbackService.submit(offset, referrer, strippedOwnerUsercode, strippedFeedback, browserName, ip);

        return "redirect:/feedback/thanks?next=" + offset;
    }


    @GetMapping("/feedback/thanks")
    public String thanks(Model model, @RequestParam(value="next", required=false) String next) {
        model.addAttribute("FeedbackOffset", next);

        return "feedback/thanks";
    }
}
