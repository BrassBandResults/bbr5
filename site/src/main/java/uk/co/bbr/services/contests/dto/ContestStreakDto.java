package uk.co.bbr.services.contests.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ContestStreakDto {
    @Getter private final String bandName;
    @Getter private final String bandSlug;
    private final List<ContestStreakYearDto> years = new ArrayList<>();

    public void addYear(Integer year) {
        this.years.add(new ContestStreakYearDto(year));
    }

    public List<ContestStreakYearDto> getYears() {
        return this.years.stream().sorted(Comparator.comparing(ContestStreakYearDto::getYear)).collect(Collectors.toList());
    }
}
