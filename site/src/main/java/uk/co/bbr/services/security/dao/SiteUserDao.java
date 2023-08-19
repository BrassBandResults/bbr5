package uk.co.bbr.services.security.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.RandomStringUtils;
import uk.co.bbr.services.framework.AbstractDao;
import uk.co.bbr.services.security.types.ContestHistoryVisibility;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor
@Table(name="site_user")
public class SiteUserDao extends AbstractDao {

    public SiteUserDao(long id) {
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
    private Integer points = 0;

    @Column(name="contest_history_visibility", length=1)
    private ContestHistoryVisibility contestHistoryVisibility = ContestHistoryVisibility.PRIVATE;

    @Setter
    @Column(name="stripe_email", length=100)
    private String stripeEmail;

    @Setter
    @Column(name="stripe_token", length=30)
    private String stripeToken;

    @Setter
    @Column(name="stripe_customer", length=30)
    private String stripeCustomer;

    @Setter
    @Column(name="new_email_required")
    private boolean newEmailRequired;

    @Setter
    @Column(name="feedback_email_opt_out")
    private boolean feedbackEmailOptOut;

    @Setter
    @Column(name="pro_user_for_free")
    private boolean proUserForFree;

    @Setter
    @Column(name="uuid", length=40, nullable=false)
    private String uuid;

    @Setter
    @Column(name="reset_password_key", length=40)
    private String resetPasswordKey;

    @Setter
    @Column(name="locale", length=10)
    private String locale;

    public UserRole getRole() {
        return UserRole.fromCode(this.accessLevel);
    }

    public void setUsercode(String value) {
        if (value != null) {
            value = value.strip();
        }
        this.usercode = value;
    }

    public void setPassword(String value) {
        if (value != null) {
            value = value.strip();
        }
        this.password = value;
    }

    public void setAccessLevel(String value) {
        if (value != null) {
            value = value.strip();
        }
        this.accessLevel = value;
    }

    public void setSalt(String value) {
        if (value != null) {
            value = value.strip();
        }
        this.salt = value;
    }

    public void setEmail(String value) {
        if (value != null) {
            value = value.strip();
        }
        this.email = value;
    }

    public void setPasswordVersion(String value) {
        if (value != null) {
            value = value.strip();
        }
        this.passwordVersion = value;
    }

    public void setContestHistoryVisibility(String value) {
        if (value == null) {
            this.contestHistoryVisibility = ContestHistoryVisibility.PUBLIC;
            return;
        }

        value = value.strip();
        if (value.length() == 0) {
            this.contestHistoryVisibility = ContestHistoryVisibility.PUBLIC;
            return;
        }
        if (value.equals("\"\"")) {
            this.contestHistoryVisibility = ContestHistoryVisibility.PUBLIC;
            return;
        }
        switch (value) {
            case "public" -> this.contestHistoryVisibility = ContestHistoryVisibility.PUBLIC;
            case "private" -> this.contestHistoryVisibility = ContestHistoryVisibility.PRIVATE;
            case "site" -> this.contestHistoryVisibility = ContestHistoryVisibility.SITE_ONLY;
            default -> this.contestHistoryVisibility = ContestHistoryVisibility.fromCode(value);
        }
    }

    public void setContestHistoryVisibility(ContestHistoryVisibility contestHistoryVisibility) {
        this.contestHistoryVisibility = contestHistoryVisibility;
    }

    public String getGravatarUrl() {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        md.update(this.email.toLowerCase().getBytes());
        byte[] digest = md.digest();
        String emailMd5 = DatatypeConverter.printHexBinary(digest).toLowerCase();

        return "https://gravatar.com/avatar/" +
                emailMd5 +
                "/?s=80&default=identicon";
    }

    public String getUuid() {
        if (this.uuid.length() < 40) {
            this.uuid = RandomStringUtils.randomAlphanumeric(40);
        }
        return this.uuid;
    }

    public boolean isSuperuser() {
        return "S".equals(this.accessLevel) || "A".equals(this.accessLevel);
    }

    public void addOnePoint() {
        this.points += 1;
    }

    public void deductOnePoint() {
        this.points -= 1;
        if (this.points < 0) {
            this.points = 0;
        }
    }

    public boolean isProUser()  {
        return "S".equals(this.accessLevel) || "A".equals(this.accessLevel) || "P".equals(this.accessLevel);
    }
}
