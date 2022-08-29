package uk.co.bbr.services.region.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import uk.co.bbr.services.region.dao.RegionDao;

@Getter
@RequiredArgsConstructor
public class RegionListDto {
    private final RegionDao region;
    private final int bandCount;

    public String getSlug() {
        return this.region.getSlug();
    }

    public String getCountryCode() {
        return this.region.getCountryCode();
    }
}
