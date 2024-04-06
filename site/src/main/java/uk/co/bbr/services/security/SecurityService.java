package uk.co.bbr.services.security;

import uk.co.bbr.services.security.dao.SiteUserDao;
import uk.co.bbr.services.security.ex.AuthenticationFailedException;

public interface SecurityService {
    String getCurrentUsername();

    SiteUserDao getCurrentUser();

    SiteUserDao authenticate(String usercode, String plaintextPassword) throws AuthenticationFailedException;

    SiteUserDao createUser(String usercode, String plaintextPassword, String email);

    SiteUserDao createUserWithDjangoStylePassword(String usercode, String hashedPassword, String email);

    void makeUserAdmin(String usercode);

    void makeUserPro(String usercode);

    void removeProFlag(SiteUserDao siteUserDao);

    void makeUserSuperuser(String usercode);

    boolean userExists(String username);

    void update(SiteUserDao user);

    void addOnePoint(String userCode);

    void deductOnePoint(String userCode);
}
