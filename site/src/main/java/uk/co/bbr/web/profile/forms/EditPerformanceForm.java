package uk.co.bbr.web.profile.forms;

import lombok.Getter;
import lombok.Setter;
import uk.co.bbr.services.performances.dao.PerformanceDao;

@Getter
@Setter
public class EditPerformanceForm {

    private Integer instrumentCode;

    public EditPerformanceForm() {}

    public EditPerformanceForm(PerformanceDao performance) {
        if (performance.getInstrument() == null) {
            this.instrumentCode = null;
        } else {
            this.instrumentCode = performance.getInstrument().getCode();
        }
    }
}
