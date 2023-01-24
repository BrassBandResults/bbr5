package uk.co.bbr.services.venues.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.co.bbr.services.framework.AbstractDao;
import uk.co.bbr.services.framework.mixins.NameTools;
import uk.co.bbr.services.regions.dao.RegionDao;
import uk.co.bbr.services.security.dao.BbrUserDao;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor
@Table(name="venue")
public class VenueDao extends AbstractDao implements NameTools {
    @Column(name="name", nullable=false)
    private String name;

    @Column(name="old_id")
    private String oldId;

    @Column(name="slug", nullable=false)
    private String slug;

    @ManyToOne(fetch= FetchType.EAGER)
    @JoinColumn(name="region_id")
    @Setter
    private RegionDao region;

    @Column(name="longitude")
    private String longitude;

    @Column(name="latitude")
    private String latitude;

    @Column(name="notes")
    private String notes;

    @Column(name="exact")
    @Setter
    private boolean exact;

    @ManyToOne(fetch= FetchType.EAGER)
    @JoinColumn(name="mapper_id")
    @Setter
    private BbrUserDao mapper;

    @ManyToOne(fetch= FetchType.EAGER)
    @JoinColumn(name="parent_id")
    @Setter
    private VenueDao parent;

    public void setName(String name){
        String nameToSet = simplifyName(name);
        this.name = nameToSet;
    }

    public void setSlug(String value) {
        if (value != null) {
            value = value.trim();
        }
        this.slug = value;
    }

    public void setOldId(String value) {
        if (value != null) {
            value = value.trim();
        }
        this.oldId = value;
    }

    public void setNotes(String notes) {
        if (notes != null) {
            notes = notes.trim();
        }
        this.notes = notes;
    }

    public void setLatitude(String latitude) {
        if (latitude != null) {
            if (latitude.trim().length() > 15) {
                latitude = latitude.trim().substring(0, 15);
            }
            latitude = latitude.trim();
        }
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        if (longitude != null) {
            if (longitude.trim().length() > 15) {
                longitude = longitude.trim().substring(0, 15);
            }
            longitude = longitude.trim();
        }
        this.longitude = longitude;
    }
}
