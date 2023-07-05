package uk.co.bbr.web.contests.forms;

import lombok.Getter;
import lombok.Setter;

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
}
