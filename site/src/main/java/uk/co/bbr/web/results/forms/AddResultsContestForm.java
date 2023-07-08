package uk.co.bbr.web.results.forms;

import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.BindingResult;

@Getter
@Setter
public class AddResultsContestForm {
    private String contestName;
    private String contestSlug;

    public void validate(BindingResult bindingResult) {
        // TODO validate that name is entered
    }
}
