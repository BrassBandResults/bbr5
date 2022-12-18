package uk.co.bbr.services.people.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.co.bbr.services.band.dao.BandDao;
import uk.co.bbr.services.framework.AbstractDao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name="person_alternative_name")
public class PersonAlternativeNameDao extends AbstractDao {

    @ManyToOne(fetch= FetchType.EAGER, optional=false)
    @JoinColumn(name="person_id")
    private PersonDao person;

    @Column(name="name", nullable=false)
    private String name;

    @Column(name="hidden")
    private boolean hidden;
}
