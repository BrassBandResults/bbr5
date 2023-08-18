package uk.co.bbr.web.results.forms;

import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import uk.co.bbr.services.people.PersonService;

@Getter
@Setter
public class AddResultsAdjudicatorForm {
    private String adjudicatorName;
    private String adjudicatorSlug;

    public void validate(BindingResult bindingResult) {
        if (this.adjudicatorSlug == null || this.adjudicatorSlug.trim().length() < 4) {
            bindingResult.addError(new ObjectError("eventDate", "page.add-results.errors.not-found-adjudicator"));
        }
    }
}
