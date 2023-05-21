package uk.co.bbr.services.regions;

import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.regions.dao.RegionDao;
import uk.co.bbr.services.regions.dto.LinkSectionDto;
import uk.co.bbr.services.regions.dto.RegionPageDto;

import java.util.List;
import java.util.Optional;

public interface RegionService {
    List<RegionDao> findAll();

    RegionDao fetchUnknownRegion();

    Optional<RegionDao> fetchBySlug(String slug);

    RegionPageDto fetchBySlugForPage(String regionSlug);

    List<LinkSectionDto> findBandsBySection(RegionDao region, String ungradedDescription);

    List<RegionDao> findSubRegions(RegionDao region);

    List<BandDao> findBandsWithMapLocation(RegionDao region);

    RegionDao create(RegionDao region);

    RegionDao create(String regionName);

    RegionDao create(String regionName, RegionDao parent);

    List<ContestDao> findContestsForRegion(RegionDao region);

    Optional<RegionDao> fetchById(Long regionId);
}
