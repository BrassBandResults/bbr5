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
@Entity
@NoArgsConstructor
@Table(name="contest_event_test_piece")
public class ContestEventTestPieceDao extends AbstractDao implements NameTools {
    @ManyToOne(fetch= FetchType.EAGER, optional=false)
    @JoinColumn(name="contest_event_id")
    @Setter
    private ContestEventDao contestEvent;

    @ManyToOne(fetch= FetchType.EAGER, optional=false)
    @JoinColumn(name="piece_id")
    @Setter
    private PieceDao piece;

    @Column(name="and_or")
    @Setter
    private TestPieceAndOr andOr;
}
