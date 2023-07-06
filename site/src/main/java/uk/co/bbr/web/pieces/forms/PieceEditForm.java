package uk.co.bbr.web.pieces.forms;

import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.pieces.dao.PieceDao;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;

@Getter
@Setter
public class PieceEditForm {
    private String name;
    private String notes;
    private String year;
    private String category;
    private String composer;
    private String arranger;

    public PieceEditForm() {
        super();
    }

    public PieceEditForm(PieceDao piece) {
        assertNotNull(piece);

        this.name = piece.getName();
        this.notes = piece.getNotes();
        this.year = piece.getYear();
        this.category = piece.getCategory().getCode();
        this.composer = piece.getComposer().getName();
        this.arranger = piece.getArranger().getName();
    }

    public void validate(BindingResult bindingResult) {
        if (this.name == null || this.name.trim().length() == 0) {
            bindingResult.addError(new ObjectError("name", "page.contest-edit.errors.name-required"));
        }
    }
}
