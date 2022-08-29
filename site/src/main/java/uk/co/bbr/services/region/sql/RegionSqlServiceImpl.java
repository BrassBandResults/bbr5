package uk.co.bbr.services.region.sql;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.services.region.dao.RegionDao;
import uk.co.bbr.services.region.dao.RegionRepository;
import uk.co.bbr.services.region.dto.RegionPageDto;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RegionSqlServiceImpl implements RegionSqlService {

    private final RegionRepository regionRepository;
    private final EntityManager entityManager;
    @Override
    public RegionPageDto findBySlugForPage(String regionSlug) {
        Optional<RegionDao> region = this.regionRepository.findBySlug(regionSlug);
        if (region.isEmpty()) {
            throw new NotFoundException("Region with slug " + regionSlug + " not found");
        }

        int activeBandsCount = this.regionRepository.countActiveForRegion(regionSlug);
        int extinctBandsCount = this.regionRepository.countExtinctForRegion(regionSlug);

        return new RegionPageDto(region.get(), activeBandsCount, extinctBandsCount);
    }
}
