package uk.co.bbr.services.bands.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import uk.co.bbr.services.bands.dao.BandDao;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class BandListDto {
    private final int returnedBandsCount;
    private final long allBandsCount;
    private final String searchPrefix;
    private final List<BandDao> returnedBands;
}
