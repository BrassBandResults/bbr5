package uk.co.bbr.services.events.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.parameters.P;
import uk.co.bbr.services.framework.AbstractDao;
import uk.co.bbr.services.framework.mixins.NameTools;
import uk.co.bbr.services.pieces.dao.PieceDao;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Getter
@Entity
@NoArgsConstructor
@Table(name="contest_result_test_piece")
public class ContestResultPieceDao extends AbstractDao implements NameTools {

    @ManyToOne(fetch=FetchType.EAGER, optional=false)
    @JoinColumn(name="contest_result_id")
    @Setter
    private ContestResultDao contestResult;

    @ManyToOne(fetch=FetchType.EAGER, optional=false)
    @JoinColumn(name="piece_id")
    @Setter
    private PieceDao piece;

    @Column(name="ordering", nullable=false)
    @Setter
    private int ordering;

    @Column(name="suffix")
    private String suffix;

    public void setSuffix(String suffix) {
        if (suffix == null) {
            this.suffix = null;
        } else {
            this.suffix = suffix.strip();
        }
    }
}
