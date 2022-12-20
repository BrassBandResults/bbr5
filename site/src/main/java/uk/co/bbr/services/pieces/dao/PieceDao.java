package uk.co.bbr.services.pieces.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.co.bbr.services.framework.AbstractDao;
import uk.co.bbr.services.pieces.types.PieceCategory;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name="piece")
public class PieceDao extends AbstractDao {
    @Column(name="old_id")
    private String oldId;

    @Column(name="name")
    private String name;

    @Column(name="slug", nullable=false)
    private String slug;

    @Column(name="notes")
    private String notes;

    @Column(name="piece_year")
    private String year;

    @Column(name="category")
    private PieceCategory category;

    public void setNotes(String notes) {
        if (notes != null) {
            this.notes = notes.trim();
        }
    }

    public void setOldId(String oldId) {
        if (oldId != null) {
            this.oldId = oldId.trim();
        }
    }

    public void setName(String name) {
        if (name != null) {
            this.name = name.trim();
        }
    }

    public void setYear(String year) {
        if (year != null) {
            this.year = year.trim();
        }
    }
}
