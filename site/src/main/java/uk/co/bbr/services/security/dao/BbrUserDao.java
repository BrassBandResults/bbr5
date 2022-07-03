package uk.co.bbr.services.security.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import uk.co.bbr.services.framework.AbstractDao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name="user")
public class BbrUserDao extends AbstractDao {

    @Column(name="usercode", length=50, nullable=false)
    private String usercode;

    @Column(name="password", length=100, nullable=false)
    private String password;

    @Column(name="email", length=100, nullable=false)
    private String email;

    @Column(name="access_level", length=1, nullable=false)
    private String accessLevel;

    @Column(name="salt", length=10, nullable=false)
    private String salt;

    @Column(name="password_version", length=1, nullable=false)
    private String passwordVersion;

    public static BbrUserDao testUserCreate(String usercode, UserRole role) {
        BbrUserDao newUser = new BbrUserDao();
        newUser.usercode = usercode;
        newUser.password = "DUMMY";
        newUser.email = "test.email@brassbandresults.co.uk";
        newUser.accessLevel = role.getCode();
        newUser.salt = "SALT";
        newUser.passwordVersion = "";

        return newUser;
    }

    public UserRole getRole() {
        return UserRole.fromCode(this.accessLevel);
    }
}
