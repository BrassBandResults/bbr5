package uk.co.bbr.services.region;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.band.dao.BandDao;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.services.framework.mixins.SlugTools;
import uk.co.bbr.services.region.dao.RegionDao;
import uk.co.bbr.services.region.dao.RegionRepository;
import uk.co.bbr.services.region.dto.LinkSectionDto;
import uk.co.bbr.services.region.dto.RegionPageDto;
import uk.co.bbr.services.section.dao.SectionDao;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RegionServiceImpl implements RegionService, SlugTools {

    private final RegionRepository regionRepository;

    @Override
    public List<RegionDao> fetchAll() {
        return this.regionRepository.fetchAll();
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
    public RegionPageDto findBySlugForPage(String regionSlug) {
        Optional<RegionDao> region = this.regionRepository.findBySlug(regionSlug);
        if (region.isEmpty()) {
            throw new NotFoundException("Region with slug " + regionSlug + " not found");
        }

        List<BandDao> bandsForThisRegion = this.regionRepository.findBandsForRegion(regionSlug);

        return new RegionPageDto(region.get(), bandsForThisRegion);
    }

    @Override
    public List<LinkSectionDto> fetchBandsBySection(RegionDao region, String ungradedDescription) {
        List<LinkSectionDto> returnList = new ArrayList<>();

        SectionDao ungradedSection = new SectionDao();
        ungradedSection.setName(ungradedDescription);
        ungradedSection.setPosition(99999);
        LinkSectionDto ungraded = new LinkSectionDto(ungradedSection);

        List<BandDao> bandsBySection = this.regionRepository.findActiveBandsBySection(region.getId());
        String lastSectionName = null;
        for (BandDao band : bandsBySection) {
            String sectionName = ungraded.getName();
            if (band.getSection() != null) {
                sectionName = band.getSection().getName();
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
    public List<RegionDao> fetchSubRegions(RegionDao region) {
        return this.regionRepository.fetchSubRegionsOf(region.getId());
    }
}
