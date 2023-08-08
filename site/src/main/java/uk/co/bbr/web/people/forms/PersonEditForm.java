package uk.co.bbr.web.people.forms;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import uk.co.bbr.services.people.dao.PersonDao;

import java.time.LocalDate;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;

@Getter
@Setter
public class PersonEditForm {
    private String firstNames;
    private String surname;
    private String suffix;
    private String knownFor;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    private String notes;

    public PersonEditForm() {
        super();
    }

    public PersonEditForm(PersonDao person) {
        assertNotNull(person);

        this.firstNames = person.getFirstNames();
        this.surname = person.getSurname();
        this.suffix = person.getSuffix();
        this.knownFor = person.getKnownFor();
        this.startDate = person.getStartDate();
        this.endDate = person.getEndDate();
        this.notes = person.getNotes();
    }

    public void validate(BindingResult bindingResult) {
        if (this.surname == null || this.surname.strip().length() == 0) {
            bindingResult.addError(new ObjectError("surname", "page.person-edit.errors.surname-required"));
        }
        if (this.startDate != null && this.endDate != null && this.endDate.isBefore(this.startDate)) {
            bindingResult.addError(new ObjectError("startDate", "page.person-edit.errors.dates-valid"));
        }
    }
}
