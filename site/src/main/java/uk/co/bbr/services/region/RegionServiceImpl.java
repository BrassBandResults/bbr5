package uk.co.bbr.services.region;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.framework.mixins.SlugTools;
import uk.co.bbr.services.framework.ValidationException;
import uk.co.bbr.services.region.dao.RegionDao;
import uk.co.bbr.services.region.dao.RegionRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RegionServiceImpl implements RegionService, SlugTools {

    @Autowired
    private RegionRepository regionRepository;

    @Override
    public RegionDao create(RegionDao newRegion) {
        // validation
        if (newRegion.getId() != null) {
            throw new ValidationException("Can't create with specific id");
        }

        if (newRegion.getName() == null || newRegion.getName().trim().length() == 0) {
            throw new ValidationException("Region name must be specified");
        }

        // defaults
        if (newRegion.getSlug() == null || newRegion.getSlug().trim().length() == 0) {
            newRegion.setSlug(slugify(newRegion.getName()));
        }

        return this.regionRepository.save(newRegion);
    }

    @Override
    public RegionDao create(String regionName) {
        RegionDao newRegion = new RegionDao();
        newRegion.setName(regionName);
        return this.create(newRegion);
    }

    @Override
    public List<RegionDao> fetchAll() {
        return this.regionRepository.findAll();
    }

    @Override
    public void delete(RegionDao region) {
        this.regionRepository.delete(region);
    }

    @Override
    public RegionDao fetchUnknownRegion() {
        return this.regionRepository.fetchUnknownRegion();
    }
}
