package uk.co.bbr.services.security;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.security.dao.BbrUserDao;
import uk.co.bbr.services.security.ex.AuthenticationFailedException;

public interface SecurityService {
    String getCurrentUsername();

    BbrUserDao authenticate(String email, String password) throws AuthenticationFailedException;
}
