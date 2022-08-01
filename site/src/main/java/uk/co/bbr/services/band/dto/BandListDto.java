package uk.co.bbr.services.band.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class BandListDto {
    private final int returnedBandsCount;
    private final long allBandsCount;
    private final String searchPrefix;
    private final List<BandListBandDto> returnedBands;
}
