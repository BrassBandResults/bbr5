package uk.co.brassbandresults.extract.json;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import uk.co.brassbandresults.extract.data.PieceData;

@Getter
@JsonPropertyOrder(alphabetic = true)
public class PieceJson {
    private final String name;
    private final String slug;
    private final String year;
    private final String composerSlug;
    private final String composerFirstNames;
    private final String composerSurname;
    private final String arrangerSlug;
    private final String arrangerFirstNames;
    private final String arrangerSurname;
    private final String notes;
    private final String andOr;
    private final String resultId;
    private final String ordering;

    public PieceJson(PieceData eachPiece) {
        this.name = eachPiece.getName();
        this.slug = eachPiece.getSlug();
        this.year = eachPiece.getYear();
        this.composerSlug = eachPiece.getComposerSlug();
        this.composerFirstNames = eachPiece.getComposerFirstNames();
        this.composerSurname = eachPiece.getComposerSurname();
        this.arrangerSlug = eachPiece.getArrangerSlug();
        this.arrangerFirstNames = eachPiece.getArrangerFirstNames();
        this.arrangerSurname = eachPiece.getArrangerSurname();
        this.notes = eachPiece.getNotes();
        this.andOr = eachPiece.getAndOr();
        this.resultId = eachPiece.getResultId();
        this.ordering = eachPiece.getOrdering();

    }
}
