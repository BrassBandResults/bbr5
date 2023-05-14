package uk.co.bbr.services.contests.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import uk.co.bbr.services.contests.dao.ContestGroupDao;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class ContestGroupYearDto {
    private final ContestGroupDao contestGroup;
    private final String year;
    private final List<ContestEventSummaryDto> contestEvents;
    private final String nextYear;
    private final String previousYear;
}
