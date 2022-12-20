package uk.co.bbr.services.bands.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.co.bbr.services.bands.types.RehearsalDay;
import uk.co.bbr.services.framework.AbstractDao;

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
@Table(name="band_rehearsal_day")
public class BandRehearsalDayDao extends AbstractDao {

    @Column(name="day_number", nullable=false)
    private RehearsalDay day;

    @ManyToOne(fetch=FetchType.EAGER, optional=false)
    @JoinColumn(name="band_id")
    private BandDao band;
}
