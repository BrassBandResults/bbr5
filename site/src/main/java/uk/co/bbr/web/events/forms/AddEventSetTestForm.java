package uk.co.bbr.web.events.forms;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import uk.co.bbr.services.events.dao.ContestEventDao;
import uk.co.bbr.services.events.types.TestPieceAndOr;

import java.time.LocalDate;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;

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
        if (this.pieceSlug == null || this.pieceSlug.trim().length() == 0) {
            bindingResult.addError(new ObjectError("name", "page.add-set-tests.errors.piece-required"));
        }
    }
}
