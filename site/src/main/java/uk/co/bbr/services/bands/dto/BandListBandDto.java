package uk.co.bbr.services.bands.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BandListBandDto {
    private final String slug;
    private final String name;
    private final String regionName;
    private final String regionSlug;
    private final String regionFlagCode;
    private final int contestCount;
    private final String dateRange;
}
