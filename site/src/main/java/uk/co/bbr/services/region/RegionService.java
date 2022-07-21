package uk.co.bbr.services.region;

import uk.co.bbr.services.region.dao.RegionDao;

import java.util.List;

public interface RegionService {
    RegionDao create(RegionDao newRegion);

    RegionDao create(String regionName);

    List<RegionDao> fetchAll();

    void delete(RegionDao createdRegion);

    RegionDao fetchUnknownRegion();
}
