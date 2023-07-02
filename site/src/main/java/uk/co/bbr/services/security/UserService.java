package uk.co.bbr.services.security;

import uk.co.bbr.services.security.dao.SiteUserDao;
import uk.co.bbr.services.security.dao.PendingUserDao;

import java.util.List;
import java.util.Optional;

public interface UserService {

    Optional<SiteUserDao> fetchUserByUsercode(String usercode);

    List<SiteUserDao> fetchTopUsers();

    List<SiteUserDao> findAll();

    List<SiteUserDao> findAllPro();

    List<SiteUserDao> findAllSuperuser();

    List<SiteUserDao> findAllAdmin();

    String registerNewUser(String username, String email, String plainTextPassword);

    List<PendingUserDao> listUnactivatedUsers();

    void activateUser(String activationKey);

    Optional<PendingUserDao> fetchPendingUser(String usercode);

    Optional<SiteUserDao> fetchUserByEmail(String email);

    void generateResetPasswordKey(SiteUserDao siteUser);
}
