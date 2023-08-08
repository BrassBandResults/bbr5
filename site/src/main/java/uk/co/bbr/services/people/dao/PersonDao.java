package uk.co.bbr.services.people.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.co.bbr.services.framework.AbstractDao;
import uk.co.bbr.services.framework.mixins.NameTools;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.time.LocalDate;

@Getter
@Entity
@NoArgsConstructor
@Table(name="person")
public class PersonDao extends AbstractDao implements NameTools {
    @Column(name="old_id")
    private String oldId;

    @Column(name="first_names")
    private String firstNames;

    @Column(name="surname", nullable=false)
    private String surname;

    @Column(name="combined_name", nullable=false)
    private String combinedName;

    @Column(name="slug", nullable=false)
    private String slug;

    @Column(name="suffix")
    private String suffix;

    @Column(name="known_for")
    private String knownFor;

    @Column(name="notes")
    private String notes;

    @Column(name="deceased", nullable=false)
    @Setter
    private boolean deceased;

    @Column(name="start_date")
    @Setter
    private LocalDate startDate;

    @Column(name="end_date")
    @Setter
    private LocalDate endDate;

    @Transient
    @Setter
    private int conductingCount = 0;
    @Transient
    @Setter
    private int adjudicationsCount = 0;
    @Transient
    @Setter
    private int compositionsCount = 0;
    @Transient
    @Setter
    private int arrangementsCount = 0;

    public int getPieceCount() {
        return this.compositionsCount + this.arrangementsCount;
    }

    public void setNotes(String notes) {
        if (notes != null) {
            this.notes = notes.strip();
        }
    }

    public void setOldId(String oldId) {
        if (oldId != null) {
            this.oldId = oldId.strip();
        }
    }

    public void setSlug(String value) {
        if (value != null) {
            value = value.strip();
        }
        this.slug = value;
    }

    public void setFirstNames(String firstNames) {
        this.firstNames = simplifyFirstName(firstNames);
        if (this.firstNames.length() == 1) {
            this.firstNames += ".";
        }
        this.setCombinedName();
    }

    public void setCombinedName(String combinedName) {
        this.combinedName = combinedName;
    }

    private void setCombinedName() {
        StringBuilder tempCombinedName = new StringBuilder();
        if (this.firstNames != null) {
            tempCombinedName.append(this.firstNames.strip());
            tempCombinedName.append(" ");
        }
        if (this.surname != null) {
            tempCombinedName.append(this.surname.strip());
            tempCombinedName.append(" ");
        }
        if (this.suffix != null) {
            tempCombinedName.append(this.suffix.strip());
            tempCombinedName.append(" ");
        }

        this.combinedName = tempCombinedName.toString().strip();
    }

    public void setSurname(String surname) {
        this.surname = simplifySurname(surname);

        this.setCombinedName();
    }

    public void setSuffix(String suffix) {
        if (suffix != null) {
            suffix = suffix.strip();
        }
        this.suffix = suffix;

        this.setCombinedName();
    }

    public void setKnownFor(String knownFor) {
        if (knownFor != null) {
            knownFor = knownFor.strip();
        }
        this.knownFor = knownFor;
    }

    public String getName() {
        StringBuilder returnValue = new StringBuilder();
        if (this.firstNames != null && this.firstNames.strip().length() > 0) {
            returnValue.append(this.firstNames);
        }

        if (returnValue.length() > 0) {
            returnValue.append(" ");
        }
        returnValue.append(this.surname);
        if (this.suffix != null && this.suffix.strip().length() > 0) {
            returnValue.append(" ");
            returnValue.append(this.suffix);
        }

        return returnValue.toString();
    }

    public String getNameSurnameFirst() {
        StringBuilder returnValue = new StringBuilder();
        returnValue.append(this.surname);

        if (this.suffix != null && this.suffix.strip().length() > 0) {
            returnValue.append(" ");
            returnValue.append(this.suffix);
        }

        if (this.firstNames != null && this.firstNames.strip().length() > 0) {
            returnValue.append(", ");
            returnValue.append(this.firstNames);
        }

        return returnValue.toString();
    }

    public boolean matchesName(String personName) {
        return personName != null && personName.equalsIgnoreCase(this.combinedName);
    }

    public ObjectNode asLookup(ObjectMapper objectMapper) {
        ObjectNode person = objectMapper.createObjectNode();
        person.put("slug", this.getSlug());
        person.put("name", this.escapeJson(this.combinedName));
        person.put("context", this.knownFor != null ? this.knownFor : "");
        return person;
    }
}
