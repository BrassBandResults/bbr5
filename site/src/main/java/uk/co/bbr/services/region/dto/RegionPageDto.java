package uk.co.bbr.services.region.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import uk.co.bbr.services.band.dao.BandDao;
import uk.co.bbr.services.region.dao.RegionDao;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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

    public Map<String, Integer> getSectionTypes() {
        Map<String, Integer> sectionTypes = new LinkedHashMap<>();
        for (BandDao band : this.bands) {
            if (sectionTypes.get(band.getSectionType()) == null) {
                sectionTypes.put(band.getSectionType(), 1);
            } else {
                sectionTypes.put(band.getSectionType(), sectionTypes.get(band.getSectionType()) + 1);
            }
        }
        return sectionTypes;
    }
}
