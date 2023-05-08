package uk.co.bbr.services.contests.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.co.bbr.services.contests.types.ContestEventDateResolution;
import uk.co.bbr.services.framework.AbstractDao;
import uk.co.bbr.services.framework.mixins.NameTools;
import uk.co.bbr.services.venues.dao.VenueDao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDate;

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
