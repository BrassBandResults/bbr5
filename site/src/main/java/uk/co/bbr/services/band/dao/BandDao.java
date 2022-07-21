package uk.co.bbr.services.band.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.co.bbr.services.framework.AbstractDao;
import uk.co.bbr.services.region.dao.RegionDao;

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
@Table(name="BAND")
public class BandDao extends AbstractDao {

    @Column(name="NAME", nullable=false)
    private String name;

    @Column(name="SLUG", nullable=false)
    private String slug;

    @Column(name="WEBSITE")
    private String website;

    @ManyToOne(fetch= FetchType.EAGER, optional=false)
    @JoinColumn(name="REGION_ID")
    private RegionDao region;

    @Column(name="LONGITUDE")
    private String longitude;

    @Column(name="LATITUDE")
    private String latitude;

    @Column(name="NOTES")
    private String notes;

    @Column(name="MAPPER_ID")
    private Long mapperId;

    @Column(name="START_DATE")
    private LocalDate startDate;

    @Column(name="END_DATE")
    private LocalDate endDate;

    @Column(name="STATUS")
    private Long status;

    @Column(name="NATIONAL_GRADING")
    private String nationalGrading;

    @Column(name="TWITTER_NAME")
    private String twitterName;

    @Column(name="SCRATCH_BAND")
    private boolean scratchBand;
}
