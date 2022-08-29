package uk.co.bbr.services.region.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import uk.co.bbr.services.region.dao.RegionDao;

@Getter
@RequiredArgsConstructor
public class RegionListDto {
    private final RegionDao region;

    public String getSlug() {
        return this.region.getSlug();
    }

    public String getCountryCode() {
        return this.region.getCountryCode();
    }


    public int getBandsCount() {
        return this.region.getBandsCount();
    }
    public int getActiveBandsCount() {
        return this.region.getActiveBandsCount();
    }

    public int getExtinctBandsCount() {
        return this.region.getExtinctBandsCount();
    }
}
