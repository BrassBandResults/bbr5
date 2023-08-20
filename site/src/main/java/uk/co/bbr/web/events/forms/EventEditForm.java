package uk.co.bbr.web.events.forms;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import uk.co.bbr.services.events.dao.ContestEventDao;

import java.time.LocalDate;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;

@Getter
@Setter
public class EventEditForm {
    private String name;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate eventDate;
    private String dateResolution;
    private String notes;
    private String venueName;
    private String venueSlug;
    private boolean noContest;
    private Long contestType;

    public EventEditForm() {
        super();
    }

    public EventEditForm(ContestEventDao event) {
        assertNotNull(event);

        this.name = event.getName();
        this.eventDate = event.getEventDate();
        this.dateResolution = event.getEventDateResolution().getCode();
        this.notes = event.getNotes();
        if (event.getVenue() != null) {
            this.venueName = event.getVenue().getName();
            this.venueSlug = event.getVenue().getSlug();
        }
        this.noContest = event.isNoContest();
        this.contestType = event.getContestType().getId();
    }

    public void validate(BindingResult bindingResult) {
        if (this.name == null || this.name.strip().length() == 0) {
            bindingResult.addError(new ObjectError("name", "page.event-edit.errors.name-required"));
        }
    }
}
