package uk.co.bbr.services.bands.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.co.bbr.services.framework.AbstractDao;
import uk.co.bbr.services.framework.mixins.NameTools;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDate;

@Getter
@Entity
@NoArgsConstructor
@Table(name="band_previous_name")
public class BandPreviousNameDao extends AbstractDao implements NameTools {

    @ManyToOne(fetch= FetchType.EAGER, optional=false)
    @JoinColumn(name="band_id")
    @Setter
    private BandDao band;

    @Column(name="old_name", nullable=false)
    private String oldName;

    @Column(name="start_date")
    @Setter
    private LocalDate startDate;

    @Column(name="end_date")
    @Setter
    private LocalDate endDate;

    @Column(name="hidden")
    @Setter
    private boolean hidden;

    public void setOldName(String name) {
        String nameToSet = simplifyBandName(name);
        this.oldName = nameToSet;
    }

    public String getDisplayName() {
        StringBuilder returnString = new StringBuilder();

        returnString.append(this.oldName);
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
