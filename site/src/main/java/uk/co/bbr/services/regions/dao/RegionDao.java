package uk.co.bbr.services.regions.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Formula;
import uk.co.bbr.services.framework.AbstractDao;
import uk.co.bbr.services.framework.mixins.NameTools;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

@Getter
@Entity
@NoArgsConstructor
@Table(name="region")
public class RegionDao extends AbstractDao implements NameTools {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "slug", nullable = false)
    private String slug;

    @Column(name = "container_id")
    @Setter
    private Long containerRegionId;

    @Column(name = "country_code")
    private String countryCode;

    @Column(name = "longitude")
    private String longitude;

    @Column(name = "latitude")
    private String latitude;

    @Column(name = "default_map_zoom")
    @Setter
    private Integer defaultMapZoom;

    @Transient
    @Setter
    private int bandsCount;

    @Transient
    @Setter
    private int extinctBandsCount;

    @Transient
    @Setter
    private int activeBandsCount;

    @Transient
    private int subRegionBandsCount;

    @Transient
    private int subRegionExtinctBandsCount;

    @Transient
    private int subRegionActiveBandsCount;

    public void setName(String name){
        if (name == null) {
            this.name = null;
        } else {
            this.name = simplifyRegionName(name);
        }
    }

    public void setSlug(String value) {
        if (value != null) {
            value = value.strip();
        }
        this.slug = value;
    }

    public void setCountryCode(String value) {
        if (value != null) {
            value = value.strip();
        }
        this.countryCode = value;
    }
}
