package uk.co.bbr.web.contests.forms;

import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.contests.dao.ContestDao;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;

@Getter
@Setter
public class ContestEditForm {
    private String name;
    private String contestGroup;
    private Long contestType;
    private Long region;
    private Long section;
    private Integer ordering;
    private String description;
    private String notes;
    private boolean extinct;
    private boolean excludeFromGroupResults;
    private boolean allEventsAdded;
    private boolean preventFutureBands;
    private Integer repeatPeriod;
    private String qualifiesFor;

    public ContestEditForm() {
        super();
    }

    public ContestEditForm(ContestDao contest) {
        assertNotNull(contest);

        this.name = contest.getName();
        if (contest.getContestGroup() != null) {
            this.contestGroup = contest.getContestGroup().getName();
        }
        this.contestType = contest.getDefaultContestType().getId();
        this.region = contest.getRegion().getId();
        if (contest.getSection() != null) {
            this.section = contest.getSection().getId();
        }
        this.ordering = contest.getOrdering();
        this.description = contest.getDescription();
        this.notes = contest.getNotes();
        this.extinct = contest.isExtinct();
        this.qualifiesFor = contest.getQualifiesFor().getName();
        this.excludeFromGroupResults = contest.isExcludeFromGroupResults();
        this.allEventsAdded = contest.isAllEventsAdded();
        this.preventFutureBands = contest.isPreventFutureBands();
        this.repeatPeriod = contest.getRepeatPeriod();
    }

    public void validate(BindingResult bindingResult) {
        if (this.name == null || this.name.trim().length() == 0) {
            bindingResult.addError(new ObjectError("name", "page.contest-edit.errors.name-required"));
        }
    }
}
