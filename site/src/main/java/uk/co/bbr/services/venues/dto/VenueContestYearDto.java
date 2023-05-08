package uk.co.bbr.services.venues.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class VenueContestYearDto {
    private final int year;
    private int eventCount = 1;

    public void incrementEventCount() {
        this.eventCount++;
    }
}
