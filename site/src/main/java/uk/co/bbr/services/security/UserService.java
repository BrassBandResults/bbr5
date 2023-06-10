package uk.co.bbr.services.security;

import uk.co.bbr.services.security.dao.BbrUserDao;

import java.util.List;
import java.util.Optional;

public interface UserService {

    Optional<BbrUserDao> fetchUserByUsercode(String usercode);

    List<BbrUserDao> fetchTopUsers();
}
