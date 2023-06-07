package uk.co.bbr.services.bands.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import uk.co.bbr.services.events.dao.ContestResultDao;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class BandDetailsDto {
    private final List<ContestResultDao> bandResults;
    private final List<ContestResultDao> bandWhitResults;
    private final List<ContestResultDao> bandAllResults;
}
