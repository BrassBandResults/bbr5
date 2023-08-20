package uk.co.bbr.web.events.forms;

import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

@Getter
@Setter
public class AddEventSetTestForm {
    private String pieceSlug;
    private String pieceName;
    private String andOr;

    public AddEventSetTestForm() {
        super();
    }

    public void validate(BindingResult bindingResult) {
        if (this.pieceSlug == null || this.pieceSlug.strip().length() == 0) {
            bindingResult.addError(new ObjectError("name", "page.add-set-tests.errors.piece-required"));
        }
    }
}
