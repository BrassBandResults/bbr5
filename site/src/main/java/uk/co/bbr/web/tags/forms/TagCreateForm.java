package uk.co.bbr.web.tags.forms;

import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

@Getter
@Setter
public class TagCreateForm {
    private String name;

    public void validate(BindingResult bindingResult) {
        if (this.name == null || this.name.trim().length() == 0) {
            bindingResult.addError(new ObjectError("name", "page.tag-edit.errors.name-required"));
        }
    }
}
