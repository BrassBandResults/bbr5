package uk.co.bbr.services.groups.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import uk.co.bbr.services.groups.dao.ContestGroupDao;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class WhitFridayOverallResultsDto {
    private final ContestGroupDao contestGroup;
    private final Integer year;
    private final List<WhitFridayOverallBandResultDto> overallBandResults;
}
