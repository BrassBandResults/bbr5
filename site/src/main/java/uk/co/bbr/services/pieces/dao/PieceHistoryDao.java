package uk.co.bbr.services.pieces.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.co.bbr.services.framework.AbstractDao;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.pieces.types.PieceCategory;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name="piece_history")
public class PieceHistoryDao extends AbstractDao {

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="piece_id", nullable=false)
    private PieceDao piece;

    @Column(name="name")
    private String name;

    @Column(name="piece_year")
    private String year;

    @Column(name="duration_minutes")
    private Integer durationMinutes;

    @Column(name="percussion_requirements")
    private String percussionRequirements;

    @Column(name="category")
    private PieceCategory category;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="composer_id")
    private PersonDao composer;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="arranger_id")
    private PersonDao arranger;

    @Column(name="notes")
    private String notes;

    public PieceHistoryDao(PieceDao piece) {
        this.piece = piece;
        this.name = piece.getName();
        this.year = piece.getYear();
        this.durationMinutes = piece.getDurationMinutes();
        this.percussionRequirements = piece.getPercussionRequirements();
        this.category = piece.getCategory();
        this.composer = piece.getComposer();
        this.arranger = piece.getArranger();
        this.notes = piece.getNotes();
        this.setCreated(piece.getUpdated());
        this.setCreatedBy(piece.getUpdatedBy());
        this.setUpdated(piece.getUpdated());
        this.setUpdatedBy(piece.getUpdatedBy());
    }
}
