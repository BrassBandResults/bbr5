package uk.co.bbr.services.security;

import uk.co.bbr.services.security.dao.BbrUserDao;
import uk.co.bbr.services.security.dao.PendingUserDao;

import java.util.List;
import java.util.Optional;

public interface UserService {

    Optional<BbrUserDao> fetchUserByUsercode(String usercode);

    List<BbrUserDao> fetchTopUsers();

    List<BbrUserDao> findAll();

    List<BbrUserDao> findAllPro();

    List<BbrUserDao> findAllSuperuser();

    List<BbrUserDao> findAllAdmin();

    String registerNewUser(String username, String email, String plainTextPassword);

    List<PendingUserDao> listUnactivatedUsers();

    void activateUser(String activationKey);
}
