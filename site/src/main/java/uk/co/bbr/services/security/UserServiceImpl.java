package uk.co.bbr.services.security;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.email.EmailService;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.services.security.dao.BbrUserDao;
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
    public Optional<BbrUserDao> fetchUserByUsercode(String usercode) {
        return this.bbrUserRepository.fetchByUsercode(usercode);
    }

    @Override
    public List<BbrUserDao> fetchTopUsers() {
        return this.bbrUserRepository.fetchTopUsers();
    }

    @Override
    public List<BbrUserDao> findAll() {
        return this.bbrUserRepository.fetchAllUsers();
    }

    @Override
    public List<BbrUserDao> findAllPro() {
        return this.bbrUserRepository.fetchAllProUsers();
    }

    @Override
    public List<BbrUserDao> findAllSuperuser() {
        return this.bbrUserRepository.fetchAllSuperusers();
    }

    @Override
    public List<BbrUserDao> findAllAdmin() {
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

        BbrUserDao newUser = new BbrUserDao();
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
        Optional<BbrUserDao> fetchedUser = this.bbrUserRepository.fetchByUsercode(pendingUserDao.getUsercode());
        if (fetchedUser.isEmpty()) {
            throw NotFoundException.userNotFoundByActivationKey();
        }

        // user is already created, just need to set a valid password and allow access
        fetchedUser.get().setPassword(pendingUserDao.getPassword());
        fetchedUser.get().setAccessLevel(UserRole.MEMBER.getCode());
        this.bbrUserRepository.saveAndFlush(fetchedUser.get());

        this.pendingUserRepository.delete(matchingUser.get());
    }

    @Override
    public Optional<PendingUserDao> fetchPendingUser(String usercode) {
        return this.pendingUserRepository.findByUsercode(usercode);
    }
}
