package uk.co.bbr.web.groups.forms;

import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import uk.co.bbr.services.events.dao.ContestEventDao;
import uk.co.bbr.services.groups.dao.ContestGroupDao;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;

@Getter
@Setter
public class GroupEditForm {
    private String name;
    private String groupType;
    private String notes;

    public GroupEditForm() {
        super();
    }

    public GroupEditForm(ContestGroupDao group) {
        assertNotNull(group);

        this.name = group.getName();
        this.groupType = group.getGroupType().getCode();
        this.notes = group.getNotes();
    }

    public void validate(BindingResult bindingResult) {
        if (this.name == null || this.name.strip().length() == 0) {
            bindingResult.addError(new ObjectError("name", "page.group-edit.errors.name-required"));
        }
    }

}
