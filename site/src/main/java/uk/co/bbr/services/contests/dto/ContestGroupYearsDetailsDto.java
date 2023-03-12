package uk.co.bbr.services.contests.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import uk.co.bbr.services.contests.dao.ContestGroupDao;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class ContestGroupYearsDetailsDto {
    private final ContestGroupDao contestGroup;
    private final List<ContestGroupYearsDetailsYearDto> years;
}
