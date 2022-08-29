package uk.co.bbr.services.region.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import uk.co.bbr.services.region.dao.RegionDao;

@Getter
@RequiredArgsConstructor
public class RegionPageDto {
    private final RegionDao region;
    private final int activeBandsCount;
    private final int extinctBandsCount;

    public String getSlug() {
        return this.region.getSlug();
    }
}
