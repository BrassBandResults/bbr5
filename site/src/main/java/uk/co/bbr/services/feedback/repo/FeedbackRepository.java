package uk.co.bbr.services.feedback.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.co.bbr.services.feedback.dao.FeedbackDao;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<FeedbackDao, Long> {
    @Query("SELECT f FROM FeedbackDao f WHERE f.url = :offset ORDER BY f.id DESC")
    List<FeedbackDao> fetchFeedbackForOffset(String offset);

}
