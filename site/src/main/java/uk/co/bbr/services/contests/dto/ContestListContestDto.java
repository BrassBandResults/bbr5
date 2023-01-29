package uk.co.bbr.services.contests.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import uk.co.bbr.services.regions.dao.RegionDao;

@Getter
@RequiredArgsConstructor
public class ContestListContestDto {
    private final String slug;
    private final String name;
    private final int contestResultsCount;
}
