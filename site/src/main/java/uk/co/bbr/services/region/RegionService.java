package uk.co.bbr.services.region;

import uk.co.bbr.services.region.dao.RegionDao;

import java.util.List;

public interface RegionService {
    List<RegionDao> fetchAll();

    RegionDao fetchUnknownRegion();

    RegionDao findBySlug(String slug);
}
