package uk.co.bbr.services.contests.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.co.bbr.services.contests.types.ContestEventDateResolution;
import uk.co.bbr.services.framework.AbstractDao;
import uk.co.bbr.services.framework.mixins.NameTools;
import uk.co.bbr.services.venues.dao.VenueDao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor
@Table(name="contest_event")
public class ContestEventDao extends AbstractDao implements NameTools {

    @Column(name="name", nullable=false)
    private String name;

    @Column(name="old_id")
    private String oldId;

    @Column(name="date_of_event", nullable=false)
    @Setter
    private LocalDate eventDate;

    @Column(name="date_resolution", nullable=false)
    @Setter
    private ContestEventDateResolution eventDateResolution;

    @ManyToOne(fetch= FetchType.EAGER, optional=false)
    @JoinColumn(name="contest_id")
    @Setter
    private ContestDao contest;

    @Column(name="notes")
    private String notes;

    @ManyToOne(fetch= FetchType.EAGER)
    @JoinColumn(name="venue_id")
    @Setter
    private VenueDao venue;

    @Column(name="complete", nullable=false)
    @Setter
    private boolean complete;

    @Column(name="no_contest", nullable=false)
    @Setter
    private boolean noContest;

    @Column(name="original_owner", nullable=false)
    private String originalOwner;

    @ManyToOne(fetch= FetchType.EAGER, optional=false)
    @JoinColumn(name="contest_type_id")
    @Setter
    private ContestTypeDao contestType;

    @Transient
    @Setter
    private List<ContestEventTestPieceDao> pieces;

    public void setName(String name){
        this.name = simplifyName(name);
    }

    public void setOldId(String value) {
        if (value != null) {
            value = value.trim();
        }
        this.oldId = value;
    }

    public void setNotes(String value) {
        if (value != null) {
            value = value.trim();
        }
        this.notes = value;
    }

    public void setOriginalOwner(String value) {
        if (value != null) {
            value = value.trim();
        }
        this.originalOwner = value;
    }

    public String getEventDateForUrl() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return this.eventDate.format(formatter);
    }

    public String getEventDateDisplay() {
        String dateFormat = null;
        switch (this.eventDateResolution) {

            case EXACT_DATE -> dateFormat = "dd MMM yyyy";
            case MONTH_AND_YEAR -> dateFormat = "MMM yyyy";
            case YEAR -> dateFormat = "yyyy";
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
        return this.eventDate.format(formatter);
    }
}
