package uk.co.bbr.services.people.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.co.bbr.services.framework.AbstractDao;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Getter
@Entity
@NoArgsConstructor
@Table(name="person_profile")
public class PersonProfileDao extends AbstractDao {

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="person_id")
    @Setter
    private PersonDao person;

    @Column(name="title")
    private String title;

    @Column(name="qualifications")
    private String qualifications;

    @Column(name="email")
    private String email;

    @Column(name="website")
    private String website;

    @Column(name="home_phone")
    private String homePhone;

    @Column(name="mobile_phone")
    private String mobilePhone;

    @Column(name="address")
    private String address;

    @Column(name="profile", nullable=false)
    private String profile;

    @Column(name="visible")
    private boolean visible;

    public void setTitle(String title) {

        if (title == null) {
            this.title = null;
            return;
        }
        this.title = title.strip();
    }

    public void setQualifications(String qualifications) {

        if (qualifications == null) {
            this.qualifications = null;
            return;
        }
        this.qualifications = qualifications.strip();
    }

    public void setEmail(String email) {

        if (email == null) {
            this.email = null;
            return;
        }
        this.email = email.strip();
    }

    public void setWebsite(String website) {

        if (website == null) {
            this.website = null;
            return;
        }
        this.website = website.strip();
    }

    public void setHomePhone(String homePhone) {

        if (homePhone == null) {
            this.homePhone = null;
            return;
        }
        this.homePhone = homePhone.strip();
    }

    public void setMobilePhone(String mobilePhone) {

        if (mobilePhone == null) {
            this.mobilePhone = null;
            return;
        }
        this.mobilePhone = mobilePhone.strip();
    }

    public void setAddress(String address) {

        if (address == null) {
            this.address = null;
            return;
        }
        this.address = address.strip();
    }

    public void setProfile(String profile) {

        if (profile == null) {
            this.profile = null;
            return;
        }
        this.profile = profile.strip();
    }
}
