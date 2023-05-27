package uk.co.bbr.web.bands.forms;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import uk.co.bbr.services.bands.dao.BandAliasDao;

import java.time.LocalDate;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;

@Getter
@Setter
public class BandAliasEditForm {
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    public BandAliasEditForm() {
        super();
    }

    public BandAliasEditForm(BandAliasDao alias) {
        assertNotNull(alias);

        this.startDate = alias.getStartDate();
        this.endDate = alias.getEndDate();
    }

    public void validate(BindingResult bindingResult) {
        if (this.startDate != null && this.endDate != null && this.endDate.isBefore(this.startDate)) {
            bindingResult.addError(new ObjectError("startDate", "page.band-edit.errors.dates-valid"));
        }
    }
}
