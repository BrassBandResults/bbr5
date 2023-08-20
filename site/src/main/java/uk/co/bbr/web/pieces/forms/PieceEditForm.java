package uk.co.bbr.web.pieces.forms;

import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import uk.co.bbr.services.pieces.dao.PieceDao;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;

@Getter
@Setter
public class PieceEditForm {
    private String name;
    private String notes;
    private String year;
    private String category;
    private String composerName;
    private String composerSlug;
    private String arrangerName;
    private String arrangerSlug;

    public PieceEditForm() {
        super();
    }

    public PieceEditForm(PieceDao piece) {
        assertNotNull(piece);

        this.name = piece.getName();
        this.notes = piece.getNotes();
        this.year = piece.getYear();
        this.category = piece.getCategory().getCode();
        if (piece.getComposer() != null) {
            this.composerName = piece.getComposer().getName();
            this.composerSlug = piece.getComposer().getSlug();
        }
        if (piece.getArranger() != null) {
            this.arrangerName = piece.getArranger().getName();
            this.arrangerSlug = piece.getArranger().getSlug();
        }
    }

    public void validate(BindingResult bindingResult) {
        if (this.name == null || this.name.strip().length() == 0) {
            bindingResult.addError(new ObjectError("name", "page.piece-edit.errors.name-required"));
        }
    }
}
