package uk.co.bbr.services.groups.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import uk.co.bbr.services.groups.dao.ContestGroupDao;
import uk.co.bbr.services.events.dto.ContestEventSummaryDto;

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
