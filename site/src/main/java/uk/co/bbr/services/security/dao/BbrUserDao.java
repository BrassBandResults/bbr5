package uk.co.bbr.services.security.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import uk.co.bbr.services.framework.AbstractDao;
import uk.co.bbr.services.security.types.ContestHistoryVisibility;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor
@Table(name="site_user")
public class BbrUserDao extends AbstractDao {

    public BbrUserDao(long id) {
        this.setId(id);
    }

    @Column(name="usercode", length=50, nullable=false)
    private String usercode;

    @Column(name="password", length=100, nullable=false)
    private String password;

    @Column(name="email", length=100, nullable=false)
    private String email;

    @Setter
    @Column(name="old_id")
    private Integer oldId;

    @Column(name="access_level", length=1, nullable=false)
    private String accessLevel;

    @Column(name="salt", length=10, nullable=false)
    private String salt;

    @Column(name="password_version", length=1, nullable=false)
    private String passwordVersion;

    @Setter
    @Column(name="last_login")
    private LocalDateTime lastLogin;

    @Setter
    @Column(name="points")
    private Integer points;

    @Column(name="contest_history_visibility", length=1)
    private ContestHistoryVisibility contestHistoryVisibility;

    @Setter
    @Column(name="stripe_email", length=100)
    private String stripeEmail;

    @Setter
    @Column(name="stripe_token", length=30)
    private String stripeToken;

    @Setter
    @Column(name="stripe_customer", length=30)
    private String stripeCustomer;

    public UserRole getRole() {
        return UserRole.fromCode(this.accessLevel);
    }

    public void setUsercode(String value) {
        if (value != null) {
            value = value.trim();
        }
        this.usercode = value;
    }

    public void setPassword(String value) {
        if (value != null) {
            value = value.trim();
        }
        this.password = value;
    }

    public void setAccessLevel(String value) {
        if (value != null) {
            value = value.trim();
        }
        this.accessLevel = value;
    }

    public void setSalt(String value) {
        if (value != null) {
            value = value.trim();
        }
        this.salt = value;
    }

    public void setEmail(String value) {
        if (value != null) {
            value = value.trim();
        }
        this.email = value;
    }

    public void setPasswordVersion(String value) {
        if (value != null) {
            value = value.trim();
        }
        this.passwordVersion = value;
    }

    public void setContestHistoryVisibility(String value) {
        if (value == null) {
            this.contestHistoryVisibility = ContestHistoryVisibility.PUBLIC;
            return;
        }

        value = value.trim();
        if (value.length() == 0) {
            this.contestHistoryVisibility = ContestHistoryVisibility.PUBLIC;
            return;
        }
        if (value.equals("\"\"")) {
            this.contestHistoryVisibility = ContestHistoryVisibility.PUBLIC;
            return;
        }
        switch (value) {
            case "public":
                this.contestHistoryVisibility = ContestHistoryVisibility.PUBLIC;
                break;
            case "private":
                this.contestHistoryVisibility = ContestHistoryVisibility.PRIVATE;
                break;
            case "site":
                this.contestHistoryVisibility = ContestHistoryVisibility.SITE_ONLY;
                break;
            default:
                System.out.println("Setting contest history visibility from [" + value + "]");
                this.contestHistoryVisibility = ContestHistoryVisibility.fromCode(value);
                break;
        }
    }
}
