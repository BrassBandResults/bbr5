package uk.co.bbr.services.region;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.region.dao.RegionDao;
import uk.co.bbr.services.region.dao.RegionRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RegionServiceImpl implements RegionService {

    @Autowired
    private RegionRepository regionRepository;

    @Override
    public RegionDao createRegion(RegionDao newRegion) {
        return this.regionRepository.save(newRegion);
    }

    @Override
    public List<RegionDao> fetchAllRegions() {
        return this.regionRepository.findAll();
    }

    @Override
    public void deleteRegion(RegionDao region) {
        this.regionRepository.delete(region);
    }
}
