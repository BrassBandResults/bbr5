package uk.co.bbr.services.region;

import uk.co.bbr.services.band.dao.BandDao;
import uk.co.bbr.services.region.dao.RegionDao;
import uk.co.bbr.services.region.dto.LinkSectionDto;
import uk.co.bbr.services.region.dto.RegionPageDto;

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
