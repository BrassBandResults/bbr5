package uk.co.bbr.services.feedback.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import uk.co.bbr.services.events.types.ContestEventDateResolution;
import uk.co.bbr.services.feedback.types.FeedbackStatus;
import uk.co.bbr.services.framework.AbstractDao;
import uk.co.bbr.web.HtmlTools;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.auditLog += LocalDateTime.now().format(formatter);
        this.auditLog += " ";
        this.auditLog += message;
        this.auditLog += "\n";
    }

    public void setStatus(FeedbackStatus feedbackStatus) {
        this.status = feedbackStatus;
    }

    public String getCreatedDisplay() {
        String dateFormat = "dd MMM yyyy";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
        return this.getCreated().format(formatter);
    }

    public String getCommentHtmlSafe() {
        return HtmlTools.format(this.comment);
    }

    public String getCommentsAdditionalHtmlSafe() {
        return HtmlTools.format(this.commentsAdditional);
    }

    public String getAuditLogHtmlSafe() {
        return HtmlTools.format(this.auditLog);
    }
}
