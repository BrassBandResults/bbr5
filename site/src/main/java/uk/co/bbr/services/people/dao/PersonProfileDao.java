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
@Table(name="PERSON_PROFILE")
public class PersonProfileDao extends AbstractDao {

    @ManyToOne(fetch= FetchType.EAGER)
    @JoinColumn(name="PERSON_ID")
    private PersonDao person;

    @Column(name="TITLE")
    private String title;

    @Column(name="QUALIFICATIONS")
    private String qualification;

    @Column(name="EMAIL")
    private String email;

    @Column(name="WEBSITE")
    private String website;

    @Column(name="HOME_PHONE")
    private String homePhone;

    @Column(name="MOBILE_PHONE")
    private String mobilePhone;

    @Column(name="ADDREESS")
    private String address;

    @Column(name="PROFILE", nullable=false)
    private String profile;

    @Column(name="VISIBLE")
    private boolean visible;
}
