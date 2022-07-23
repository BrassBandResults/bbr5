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
@Table(name="PERSON")
public class PersonDao extends AbstractDao {
    @Column(name="FIRST_NAMES")
    private String first_names;

    @Column(name="SURNAME", nullable=false)
    private String surname;

    @Column(name="SLUG", nullable=false)
    private String slug;

    @Column(name="SUFFIX")
    private String suffix;

    @Column(name="KNOWN_FOR")
    private String knownFor;

    @Column(name="NOTES")
    private String notes;

    @Column(name="DECEASED", nullable=false)
    private boolean deceased;

    @Column(name="START_DATE")
    private LocalDate startDate;

    @Column(name="END_DATE")
    private LocalDate endDate;
}
