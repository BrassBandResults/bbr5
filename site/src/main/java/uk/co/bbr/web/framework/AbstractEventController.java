package uk.co.bbr.web.framework;

import uk.co.bbr.services.events.ContestEventService;
import uk.co.bbr.services.events.dao.ContestEventDao;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.web.Tools;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.Optional;

public class AbstractEventController {

    protected ContestEventDao contestEventFromUrlParameters(ContestEventService contestEventService, String contestSlug, String contestEventDate) {
        LocalDate eventDate;
        try {
            eventDate = Tools.parseEventDate(contestEventDate);
        }
        catch (DateTimeException ex) {
            throw NotFoundException.eventNotFound(contestSlug, contestEventDate);
        }

        Optional<ContestEventDao> contestEvent = contestEventService.fetchEvent(contestSlug, eventDate);

        if (contestEvent.isEmpty()) {
            contestEvent = contestEventService.fetchEventWithinWiderDateRange(contestSlug, eventDate);
            if (contestEvent.isEmpty()) {
                throw NotFoundException.eventNotFound(contestSlug, contestEventDate);
            }
        }
        return contestEvent.get();
    }
}
