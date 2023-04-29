package uk.co.bbr.services.contests.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.co.bbr.services.framework.AbstractDao;
import uk.co.bbr.services.framework.mixins.NameTools;
import uk.co.bbr.services.people.dao.PersonDao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Getter
@Entity
@NoArgsConstructor
@Table(name="contest_event_adjudicator")
public class ContestAdjudicatorDao extends AbstractDao implements NameTools {
    @Column(name="adjudicator_name", nullable=false)
    private String name;

    @Column(name="old_id")
    private String oldId;

    @ManyToOne(fetch= FetchType.EAGER, optional=false)
    @JoinColumn(name="contest_event_id")
    @Setter
    private ContestEventDao contestEvent;

    @ManyToOne(fetch= FetchType.EAGER, optional=false)
    @JoinColumn(name="person_id")
    @Setter
    private PersonDao adjudicator;

    public void setName(String value){
        this.name = simplifyPersonFullName(value);
    }
}
