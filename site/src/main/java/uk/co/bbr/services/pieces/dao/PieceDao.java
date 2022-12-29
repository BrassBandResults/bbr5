package uk.co.bbr.services.pieces.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Formula;
import uk.co.bbr.services.framework.AbstractDao;
import uk.co.bbr.services.framework.mixins.NameTools;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.pieces.types.PieceCategory;
import uk.co.bbr.services.sections.dao.SectionDao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name="piece")
public class PieceDao extends AbstractDao implements NameTools {
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

    @ManyToOne(fetch= FetchType.EAGER)
    @JoinColumn(name="composer_id")
    private PersonDao composer;

    @ManyToOne(fetch= FetchType.EAGER)
    @JoinColumn(name="arranger_id")
    private PersonDao arranger;

    @Formula("0")
    private int setTestCount;

    @Formula("0")
    private int ownChoiceCount;

    public void setNotes(String notes) {
        if (notes != null) {
            notes = notes.trim();
        }
        this.notes = notes;
    }

    public void setOldId(String oldId) {
        if (oldId != null) {
            oldId = oldId.trim();
        }
        this.oldId = oldId;
    }

    public void setName(String name) {
        String nameToSet = simplifyName(name);
        this.name = nameToSet;
    }

    public void setYear(String year) {
        if (year != null) {
            year = year.trim();
        }
        this.year = year;
    }
}
