package uk.co.bbr.services.band.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.co.bbr.services.band.types.BandStatus;
import uk.co.bbr.services.framework.AbstractDao;
import uk.co.bbr.services.framework.mixins.NameTools;
import uk.co.bbr.services.region.dao.RegionDao;
import uk.co.bbr.services.section.dao.SectionDao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name="BAND")
public class BandDao extends AbstractDao implements NameTools {

    @Column(name="NAME", nullable=false)
    private String name;

    @Column(name="OLD_ID")
    private String oldId;

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
    private BandStatus status;

    @ManyToOne(fetch= FetchType.EAGER)
    @JoinColumn(name="SECTION_ID")
    private SectionDao section;

    @Column(name="TWITTER_NAME")
    private String twitterName;

    public void setName(String sourceName) {
        String nameToSet = simplifyName(sourceName);
        this.name = nameToSet;
    }

    public void setLatitude(String latitude) {
        if (latitude != null) {
            if (latitude.trim().length() > 15) {
                latitude = latitude.trim().substring(0, 15);
            }
            this.latitude = latitude.trim();
        }
    }

    public void setLongitude(String longitude) {
        if (longitude != null) {
            if (longitude.trim().length() > 15) {
                longitude = longitude.trim().substring(0, 15);
            }
            this.longitude = longitude.trim();
        }
    }

    public void setNotes(String notes) {
        if (notes != null) {
            this.notes = notes.trim();
        }
    }

    public void setWebsite(String website) {
        if (website != null) {
            this.website = website.trim();
        }
    }

    public void setTwitterName(String twitterName) {
        if (twitterName != null) {
            if (twitterName.trim().startsWith("@")) {
                twitterName = twitterName.trim().substring(1);
            }

            this.twitterName = twitterName.trim();
        }
    }
}
