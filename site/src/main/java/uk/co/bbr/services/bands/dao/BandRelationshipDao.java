package uk.co.bbr.services.bands.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.co.bbr.services.framework.AbstractDao;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Getter
@Entity
@NoArgsConstructor
@Table(name="band_relationship")
public class BandRelationshipDao extends AbstractDao {

    @ManyToOne(fetch=FetchType.EAGER, optional=false)
    @JoinColumn(name="left_band_id")
    @Setter
    private BandDao leftBand;

    @Column(name="left_band_name")
    private String leftBandName;

    @ManyToOne(fetch=FetchType.EAGER, optional=false)
    @JoinColumn(name="relationship_id")
    @Setter
    private BandRelationshipTypeDao relationship;

    @ManyToOne(fetch=FetchType.EAGER, optional=false)
    @JoinColumn(name="right_band_id")
    @Setter
    private BandDao rightBand;

    @Column(name="right_band_name")
    private String rightBandName;

    @Setter
    @Column(name="start_date")
    private LocalDate startDate;

    @Setter
    @Column(name="end_date")
    private LocalDate endDate;

    public void setRightBandName(String value) {
        if (value != null) {
            value = value.strip();
        }
        this.rightBandName = value;
    }

    public void setLeftBandName(String value) {
        if (value != null) {
            value = value.strip();
        }
        this.leftBandName = value;
    }

    public String relationshipName(BandDao band) {
        if (this.leftBand.getId().equals(band.getId())) {
            return this.relationship.getName();
        } else {
            return this.relationship.getReverseName();
        }
    }

    public BandDao otherBand(BandDao band) {
        if (this.leftBand.getId().equals(band.getId())) {
            return this.rightBand;
        } else {
            return this.leftBand;
        }
    }

    public String otherBandName(BandDao band) {
        if (this.leftBand.getId().equals(band.getId())) {
            return this.rightBandName;
        } else {
            return this.leftBandName;
        }
    }
}
