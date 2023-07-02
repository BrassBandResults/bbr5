package uk.co.bbr.services.security.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.co.bbr.services.security.dao.SiteUserDao;

import java.util.List;
import java.util.Optional;

public interface BbrUserRepository  extends JpaRepository<SiteUserDao, Long> {
    @Query("SELECT u FROM SiteUserDao u WHERE u.usercode = ?1")
    Optional<SiteUserDao> fetchByUsercode(String usercode);

    @Query("SELECT u FROM SiteUserDao u WHERE u.usercode = ?1 AND u.password = ?2 AND u.accessLevel != '0'")
    Optional<SiteUserDao> loginCheck(String usercode, String hashedPassword);

    @Query("SELECT u FROM SiteUserDao u WHERE u.points > 49 AND u.accessLevel != '0' ORDER BY u.points DESC")
    List<SiteUserDao> fetchTopUsers();

    @Query("SELECT u FROM SiteUserDao u WHERE u.accessLevel != '0' ORDER BY u.usercode")
    List<SiteUserDao> fetchAllUsers();

    @Query("SELECT u FROM SiteUserDao u WHERE u.accessLevel = 'P' ORDER BY u.usercode")
    List<SiteUserDao> fetchAllProUsers();

    @Query("SELECT u FROM SiteUserDao u WHERE u.accessLevel = 'S' ORDER BY u.usercode")
    List<SiteUserDao> fetchAllSuperusers();

    @Query("SELECT u FROM SiteUserDao u WHERE u.accessLevel = 'A' ORDER BY u.usercode")
    List<SiteUserDao> fetchAllAdminUsers();

}
