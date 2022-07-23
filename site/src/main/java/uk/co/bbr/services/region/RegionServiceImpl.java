package uk.co.bbr.services.region;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.services.framework.mixins.SlugTools;
import uk.co.bbr.services.framework.ValidationException;
import uk.co.bbr.services.region.dao.RegionDao;
import uk.co.bbr.services.region.dao.RegionRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RegionServiceImpl implements RegionService, SlugTools {

    @Autowired
    private RegionRepository regionRepository;

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
}
