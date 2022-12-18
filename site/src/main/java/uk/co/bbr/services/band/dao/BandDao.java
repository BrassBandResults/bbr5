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
@Table(name="band")
public class BandDao extends AbstractDao implements NameTools {

    @Column(name="name", nullable=false)
    private String name;

    @Column(name="old_id")
    private String oldId;

    @Column(name="slug", nullable=false)
    private String slug;

    @Column(name="website")
    private String website;

    @ManyToOne(fetch= FetchType.EAGER, optional=false)
    @JoinColumn(name="region_id")
    private RegionDao region;

    @Column(name="longitude")
    private String longitude;

    @Column(name="latitude")
    private String latitude;

    @Column(name="notes")
    private String notes;

    @Column(name="mapper_id")
    private Long mapperId;

    @Column(name="start_date")
    private LocalDate startDate;

    @Column(name="end_date")
    private LocalDate endDate;

    @Column(name="status")
    private BandStatus status;

    @ManyToOne(fetch= FetchType.EAGER)
    @JoinColumn(name="section_id")
    private SectionDao section;

    @Column(name="twitter_name")
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

    public String getDateRange() {
        if (this.startDate == null && this.endDate == null) {
            return "";
        }

        StringBuilder builder = new StringBuilder();
        if (this.startDate != null) {
            builder.append(this.startDate.getYear());
        }
        builder.append("-");
        if (this.endDate != null) {
            builder.append(this.endDate.getYear());
        }
        return builder.toString();
    }

    public String getSectionType() {
        if (this.section == null) {
              return this.status.getTranslationKey();
        }
        if (this.section.getTranslationKey().equals("section.youth")) {
            return "status.youth";
        }
        return this.section.getTranslationKey();
    }
}
