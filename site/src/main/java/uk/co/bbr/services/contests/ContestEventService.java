package uk.co.bbr.services.contests;

import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.contests.dao.ContestEventDao;
import uk.co.bbr.services.contests.dao.ContestGroupDao;

import java.time.LocalDate;

public interface ContestEventService {

    ContestEventDao create(ContestDao contest, LocalDate eventDate);

    ContestEventDao create(ContestDao contest, ContestEventDao event);
}
