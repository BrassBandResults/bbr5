package uk.co.bbr.services.feedback.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import uk.co.bbr.services.feedback.types.FeedbackStatus;
import uk.co.bbr.services.framework.AbstractDao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor
@Table(name="site_feedback")
public class FeedbackDao extends AbstractDao {

    @Column(name="url", nullable=false)
    private String url;

    @Column(name="comment", nullable=false)
    private String comment;

    @Column(name="status", nullable=false)
    private FeedbackStatus status;

    @Column(name="browser_string", nullable=false)
    private String browser;

    @Column(name="ip", nullable=false, length = 15)
    private String ip;

    @Column(name="additional_comments")
    private String commentsAdditional;

    @Column(name="audit_log")
    private String auditLog;

    @Column(name="reported_by")
    private String reportedBy;

    public void setUrl(String url) {
        this.url = url;
    }

    public void setReportedBy(String usercode) {
        this.reportedBy = usercode;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }

    public void setIp(String ip) {
        if (ip != null && ip.trim().length() > 15) {
            ip = ip.trim().substring(0, 15);
        }
        this.ip = ip;
    }

    public void addAuditLog(String message) {
        if (this.auditLog == null) {
            this.auditLog = "";
        }

        this.auditLog += LocalDateTime.now().toString();
        this.auditLog += " ";
        this.auditLog += message;
        this.auditLog += "\n";
    }

    public void setStatus(FeedbackStatus feedbackStatus) {
        this.status = feedbackStatus;
    }
}
