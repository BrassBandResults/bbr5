package uk.co.bbr.web.venues.forms;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VenueEditForm {
    private String name;
    private Long region;
    private String latitude;
    private String longitude;
    private String notes;
    private String parentRegion;
}
