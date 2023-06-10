package uk.co.bbr.services.security.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.co.bbr.services.security.dao.BbrUserDao;

import java.util.List;
import java.util.Optional;

public interface BbrUserRepository  extends JpaRepository<BbrUserDao, Long> {
    @Query("SELECT u FROM BbrUserDao u WHERE u.usercode = ?1")
    Optional<BbrUserDao> fetchByUsercode(String usercode);

    @Query("SELECT u FROM BbrUserDao u WHERE u.usercode = ?1 AND u.password = ?2")
    Optional<BbrUserDao> loginCheck(String usercode, String hashedPassword);

    @Query("SELECT u FROM BbrUserDao u WHERE u.points > 49 ORDER BY u.points DESC")
    List<BbrUserDao> fetchTopUsers();

    @Query("SELECT u FROM BbrUserDao u ORDER BY u.usercode")
    List<BbrUserDao> fetchAllUsers();

    @Query("SELECT u FROM BbrUserDao u WHERE u.accessLevel = 'P' ORDER BY u.usercode")
    List<BbrUserDao> fetchAllProUsers();

    @Query("SELECT u FROM BbrUserDao u WHERE u.accessLevel = 'S' ORDER BY u.usercode")
    List<BbrUserDao> fetchAllSuperusers();

    @Query("SELECT u FROM BbrUserDao u WHERE u.accessLevel = 'A' ORDER BY u.usercode")
    List<BbrUserDao> fetchAllAdminUsers();

}
