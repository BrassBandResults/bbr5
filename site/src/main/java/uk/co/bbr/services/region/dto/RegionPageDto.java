package uk.co.bbr.services.region.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import uk.co.bbr.services.band.dao.BandDao;
import uk.co.bbr.services.region.dao.RegionDao;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@RequiredArgsConstructor
public class RegionPageDto {
    private final RegionDao region;

    private final List<BandDao> bands;

    public String getSlug() {
        return this.region.getSlug();
    }

    public int getActiveBandsCount() {
        return this.region.getActiveBandsCount();
    }

    public int getExtinctBandsCount() {
        return this.region.getExtinctBandsCount();
    }

    public Set<String> getSectionTypes() {
        Set<String> sectionTypes = new HashSet<>();
        for (BandDao band : this.bands) {
            sectionTypes.add(band.getSectionType());
        }
        return sectionTypes;
    }
}
