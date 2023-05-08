package uk.co.bbr.services.venues.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import uk.co.bbr.services.contests.dao.ContestDao;

@Getter
@RequiredArgsConstructor
public class VenueContestDto {
    private final ContestDao contest;
    private int eventCount = 1;

    public void incrementEventCount() {
        this.eventCount++;
    }
}
