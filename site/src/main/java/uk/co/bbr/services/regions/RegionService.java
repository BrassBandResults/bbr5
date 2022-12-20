package uk.co.bbr.services.regions;

import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.regions.dao.RegionDao;
import uk.co.bbr.services.regions.dto.LinkSectionDto;
import uk.co.bbr.services.regions.dto.RegionPageDto;

import java.util.List;

public interface RegionService {
    List<RegionDao> fetchAll();

    RegionDao fetchUnknownRegion();

    RegionDao findBySlug(String slug);

    RegionPageDto findBySlugForPage(String regionSlug);

    List<LinkSectionDto> fetchBandsBySection(RegionDao region, String ungradedDescription);

    List<RegionDao> fetchSubRegions(RegionDao region);

    List<BandDao> fetchBandsWithMapLocation(RegionDao region);
}
