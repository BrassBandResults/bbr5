package uk.co.bbr.services.region.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Formula;
import uk.co.bbr.services.band.dao.BandDao;
import uk.co.bbr.services.framework.AbstractDao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

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

    @Formula("SELECT COUNT(*) FROM band b WHERE b.region_id = id")
    private int bandsCount;

    @Formula("SELECT COUNT(*) FROM band b WHERE b.region_id = id AND b.status = 0")
    private int extinctBandsCount;

    @Formula("SELECT COUNT(*) FROM band b WHERE b.region_id = id AND b.status <> 0")
    private int activeBandsCount;
}
