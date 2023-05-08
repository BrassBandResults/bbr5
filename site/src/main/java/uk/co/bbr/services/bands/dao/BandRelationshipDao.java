package uk.co.bbr.services.bands.dao;

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
            value = value.trim();
        }
        this.rightBandName = value;
    }

    public void setLeftBandName(String value) {
        if (value != null) {
            value = value.trim();
        }
        this.leftBandName = value;
    }
}
