package uk.co.bbr.services.events.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.co.bbr.services.framework.AbstractDao;
import uk.co.bbr.services.framework.mixins.NameTools;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Getter
@Entity
@NoArgsConstructor
@Table(name="contest_result_award")
public class ContestResultAwardDao extends AbstractDao implements NameTools {

    @ManyToOne(fetch=FetchType.EAGER, optional=false)
    @JoinColumn(name="contest_result_id")
    @Setter
    private ContestResultDao contestResult;

    @ManyToOne(fetch=FetchType.EAGER, optional=false)
    @JoinColumn(name="award_type_id")
    @Setter
    private ContestResultAwardTypeDao awardType;

    @Column(name="description", nullable=false)
    private String description;

}
