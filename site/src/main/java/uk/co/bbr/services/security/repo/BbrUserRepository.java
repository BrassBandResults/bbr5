package uk.co.bbr.services.security.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.co.bbr.services.security.dao.BbrUserDao;

import java.util.Optional;

public interface BbrUserRepository  extends JpaRepository<BbrUserDao, Long> {
    @Query("SELECT u FROM BbrUserDao u WHERE u.usercode = ?1")
    Optional<BbrUserDao> findByUsercode(String usercode);

    @Query("SELECT u FROM BbrUserDao u WHERE u.usercode = ?1 AND u.password = ?2")
    Optional<BbrUserDao> loginCheck(String usercode, String hashedPassword);
}
