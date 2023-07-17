package uk.co.bbr.web;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAttribute {

    @ModelAttribute("STATIC_HOST")
    public String staticHost() {
        return "https://" + System.getenv("BBR_STATIC_FILES_HOST");
    }
}
