package uk.co.bbr.services.region.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import uk.co.bbr.services.region.dao.RegionDao;

@Getter
@RequiredArgsConstructor
public class RegionPageDto {
    private final RegionDao region;

    public String getSlug() {
        return this.region.getSlug();
    }

    public int getActiveBandsCount() {
        return this.region.getActiveBandsCount();
    }

    public int getExtinctBandsCount() {
        return this.region.getExtinctBandsCount();
    }
}
