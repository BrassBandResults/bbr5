package uk.co.bbr.services.venues.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.co.bbr.services.framework.AbstractDao;
import uk.co.bbr.services.framework.mixins.NameTools;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Getter
@Entity
@NoArgsConstructor
@Table(name="venue_alias")
public class VenueAliasDao extends AbstractDao implements NameTools {
    @Column(name="name", nullable=false)
    private String name;

    @Column(name="old_id")
    private String oldId;

    @ManyToOne(fetch=FetchType.EAGER, optional=false)
    @JoinColumn(name="venue_id")
    @Setter
    private VenueDao venue;

    @Column(name="start_date")
    @Setter
    private LocalDate startDate;

    @Column(name="end_date")
    @Setter
    private LocalDate endDate;

    public void setName(String value){
        this.name = simplifyVenueName(value);
    }

    public String getDisplayName() {
        StringBuilder returnString = new StringBuilder();

        returnString.append(this.name);
        if (this.startDate != null || this.endDate != null) {
            returnString.append(" (");
            if (this.startDate != null) {
                returnString.append(this.startDate.getYear());
            }
            returnString.append("-");
            if (this.endDate != null) {
                returnString.append(this.endDate.getYear());
            }
            returnString.append(")");
        }

        return returnString.toString();
    }
}
