package uk.co.bbr.services.region;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.services.framework.mixins.SlugTools;
import uk.co.bbr.services.region.dao.RegionDao;
import uk.co.bbr.services.region.dao.RegionRepository;
import uk.co.bbr.services.region.dto.RegionPageDto;
import uk.co.bbr.services.region.dto.RegionListDto;
import uk.co.bbr.services.region.sql.RegionSqlService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RegionServiceImpl implements RegionService, SlugTools {

    private final RegionRepository regionRepository;
    private final RegionSqlService regionSqlService;

    @Override
    public List<RegionDao> fetchAll() {
        return this.regionRepository.findAll();
    }

    @Override
    public RegionDao fetchUnknownRegion() {
        return this.regionRepository.fetchUnknownRegion();
    }

    @Override
    public RegionDao findBySlug(String slug) {
        Optional<RegionDao> region = this.regionRepository.findBySlug(slug);
        if (region.isEmpty()) {
            throw new NotFoundException("Region with slug " + slug + " not found");
        }
        return region.get();
    }

    @Override
    public List<RegionListDto> fetchRegionsForListPage() {
        return this.regionRepository.fetchRegionsForList();
    }

    @Override
    public RegionPageDto findBySlugForPage(String regionSlug) {
        return this.regionSqlService.findBySlugForPage(regionSlug);
    }
}
