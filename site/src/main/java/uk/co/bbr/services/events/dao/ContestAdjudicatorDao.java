package uk.co.bbr.services.events.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.framework.AbstractDao;
import uk.co.bbr.services.framework.mixins.NameTools;
import uk.co.bbr.services.people.dao.PersonDao;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Getter
@Entity
@NoArgsConstructor
@Table(name="contest_event_adjudicator")
public class ContestAdjudicatorDao extends AbstractDao implements NameTools {
    @Column(name="adjudicator_name", nullable=false)
    private String name;

    @Column(name="old_id")
    private String oldId;

    @ManyToOne(fetch=FetchType.EAGER, optional=false)
    @JoinColumn(name="contest_event_id")
    @Setter
    private ContestEventDao contestEvent;

    @ManyToOne(fetch=FetchType.EAGER, optional=false)
    @JoinColumn(name="person_id")
    @Setter
    private PersonDao adjudicator;

    @Transient
    @Setter
    private BandDao winner;

    public void setName(String value){
        this.name = simplifyPersonFullName(value);
    }
}
