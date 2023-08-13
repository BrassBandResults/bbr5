package uk.co.bbr.services.security.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.co.bbr.services.framework.AbstractDao;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name="site_user_pending")
public class PendingUserDao extends AbstractDao {

    @Column(name="usercode", length=50, nullable=false)
    private String usercode;

    @Column(name="password", length=100, nullable=false)
    private String password;

    @Column(name="email", length=100, nullable=false)
    private String email;

    @Column(name="salt", length=10, nullable=false)
    private String salt;

    @Column(name="activation_key", length=40, nullable=false)
    private String activationKey;
}
