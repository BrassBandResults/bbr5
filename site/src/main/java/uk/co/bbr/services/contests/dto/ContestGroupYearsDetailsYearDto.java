package uk.co.bbr.services.contests.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ContestGroupYearsDetailsYearDto {
    private final String year;
    private final int eventsCount;
}
