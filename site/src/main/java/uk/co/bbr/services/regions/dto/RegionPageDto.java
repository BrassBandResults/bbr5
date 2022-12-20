package uk.co.bbr.services.regions.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.regions.dao.RegionDao;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    public String getLatitude() { return this.region.getLatitude();}
    public String getLongitude() { return this.region.getLongitude();}
    public Integer getDefaultMapZoom() { return this.region.getDefaultMapZoom();}

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
