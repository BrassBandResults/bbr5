package uk.co.bbr.services.contests.dto;

import lombok.Getter;
import uk.co.bbr.services.bands.BandService;
import uk.co.bbr.services.bands.dao.BandDao;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ContestStreakContainerDto {
    private List<ContestStreakDto> streaks = new ArrayList<>();

    public void populate(Map<String, List<Integer>> streaksBandSlugToYear, BandService bandService) {
        for (String eachBandSlug : streaksBandSlugToYear.keySet()) {
            for (Integer year : streaksBandSlugToYear.get(eachBandSlug)) {
                ContestStreakDto existingStreak = this.getStreaksForBand(eachBandSlug);
                if (existingStreak == null) {
                    Optional<BandDao> band = bandService.fetchBySlug(eachBandSlug);
                    if (band.isEmpty()) {
                        continue;
                    }
                    existingStreak = new ContestStreakDto(band.get().getName(), band.get().getSlug());
                    this.streaks.add(existingStreak);
                }
                existingStreak.addYear(year);
            }
        }

        this.markStreaks();
    }

    private void markStreaks() {
        for (ContestStreakDto streakBands : this.streaks) {
            for (int i=1; i< streakBands.getYears().size(); i++) {
                int thisYear = streakBands.getYears().get(i).getYear();
                int previousYear = streakBands.getYears().get(i-1).getYear();

                if (thisYear == previousYear + 1) {
                    streakBands.getYears().get(i).markInStreak();
                    streakBands.getYears().get(i-1).markInStreak();
                }
            }
        }
    }

    private ContestStreakDto getStreaksForBand(String eachBandSlug) {
        for (ContestStreakDto streak : this.streaks) {
            if (streak.getBandSlug().equals(eachBandSlug)) {
                return streak;
            }
        }
        return null;
    }

    public List<ContestStreakDto> getStreaks() {
        return this.streaks.stream().sorted(Comparator.comparing(ContestStreakDto::getBandName)).collect(Collectors.toList());
    }
}

