package uk.co.bbr.web.results.forms;

import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.BindingResult;

@Getter
@Setter
public class AddResultsTestPieceForm {
    private String testPieceName;
    private String testPieceSlug;

    public void validate(BindingResult bindingResult) {
        // TODO validate that it's a sensible date
    }
}
