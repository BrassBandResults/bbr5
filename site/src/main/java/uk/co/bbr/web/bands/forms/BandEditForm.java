package uk.co.bbr.web.bands.forms;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import uk.co.bbr.services.bands.dao.BandDao;

import java.time.LocalDate;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;

@Getter
@Setter
public class BandEditForm {
    private String name;
    private Long region;
    private String latitude;
    private String longitude;
    private String website;
    private Integer status;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    private String notes;

    public BandEditForm() {
        super();
    }

    public BandEditForm(BandDao band) {
        assertNotNull(band);

        this.name = band.getName();
        this.region = band.getRegion().getId();
        this.latitude = band.getLatitude();
        this.longitude = band.getLongitude();
        this.website = band.getWebsite();
        this.status = band.getStatus().getCode();
        this.startDate = band.getStartDate();
        this.endDate = band.getEndDate();
        this.notes = band.getNotes();
    }

    public void validate(BindingResult bindingResult) {
        if (this.name == null || this.name.strip().length() == 0) {
            bindingResult.addError(new ObjectError("name", "page.band-edit.errors.name-required"));
        }
        if (this.startDate != null && this.endDate != null && this.endDate.isBefore(this.startDate)) {
            bindingResult.addError(new ObjectError("startDate", "page.band-edit.errors.dates-valid"));
        }
    }
}
