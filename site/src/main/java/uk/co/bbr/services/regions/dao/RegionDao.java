package uk.co.bbr.services.regions.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Formula;
import uk.co.bbr.services.framework.AbstractDao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name="region")
public class RegionDao extends AbstractDao {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "slug", nullable = false)
    private String slug;

    @Column(name = "container_id")
    private Long containerRegionId;

    @Column(name = "country_code")
    private String countryCode;

    @Column(name = "longitude")
    private String longitude;

    @Column(name = "latitude")
    private String latitude;

    @Column(name = "default_map_zoom")
    private Integer defaultMapZoom;

    @Formula("(SELECT COUNT(*) FROM band b WHERE b.region_id = id)")
    private int bandsCount;

    @Formula("(SELECT COUNT(*) FROM band b WHERE b.region_id = id AND b.status = 0)")
    private int extinctBandsCount;

    @Formula("(SELECT COUNT(*) FROM band b WHERE b.region_id = id AND b.status <> 0)")
    private int activeBandsCount;
}
