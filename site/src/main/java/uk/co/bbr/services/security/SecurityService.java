package uk.co.bbr.services.security;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.services.security.dao.BbrUserDao;
import uk.co.bbr.services.security.ex.AuthenticationFailedException;

import java.util.Optional;

public interface SecurityService {
    String getCurrentUsername();

    BbrUserDao authenticate(String email, String plaintextPassword) throws AuthenticationFailedException;

    BbrUserDao createUser(String usercode, String plaintextPassword, String email);
    BbrUserDao createUserWithDjangoStylePassword(String usercode, String hashedPassword, String email);

    void makeUserAdmin(String usercode);

    void makeUserPro(String usercode);

    void makeUserSuperuser(String usercode);

    Optional<BbrUserDao> fetchUserByUsercode(String usercode);
}
