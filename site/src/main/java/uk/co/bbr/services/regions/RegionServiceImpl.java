package uk.co.bbr.services.regions;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.bands.sql.BandMapSql;
import uk.co.bbr.services.bands.sql.dto.BandListSqlDto;
import uk.co.bbr.services.bands.sql.dto.RegionBandSqlDto;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.contests.repo.ContestRepository;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.services.framework.ValidationException;
import uk.co.bbr.services.framework.mixins.SlugTools;
import uk.co.bbr.services.regions.dao.RegionDao;
import uk.co.bbr.services.regions.repo.RegionRepository;
import uk.co.bbr.services.regions.dto.LinkSectionDto;
import uk.co.bbr.services.regions.dto.RegionPageDto;
import uk.co.bbr.services.regions.sql.RegionSql;
import uk.co.bbr.services.regions.sql.dto.BandListForRegionSqlDto;
import uk.co.bbr.services.regions.sql.dto.ContestListForRegionSqlDto;
import uk.co.bbr.services.regions.sql.dto.RegionListSqlDto;
import uk.co.bbr.services.sections.dao.SectionDao;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.web.security.annotations.IsBbrMember;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RegionServiceImpl implements RegionService, SlugTools {

    private final RegionRepository regionRepository;
    private final ContestRepository contestRepository;
    private final SecurityService securityService;
    private final EntityManager entityManager;

    @Override
    public List<RegionDao> findAll() {
        List<RegionDao> returnRegions = new ArrayList<>();
        List<RegionListSqlDto> regions = RegionSql.listAll(this.entityManager);
        for (RegionListSqlDto region : regions) {
            returnRegions.add(region.asRegion());
        }
        return returnRegions;
    }

    @Override
    public List<RegionDao> findAllWithCounts() {
        List<RegionDao> returnRegions = new ArrayList<>();
        List<RegionListSqlDto> regions = RegionSql.listAllWithCounts(this.entityManager);
        for (RegionListSqlDto region : regions) {
            returnRegions.add(region.asRegion());
        }
        return returnRegions;
    }

    @Override
    public RegionDao fetchUnknownRegion() {
        return this.regionRepository.fetchUnknownRegion();
    }

    @Override
    public Optional<RegionDao> fetchBySlug(String slug) {
        return this.regionRepository.fetchBySlug(slug);
    }

    @Override
    public RegionPageDto fetchBySlugForPage(String regionSlug) {
        Optional<RegionDao> region = this.regionRepository.fetchBySlug(regionSlug);
        if (region.isEmpty()) {
            throw NotFoundException.regionNotFoundBySlug(regionSlug);
        }

        List<BandListForRegionSqlDto> bandsForRegionSql = RegionSql.listBandsForRegion(this.entityManager, region.get().getId());
        List<BandDao> bandsForThisRegion = new ArrayList<>();
        for (BandListForRegionSqlDto band : bandsForRegionSql) {
            bandsForThisRegion.add(band.asBand());
        }

        return new RegionPageDto(region.get(), bandsForThisRegion);
    }

    @Override
    public List<LinkSectionDto> findBandsBySection(RegionDao region, String ungradedTranslationKey) {
        List<LinkSectionDto> returnList = new ArrayList<>();

        SectionDao ungradedSection = new SectionDao();
        ungradedSection.setTranslationKey(ungradedTranslationKey);
        ungradedSection.setPosition(99999);
        LinkSectionDto ungraded = new LinkSectionDto(ungradedSection);

        List<BandDao> bandsBySection = this.regionRepository.findActiveBandsBySection(region.getId());
        String lastSectionName = null;
        for (BandDao band : bandsBySection) {
            String sectionName = ungraded.getTranslationKey();
            if (band.getSection() != null) {
                sectionName = band.getSection().getTranslationKey();
            }
            if (!sectionName.equals(lastSectionName)) {
                if (band.getSection() == null) {
                    returnList.add(ungraded);
                } else {
                    returnList.add(new LinkSectionDto(band.getSection()));
                }
                lastSectionName = sectionName;
            }
            returnList.get(returnList.size()-1).getBands().add(band);
        }
        return returnList.stream().sorted(Comparator.comparing(LinkSectionDto::getPosition)).collect(Collectors.toList());
    }

    @Override
    public List<RegionDao> findSubRegions(RegionDao region) {
        return this.regionRepository.fetchSubRegionsOf(region.getId());
    }

    @Override
    @IsBbrMember
    public RegionDao create(String regionName) {
        RegionDao region = new RegionDao();
        region.setName(regionName);

        return this.create(region);
    }

    @Override
    @IsBbrMember
    public RegionDao create(String regionName, RegionDao parent) {
        RegionDao region = new RegionDao();
        region.setName(regionName);
        region.setContainerRegionId(parent.getId());

        return this.create(region);
    }

    @Override
    @IsBbrMember
    public RegionDao create(RegionDao region) {
        // validation
        if (region.getId() != null) {
            throw new ValidationException("Can't create with specific id");
        }

        if (StringUtils.isBlank(region.getName())) {
            throw new ValidationException("Band name must be specified");
        }

        // defaults
        if (StringUtils.isBlank(region.getSlug())) {
            region.setSlug(slugify(region.getName()));
        }

        region.setCreated(LocalDateTime.now());
        region.setCreatedBy(this.securityService.getCurrentUsername());
        region.setUpdated(LocalDateTime.now());
        region.setUpdatedBy(this.securityService.getCurrentUsername());
        return this.regionRepository.saveAndFlush(region);
    }

    @Override
    public List<ContestDao> findContestsForRegion(RegionDao region) {
        List<ContestDao> returnContests = new ArrayList<>();
        List<ContestListForRegionSqlDto> contestsSql = RegionSql.listContestsForRegion(this.entityManager, region.getSlug());
        for (ContestListForRegionSqlDto eachContest : contestsSql){
            returnContests.add(eachContest.asContest());
        }

        return returnContests;
    }

    @Override
    public Optional<RegionDao> fetchById(Long regionId) {
        if (regionId == null) {
            return Optional.empty();
        }
        return this.regionRepository.findById(regionId);
    }
}
