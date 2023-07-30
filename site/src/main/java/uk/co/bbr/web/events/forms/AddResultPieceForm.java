package uk.co.bbr.web.events.forms;

import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

@Getter
@Setter
public class AddResultPieceForm {
    private String pieceSlug;
    private String pieceName;
    private String suffix;

    public AddResultPieceForm() {
        super();
    }

    public void validate(BindingResult bindingResult) {
        if (this.pieceSlug == null || this.pieceSlug.trim().length() == 0) {
            bindingResult.addError(new ObjectError("name", "page.add-set-tests.errors.piece-required"));
        }
    }
}
