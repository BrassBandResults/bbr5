package uk.co.bbr.services.people.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.co.bbr.services.framework.AbstractDao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name="person")
public class PersonDao extends AbstractDao {
    @Column(name="old_id")
    private String oldId;

    @Column(name="first_names")
    private String firstNames;

    @Column(name="surname", nullable=false)
    private String surname;

    @Column(name="slug", nullable=false)
    private String slug;

    @Column(name="suffix")
    private String suffix;

    @Column(name="known_for")
    private String knownFor;

    @Column(name="notes")
    private String notes;

    @Column(name="deceased", nullable=false)
    private boolean deceased;

    @Column(name="start_date")
    private LocalDate startDate;

    @Column(name="end_date")
    private LocalDate endDate;

    public void setNotes(String notes) {
        if (notes != null) {
            this.notes = notes.trim();
        }
    }

    public void setOldId(String oldId) {
        if (oldId != null) {
            this.oldId = oldId.trim();
        }
    }

    public void setFirstNames(String firstNames) {
        if (firstNames != null) {
            this.firstNames = firstNames.trim();
        }
    }

    public void setSurname(String surname) {
        if (surname != null) {
            this.surname = surname.trim();
        }
    }

    public void setSuffix(String suffix) {
        if (suffix != null) {
            this.suffix = suffix.trim();
        }
    }

    public void setKnownFor(String knownFor) {
        if (knownFor != null) {
            this.knownFor = knownFor.trim();
        }
    }

    public String getName() {
        StringBuilder returnValue = new StringBuilder();
        if (this.firstNames != null && this.firstNames.trim().length() > 0) {
            returnValue.append(this.firstNames);
        }

        if (returnValue.length() > 0) {
            returnValue.append(" ");
        }
        returnValue.append(this.surname);
        if (this.suffix != null && this.suffix.trim().length() > 0) {
            returnValue.append(" ");
            returnValue.append(this.suffix);
        }

        return returnValue.toString();
    }
}
