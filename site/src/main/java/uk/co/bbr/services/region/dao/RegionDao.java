package uk.co.bbr.services.region.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.co.bbr.services.framework.AbstractDao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name="REGION")
public class RegionDao extends AbstractDao {

    @Column(name="NAME", nullable=false)
    private String name;

    @Column(name="SLUG", nullable=false)
    private String slug;

    @Column(name="CONTAINER_ID")
    private Long containerRegionId;

    @Column(name="COUNTRY_CODE")
    private String countryCode;

    @Column(name="LONGITUDE")
    private String longitude;

    @Column(name="LATITUDE")
    private String latitude;

    @Column(name="DEFAULT_MAP_ZOOM")
    private Integer defaultMapZoom;
}
