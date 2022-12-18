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
    @Column(name="first_names")
    private String first_names;

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
}
