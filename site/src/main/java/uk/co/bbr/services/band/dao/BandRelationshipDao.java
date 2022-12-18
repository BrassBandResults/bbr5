package uk.co.bbr.services.band.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.co.bbr.services.framework.AbstractDao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name="band_relationship")
public class BandRelationshipDao extends AbstractDao {

    @ManyToOne(fetch= FetchType.EAGER, optional=false)
    @JoinColumn(name="left_band_id")
    private BandDao leftBand;

    @Column(name="left_band_name")
    private String leftBandName;

    @ManyToOne(fetch= FetchType.EAGER, optional=false)
    @JoinColumn(name="relationship_id")
    private BandRelationshipTypeDao relationship;

    @ManyToOne(fetch= FetchType.EAGER, optional=false)
    @JoinColumn(name="right_band_id")
    private BandDao rightBand;

    @Column(name="right_band_name")
    private String rightBandName;

    @Column(name="start_date")
    private LocalDate startDate;

    @Column(name="end_date")
    private LocalDate endDate;
}
