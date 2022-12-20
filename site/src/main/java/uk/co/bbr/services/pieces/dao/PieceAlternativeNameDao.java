package uk.co.bbr.services.pieces.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.co.bbr.services.framework.AbstractDao;

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
@Table(name="piece_alternative_name")
public class PieceAlternativeNameDao extends AbstractDao {

    @ManyToOne(fetch= FetchType.EAGER, optional=false)
    @JoinColumn(name="piece_id")
    private PieceDao piece;

    @Column(name="name", nullable=false)
    private String name;

    @Column(name="hidden")
    private boolean hidden;

    public void setName(String name) {
        if (name != null) {
            this.name = name.trim();
        }
    }
}
