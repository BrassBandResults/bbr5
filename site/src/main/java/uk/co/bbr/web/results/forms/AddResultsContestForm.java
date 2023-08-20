package uk.co.bbr.web.results.forms;

import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

@Getter
@Setter
public class AddResultsContestForm {
    private String contestName;
    private String contestSlug;

    public void validate(BindingResult bindingResult) {
        if (this.contestSlug == null || this.contestSlug.trim().length() < 4) {
            bindingResult.addError(new ObjectError("eventDate", "page.add-results.errors.not-found-contest"));
        }
    }
}
