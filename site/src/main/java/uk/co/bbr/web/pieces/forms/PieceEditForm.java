package uk.co.bbr.web.pieces.forms;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PieceEditForm {
    private String name;
    private String notes;
    private String year;
    private String category;
    private String composer;
    private String arranger;
}
