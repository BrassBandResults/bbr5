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
@Table(name="BAND_RELATIONSHIP")
public class BandRelationshipDao extends AbstractDao {

    @Column(name="NAME", nullable=false)
    private String name;

    @ManyToOne(fetch= FetchType.EAGER, optional=false)
    @JoinColumn(name="LEFT_BAND_ID")
    private BandDao leftBand;

    @Column(name="LEFT_BAND_NAME")
    private String leftBandName;

    @ManyToOne(fetch= FetchType.EAGER, optional=false)
    @JoinColumn(name="RELATIONSHIP_ID")
    private BandRelationshipDao relationship;

    @ManyToOne(fetch= FetchType.EAGER, optional=false)
    @JoinColumn(name="RIGHT_BAND_ID")
    private BandDao rightBand;

    @Column(name="RIGHT_BAND_NAME")
    private String rightBandName;

    @Column(name="START_DATE")
    private LocalDate startDate;

    @Column(name="END_DATE")
    private LocalDate endDate;
}
