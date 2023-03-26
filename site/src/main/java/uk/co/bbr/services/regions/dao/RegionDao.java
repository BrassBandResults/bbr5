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

    @Formula("(SELECT COUNT(*) FROM band b WHERE b.region_id = id)")
    private int bandsCount;

    @Formula("(SELECT COUNT(*) FROM band b WHERE b.region_id = id AND b.status = 0)")
    private int extinctBandsCount;

    @Formula("(SELECT COUNT(*) FROM band b WHERE b.region_id = id AND b.status <> 0)")
    private int activeBandsCount;


    @Formula("(SELECT COUNT(*) FROM band b WHERE b.region_id IN (SELECT r.id FROM region r WHERE r.container_id = id))")
    private int subRegionBandsCount;

    @Formula("(SELECT COUNT(*) FROM band b WHERE b.region_id IN (SELECT r.id FROM region r WHERE r.container_id = id) AND b.status = 0)")
    private int subRegionExtinctBandsCount;

    @Formula("(SELECT COUNT(*) FROM band b WHERE b.region_id IN (SELECT r.id FROM region r WHERE r.container_id = id) AND b.status <> 0)")
    private int subRegionActiveBandsCount;

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

    public void setCountryCode(String value) {
        if (value != null) {
            value = value.trim();
        }
        this.countryCode = value;
    }
}
