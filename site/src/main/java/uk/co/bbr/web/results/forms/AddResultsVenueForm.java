package uk.co.bbr.web.results.forms;

import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import uk.co.bbr.services.venues.VenueService;

@Getter
@Setter
public class AddResultsVenueForm {
    private String venueName;
    private String venueSlug;

    public void validate(BindingResult bindingResult) {
        if (this.venueSlug == null || this.venueSlug.trim().length() < 4) {
            bindingResult.addError(new ObjectError("eventDate", "page.add-results.errors.not-found-venue"));
        }
    }
}
