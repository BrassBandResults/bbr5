package uk.co.bbr.services.performances.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.co.bbr.services.events.dao.ContestResultDao;
import uk.co.bbr.services.framework.AbstractDao;
import uk.co.bbr.services.framework.mixins.NameTools;
import uk.co.bbr.services.performances.types.Instrument;
import uk.co.bbr.services.performances.types.PerformanceStatus;

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
@Table(name="personal_contest_history")
public class PerformanceDao extends AbstractDao implements NameTools {
    @Column(name="old_id")
    private String oldId;

    @ManyToOne(fetch= FetchType.EAGER)
    @JoinColumn(name="result_id")
    private ContestResultDao result;

    @Column(name="status", nullable=false)
    private PerformanceStatus status;

    @Column(name="instrument")
    private Instrument instrument;
}
