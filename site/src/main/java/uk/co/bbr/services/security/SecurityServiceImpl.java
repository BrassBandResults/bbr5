package uk.co.bbr.services.security;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.services.framework.ValidationException;
import uk.co.bbr.services.security.dao.SiteUserDao;
import uk.co.bbr.services.security.repo.BbrUserRepository;
import uk.co.bbr.services.security.dao.UserRole;
import uk.co.bbr.services.security.ex.AuthenticationFailedException;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SecurityServiceImpl implements SecurityService {

    private final BbrUserRepository bbrUserRepository;
    private final UserService userService;

    @Override
    public String getCurrentUsername() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        if (securityContext.getAuthentication() == null) {
            return null;
        }
        return securityContext.getAuthentication().getName();
    }

    @Override
    public SiteUserDao getCurrentUser() {
        Optional<SiteUserDao> user = this.bbrUserRepository.fetchByUsercode(this.getCurrentUsername());
        if (user.isEmpty()) {
            throw new NotFoundException("Current user not found");
        }
        return user.get();
    }

    @Override
    public SiteUserDao authenticate(String usercode, String plaintextPassword) throws AuthenticationFailedException {
        Optional<SiteUserDao> fetchedUserOptional = this.bbrUserRepository.fetchByUsercode(usercode);
        if (fetchedUserOptional.isEmpty()) {
            throw new AuthenticationFailedException();
        }
        SiteUserDao fetchedUser = fetchedUserOptional.get();
        SiteUserDao loggedInUser;

        if (fetchedUser.getPasswordVersion().equals("D")) { // django password
            DjangoHasher djangoHash = new DjangoHasher();
            boolean success = djangoHash.checkPassword(plaintextPassword, fetchedUser.getPassword());
            if (!success) {
                throw new AuthenticationFailedException();
            }
            loggedInUser = fetchedUser;
        } else { // java password
            String hashedPassword = PasswordTools.hashPassword(fetchedUser.getPasswordVersion(), fetchedUser.getSalt(), usercode, plaintextPassword);
            Optional<SiteUserDao> userOptional = this.bbrUserRepository.loginCheck(usercode, hashedPassword);
            if (userOptional.isEmpty()) {
                throw new AuthenticationFailedException();
            }
            loggedInUser = userOptional.get();
        }

        return loggedInUser;
    }

    @Override
    public SiteUserDao createUserWithDjangoStylePassword(String usercode, String hashedPassword, String email) {
        SiteUserDao newUser = new SiteUserDao();
        newUser.setEmail(email);
        newUser.setAccessLevel(UserRole.MEMBER.getCode());
        newUser.setUsercode(usercode);

        newUser.setCreated(LocalDateTime.now());
        newUser.setCreatedBy("owner");
        newUser.setUpdated(LocalDateTime.now());
        newUser.setUpdatedBy("owner");

        newUser.setSalt("");
        newUser.setPasswordVersion("D");
        newUser.setPassword(hashedPassword);
        newUser.setUuid(RandomStringUtils.randomAlphanumeric(40));
        this.bbrUserRepository.saveAndFlush(newUser);
        return newUser;
    }

    @Override
    public SiteUserDao createUser(String usercode, String plaintextPassword, String email) {
        if (this.userExists(usercode)) {
            throw new ValidationException("User with usercode " + usercode + " already exists");
        }

        String salt = PasswordTools.createSalt();
        String passwordVersion = PasswordTools.latestVersion();

        SiteUserDao newUser = new SiteUserDao();
        newUser.setEmail(email);
        newUser.setAccessLevel(UserRole.MEMBER.getCode());
        newUser.setUsercode(usercode);

        newUser.setCreated(LocalDateTime.now());
        newUser.setCreatedBy("owner");
        newUser.setUpdated(LocalDateTime.now());
        newUser.setUpdatedBy("owner");

        newUser.setSalt(salt);
        newUser.setPasswordVersion(passwordVersion);
        newUser.setPassword(PasswordTools.hashPassword(passwordVersion, salt, usercode, plaintextPassword));
        newUser.setUuid(RandomStringUtils.randomAlphanumeric(40));
        this.bbrUserRepository.saveAndFlush(newUser);
        return newUser;
    }

    @Override
    public void makeUserAdmin(String usercode) {
        Optional<SiteUserDao> matchingUserOptional = this.bbrUserRepository.fetchByUsercode(usercode);
        if (matchingUserOptional.isEmpty()){
            throw NotFoundException.userNotFoundByUsercode(usercode);
        }

        SiteUserDao matchingUser = matchingUserOptional.get();

        matchingUser.setAccessLevel(UserRole.ADMIN.getCode());
        this.bbrUserRepository.saveAndFlush(matchingUser);
    }

    @Override
    public void makeUserPro(String usercode) {
        Optional<SiteUserDao> matchingUserOptional = this.bbrUserRepository.fetchByUsercode(usercode);
        if (matchingUserOptional.isEmpty()){
            throw NotFoundException.userNotFoundByUsercode(usercode);
        }

        SiteUserDao matchingUser = matchingUserOptional.get();

        matchingUser.setAccessLevel(UserRole.PRO.getCode());
        this.bbrUserRepository.saveAndFlush(matchingUser);
    }

    @Override
    public void makeUserSuperuser(String usercode) {
        Optional<SiteUserDao> matchingUserOptional = this.bbrUserRepository.fetchByUsercode(usercode);
        if (matchingUserOptional.isEmpty()){
            throw NotFoundException.userNotFoundByUsercode(usercode);
        }

        SiteUserDao matchingUser = matchingUserOptional.get();

        matchingUser.setAccessLevel(UserRole.SUPERUSER.getCode());
        this.bbrUserRepository.saveAndFlush(matchingUser);
    }

    @Override
    public boolean userExists(String usercode) {
        return this.userService.fetchUserByUsercode(usercode).isPresent();
    }
}
