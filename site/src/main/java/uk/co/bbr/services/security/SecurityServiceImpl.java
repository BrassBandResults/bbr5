package uk.co.bbr.services.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.services.security.dao.BbrUserDao;
import uk.co.bbr.services.security.dao.BbrUserRepository;
import uk.co.bbr.services.security.ex.AuthenticationFailedException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SecurityServiceImpl implements SecurityService {

    @Autowired
    private BbrUserRepository bbrUserRepository;

    @Override
    public String getCurrentUsername() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return securityContext.getAuthentication().getName();

    }

    @Override
    public BbrUserDao authenticate(String usercode, String plaintextPassword) throws AuthenticationFailedException {
        Optional<BbrUserDao> fetchedUserOptional = this.bbrUserRepository.findByUsercode(usercode);
        if (fetchedUserOptional.isEmpty()) {
            throw new AuthenticationFailedException();
        }
        BbrUserDao fetchedUser = fetchedUserOptional.get();

        String hashedPassword = PasswordTools.hashPassword(fetchedUser.getPasswordVersion(), fetchedUser.getSalt(), usercode, plaintextPassword);

        Optional<BbrUserDao> userOptional = this.bbrUserRepository.loginCheck(usercode, hashedPassword);
        if (userOptional.isEmpty()) {

        }
        return userOptional.get();
    }

    @Override
    public void createUser(String usercode, String plaintextPassword, String email) {
        String salt = PasswordTools.createSalt();
        String passwordVersion = PasswordTools.latestVersion();

        BbrUserDao newUser = new BbrUserDao();
        newUser.setEmail(email);
        newUser.setAccessLevel("M");
        newUser.setUsercode(usercode);

        newUser.setSalt(salt);
        newUser.setPasswordVersion(passwordVersion);
        newUser.setPassword(PasswordTools.hashPassword(passwordVersion, salt, usercode, plaintextPassword));
        this.bbrUserRepository.save(newUser);
    }

    @Override
    public void makeUserAdmin(String usercode) {
        Optional<BbrUserDao> matchingUserOptional = this.bbrUserRepository.findByUsercode(usercode);
        if (matchingUserOptional.isEmpty()){
            throw new NotFoundException("User " + usercode + " not found");
        }

        BbrUserDao matchingUser = matchingUserOptional.get();

        matchingUser.setAccessLevel("A");
        this.bbrUserRepository.save(matchingUser);
    }

    @Override
    public void makeUserPro(String usercode) {
        Optional<BbrUserDao> matchingUserOptional = this.bbrUserRepository.findByUsercode(usercode);
        if (matchingUserOptional.isEmpty()){
            throw new NotFoundException("User " + usercode + " not found");
        }

        BbrUserDao matchingUser = matchingUserOptional.get();

        matchingUser.setAccessLevel("P");
        this.bbrUserRepository.save(matchingUser);
    }

    @Override
    public void makeUserSuperuser(String usercode) {
        Optional<BbrUserDao> matchingUserOptional = this.bbrUserRepository.findByUsercode(usercode);
        if (matchingUserOptional.isEmpty()){
            throw new NotFoundException("User " + usercode + " not found");
        }

        BbrUserDao matchingUser = matchingUserOptional.get();

        matchingUser.setAccessLevel("S");
        this.bbrUserRepository.save(matchingUser);
    }
}
