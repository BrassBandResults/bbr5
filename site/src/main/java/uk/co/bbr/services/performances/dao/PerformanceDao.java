package uk.co.bbr.services.performances.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.co.bbr.services.events.dao.ContestResultDao;
import uk.co.bbr.services.framework.AbstractDao;
import uk.co.bbr.services.framework.mixins.NameTools;
import uk.co.bbr.services.performances.dto.CompetitorDto;
import uk.co.bbr.services.performances.types.Instrument;
import uk.co.bbr.services.performances.types.PerformanceStatus;

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
