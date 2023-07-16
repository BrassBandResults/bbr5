package uk.co.bbr.services.contests.dto;

import lombok.Getter;

@Getter
public class ContestStreakYearDto {

    private final int year;
    private boolean inStreak = false;
    private boolean streakEnd = false;

    public ContestStreakYearDto(int year) {
        this.year = year;
    }

    public void markInStreak() {
        this.inStreak = true;
    }

    public void markStreakEnd() {
        this.streakEnd = true;
    }
}
