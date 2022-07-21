package uk.co.bbr.services.band.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.co.bbr.services.framework.AbstractDao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name="BAND_PREVIOUS_NAME")
public class BandPreviousNameDao extends AbstractDao {

    @Column(name="OLD_NAME", nullable=false)
    private String oldName;

    @Column(name="START_DATE")
    private LocalDate startDate;

    @Column(name="END_DATE")
    private LocalDate endDate;

    @Column(name="HIDDEN")
    private boolean hidden;
}
