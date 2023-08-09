package uk.co.bbr.services.bands.dao;

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
import java.time.format.DateTimeFormatter;

@Getter
@Entity
@NoArgsConstructor
@Table(name="band_previous_name")
public class BandAliasDao extends AbstractDao implements NameTools {

    @ManyToOne(fetch=FetchType.EAGER, optional=false)
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
        this.oldName = simplifyBandName(name);
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

    public String getStartDateDisplay() {
        if (this.startDate == null) {
            return "";
        }

        String dateFormat = "dd MMM yyyy";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
        return this.startDate.format(formatter);
    }

    public String getEndDateDisplay() {
        if (this.endDate == null) {
            return "";
        }

        String dateFormat = "dd MMM yyyy";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
        return this.endDate.format(formatter);
    }
}
