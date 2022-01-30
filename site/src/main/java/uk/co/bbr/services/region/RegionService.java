package uk.co.bbr.services.region;

import uk.co.bbr.services.region.dao.RegionDao;

import java.util.List;

public interface RegionService {
    RegionDao createRegion(RegionDao newRegion);

    List<RegionDao> fetchAllRegions();

    void deleteRegion(RegionDao createdRegion);
}
