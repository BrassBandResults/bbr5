package uk.co.bbr.services.events.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.co.bbr.services.framework.AbstractDao;
import uk.co.bbr.services.framework.mixins.NameTools;
import uk.co.bbr.services.pieces.dao.PieceDao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

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
}
