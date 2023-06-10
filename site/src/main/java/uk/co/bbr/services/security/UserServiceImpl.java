package uk.co.bbr.services.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.services.security.dao.BbrUserDao;
import uk.co.bbr.services.security.dao.UserRole;
import uk.co.bbr.services.security.ex.AuthenticationFailedException;
import uk.co.bbr.services.security.repo.BbrUserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final BbrUserRepository bbrUserRepository;

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
}
