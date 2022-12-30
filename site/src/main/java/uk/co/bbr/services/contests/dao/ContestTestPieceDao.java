package uk.co.bbr.services.contests.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.co.bbr.services.contests.types.TestPieceAndOr;
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
@Setter
@Entity
@NoArgsConstructor
@Table(name="contest_test_piece")
public class ContestTestPieceDao extends AbstractDao implements NameTools {
    @ManyToOne(fetch= FetchType.EAGER, optional=false)
    @JoinColumn(name="contest_event_id")
    private ContestEventDao contestEvent;

    @ManyToOne(fetch= FetchType.EAGER, optional=false)
    @JoinColumn(name="piece_id")
    private PieceDao piece;

    @Column(name="and_or")
    private TestPieceAndOr andOr;
}
