package uk.co.bbr.web.profile.forms;

import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import uk.co.bbr.services.people.dao.PersonProfileDao;

@Getter
@Setter
public class EditProfileForm {

    private String profile;
    private String address;
    private String email;
    private String qualifications;
    private String homePhone;
    private String mobilePhone;
    private String title;
    private String website;

    public EditProfileForm() {}

    public EditProfileForm(PersonProfileDao profile) {
        this.profile = profile.getProfile();
        this.address = profile.getAddress();
        this.email = profile.getEmail();
        this.qualifications = profile.getQualifications();
        this.homePhone = profile.getHomePhone();
        this.mobilePhone = profile.getMobilePhone();
        this.title = profile.getTitle();
        this.website = profile.getWebsite();
    }

    public void validate(BindingResult bindingResult) {
        if (this.profile == null || this.profile.strip().length() == 0) {
            bindingResult.addError(new ObjectError("surname", "page.person-edit.errors.surname-required"));
        }
    }
}
