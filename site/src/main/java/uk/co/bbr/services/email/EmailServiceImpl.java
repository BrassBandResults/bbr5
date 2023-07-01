package uk.co.bbr.services.email;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.security.PasswordTools;
import uk.co.bbr.services.security.dao.BbrUserDao;
import uk.co.bbr.services.security.dao.PendingUserDao;
import uk.co.bbr.services.security.repo.BbrUserRepository;
import uk.co.bbr.services.security.repo.PendingUserRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    @Override
    public void sendActivationEmail(String email, String activationKey) {

    }
}
