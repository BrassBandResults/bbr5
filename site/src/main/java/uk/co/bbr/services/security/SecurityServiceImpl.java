package uk.co.bbr.services.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.security.dao.BbrUserDao;
import uk.co.bbr.services.security.dao.UserRole;
import uk.co.bbr.services.security.ex.AuthenticationFailedException;

@Service
@RequiredArgsConstructor
public class SecurityServiceImpl implements SecurityService {
    @Override
    public String getCurrentUsername() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return securityContext.getAuthentication().getName();

    }

    @Override
    public BbrUserDao authenticate(String username, String password) throws AuthenticationFailedException {
        BbrUserDao user = new BbrUserDao(username, UserRole.MEMBER);
        user.setId(1L);
        // TODO do this properly!
        return user;
    }
}
