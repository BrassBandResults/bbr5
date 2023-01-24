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
@Entity
@NoArgsConstructor
@Table(name="piece_alias")
public class PieceAlias extends AbstractDao {

    @ManyToOne(fetch= FetchType.EAGER, optional=false)
    @JoinColumn(name="piece_id")
    @Setter
    private PieceDao piece;

    @Column(name="name", nullable=false)
    private String name;

    @Column(name="hidden")
    @Setter
    private boolean hidden;

    public void setName(String name) {
        if (name != null) {
            this.name = name.trim();
        }
    }
}
