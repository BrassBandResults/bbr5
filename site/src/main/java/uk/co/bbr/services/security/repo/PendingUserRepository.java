package uk.co.bbr.services.security.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.co.bbr.services.security.dao.PendingUserDao;

import java.util.List;
import java.util.Optional;

public interface PendingUserRepository extends JpaRepository<PendingUserDao, Long> {
    @Query("SELECT p FROM PendingUserDao p ORDER BY p.created DESC")
    List<PendingUserDao> listByDate();

    @Query("SELECT p FROM PendingUserDao p WHERE p.activationKey = :activationKey")
    Optional<PendingUserDao> findByKey(String activationKey);
}
