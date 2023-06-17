package uk.co.bbr.services.feedback.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.co.bbr.services.feedback.dao.FeedbackDao;
import uk.co.bbr.services.feedback.types.FeedbackStatus;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<FeedbackDao, Long> {
    @Query("SELECT f FROM FeedbackDao f WHERE f.url = :offset ORDER BY f.id DESC")
    List<FeedbackDao> fetchFeedbackForOffset(String offset);

    @Query("SELECT f FROM FeedbackDao f WHERE f.status = :statusCode")
    List<FeedbackDao> fetchForType(FeedbackStatus statusCode);

    @Query("SELECT COUNT(f) FROM FeedbackDao f WHERE f.status = :statusCode")
    int fetchCount(FeedbackStatus statusCode);
}
