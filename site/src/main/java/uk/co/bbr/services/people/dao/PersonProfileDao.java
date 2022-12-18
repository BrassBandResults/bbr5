package uk.co.bbr.services.people.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.co.bbr.services.framework.AbstractDao;
import uk.co.bbr.services.section.dao.SectionDao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name="person_profile")
public class PersonProfileDao extends AbstractDao {

    @ManyToOne(fetch= FetchType.EAGER)
    @JoinColumn(name="person_id")
    private PersonDao person;

    @Column(name="title")
    private String title;

    @Column(name="qualifications")
    private String qualification;

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
}
