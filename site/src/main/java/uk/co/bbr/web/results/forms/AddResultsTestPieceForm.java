package uk.co.bbr.web.results.forms;

import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

@Getter
@Setter
public class AddResultsTestPieceForm {
    private String testPieceName;
    private String testPieceSlug;

    public void validate(BindingResult bindingResult) {
        if (this.testPieceSlug == null || this.testPieceSlug.trim().length() < 4) {
            bindingResult.addError(new ObjectError("eventDate", "page.add-results.errors.not-found-piece"));
        }
    }
}
