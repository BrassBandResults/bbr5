package uk.co.bbr.services.contests.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.co.bbr.services.framework.AbstractDao;
import uk.co.bbr.services.framework.mixins.NameTools;
import uk.co.bbr.services.groups.dao.ContestGroupDao;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Getter
@Entity
@NoArgsConstructor
@Table(name="contest_weblink")
public class ContestWeblinkDao extends AbstractDao implements NameTools {

    @Column(name="url", nullable=false)
    private String url;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="contest_id")
    @Setter
    private ContestDao contest;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="contest_group_id")
    @Setter
    private ContestGroupDao contestGroup;
}
