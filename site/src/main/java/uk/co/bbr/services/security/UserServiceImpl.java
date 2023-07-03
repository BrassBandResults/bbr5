package uk.co.bbr.services.security;

import lombok.RequiredArgsConstructor;
import net.bytebuddy.utility.RandomString;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.email.EmailService;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.services.security.dao.SiteUserDao;
import uk.co.bbr.services.security.dao.PendingUserDao;
import uk.co.bbr.services.security.dao.UserRole;
import uk.co.bbr.services.security.repo.BbrUserRepository;
import uk.co.bbr.services.security.repo.PendingUserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final BbrUserRepository bbrUserRepository;
    private final PendingUserRepository pendingUserRepository;
    private final EmailService emailService;

    @Override
    public Optional<SiteUserDao> fetchUserByUsercode(String usercode) {
        return this.bbrUserRepository.fetchByUsercode(usercode);
    }

    @Override
    public List<SiteUserDao> fetchTopUsers() {
        return this.bbrUserRepository.fetchTopUsers();
    }

    @Override
    public List<SiteUserDao> findAll() {
        return this.bbrUserRepository.fetchAllUsers();
    }

    @Override
    public List<SiteUserDao> findAllPro() {
        return this.bbrUserRepository.fetchAllProUsers();
    }

    @Override
    public List<SiteUserDao> findAllSuperuser() {
        return this.bbrUserRepository.fetchAllSuperusers();
    }

    @Override
    public List<SiteUserDao> findAllAdmin() {
        return this.bbrUserRepository.fetchAllAdminUsers();
    }

    @Override
    public String registerNewUser(String usercode, String email, String plainTextPassword) {
        String salt = PasswordTools.createSalt();
        String hashedPassword = PasswordTools.newPassword(salt, usercode, plainTextPassword);

        String activationKey = RandomStringUtils.randomAlphanumeric(40);

        PendingUserDao pendingUser = new PendingUserDao();
        pendingUser.setUsercode(usercode);
        pendingUser.setEmail(email);
        pendingUser.setSalt(salt);
        pendingUser.setPassword(hashedPassword);
        pendingUser.setActivationKey(activationKey);
        pendingUser.setCreatedBy("owner");
        pendingUser.setCreated(LocalDateTime.now());
        pendingUser.setUpdatedBy("owner");
        pendingUser.setUpdated(LocalDateTime.now());
        this.pendingUserRepository.saveAndFlush(pendingUser);

        SiteUserDao newUser = new SiteUserDao();
        newUser.setEmail(pendingUser.getEmail());
        newUser.setAccessLevel(UserRole.NO_ACCESS.getCode());
        newUser.setUsercode(pendingUser.getUsercode());

        newUser.setCreated(pendingUser.getCreated());
        newUser.setCreatedBy(pendingUser.getCreatedBy());
        newUser.setUpdated(LocalDateTime.now());
        newUser.setUpdatedBy("owner");

        newUser.setSalt(pendingUser.getSalt());
        newUser.setPasswordVersion(PasswordTools.latestVersion());
        newUser.setPassword(RandomStringUtils.randomAlphanumeric(5)); // temporary invalid password
        newUser.setUuid(RandomStringUtils.randomAlphanumeric(40));
        this.bbrUserRepository.saveAndFlush(newUser);

        this.emailService.sendActivationEmail(email, pendingUser.getActivationKey());

        return pendingUser.getActivationKey();
    }

    @Override
    public List<PendingUserDao> listUnactivatedUsers() {
        return this.pendingUserRepository.listByDate();
    }

    @Override
    public void activateUser(String activationKey) {
        Optional<PendingUserDao> matchingUser = this.pendingUserRepository.findByKey(activationKey);
        if (matchingUser.isEmpty()) {
            throw NotFoundException.userNotFoundByActivationKey();
        }

        PendingUserDao pendingUserDao = matchingUser.get();
        Optional<SiteUserDao> fetchedUser = this.bbrUserRepository.fetchByUsercode(pendingUserDao.getUsercode());
        if (fetchedUser.isEmpty()) {
            throw NotFoundException.userNotFoundByActivationKey();
        }

        // user is already created, just need to set a valid password and allow access
        fetchedUser.get().setPassword(pendingUserDao.getPassword());
        fetchedUser.get().setSalt(pendingUserDao.getSalt());
        fetchedUser.get().setAccessLevel(UserRole.MEMBER.getCode());
        this.bbrUserRepository.saveAndFlush(fetchedUser.get());

        this.pendingUserRepository.delete(matchingUser.get());
    }

    @Override
    public Optional<PendingUserDao> fetchPendingUser(String usercode) {
        return this.pendingUserRepository.findByUsercode(usercode);
    }

    @Override
    public Optional<SiteUserDao> fetchUserByEmail(String email) {
        List<SiteUserDao> users = this.bbrUserRepository.fetchByEmail(email);
        if (users.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(users.get(0));
    }

    @Override
    public void sendResetPasswordEmail(SiteUserDao siteUser) {
        siteUser.setResetPasswordKey(RandomStringUtils.randomAlphanumeric(40));
        this.bbrUserRepository.saveAndFlush(siteUser);
        this.emailService.sendResetPasswordEmail(siteUser);
    }

    @Override
    public Optional<SiteUserDao> fetchUserByResetPasswordKey(String resetKey) {
        return this.bbrUserRepository.fetchByResetKey(resetKey);
    }

    @Override
    public void changePassword(SiteUserDao siteUser, String plaintextPassword) {
        Optional<SiteUserDao> matchingUser = this.fetchUserByUsercode(siteUser.getUsercode());
        if (matchingUser.isEmpty()) {
            throw NotFoundException.userNotFoundByActivationKey();
        }

        matchingUser.get().setSalt(PasswordTools.createSalt());
        matchingUser.get().setPasswordVersion(PasswordTools.latestVersion());
        matchingUser.get().setPassword(PasswordTools.hashPassword(PasswordTools.latestVersion(), matchingUser.get().getSalt(), siteUser.getUsercode(), plaintextPassword));

        this.bbrUserRepository.saveAndFlush(matchingUser.get());
    }
}
