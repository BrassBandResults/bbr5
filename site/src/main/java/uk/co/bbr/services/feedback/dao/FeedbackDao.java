package uk.co.bbr.services.feedback.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import uk.co.bbr.services.feedback.types.FeedbackStatus;
import uk.co.bbr.services.framework.AbstractDao;
import uk.co.bbr.services.security.dao.SiteUserDao;
import uk.co.bbr.web.Tools;
import uk.co.bbr.web.security.annotations.IsBbrSuperuser;

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

    @Column(name="owned_by")
    private String ownedBy;

    public void setUrl(String url) {
        if (url != null) {
            url = url.strip();
        }
        this.url = url;
    }

    public void setReportedBy(String usercode) {
        if (usercode != null) {
            usercode = usercode.strip();
        }
        this.reportedBy = usercode;
    }

    public void setComment(String comment) {
        if (comment != null) {
            comment = comment.strip();
        }
        this.comment = comment;
    }

    public void setBrowser(String browser) {
        if (browser != null) {
            browser = browser.strip();
        }
        this.browser = browser;
    }

    public void setOwnedBy(String username) {
        if (username != null) {
            username = username.strip();
        }
        this.ownedBy = username;
    }

    public void setIp(String ip) {
        if (ip != null && ip.strip().length() > 15) {
            ip = ip.strip().substring(0, 15);
        }
        if (ip != null) {
            ip = ip.strip();
        }
        this.ip = ip;
    }

    public void addAuditLog(String currentUsercode, String message) {
        if (this.auditLog == null) {
            this.auditLog = "";
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.auditLog += LocalDateTime.now().format(formatter);
        this.auditLog += " ";
        this.auditLog += currentUsercode;
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
        return Tools.format(this.comment);
    }

    public String getCommentsAdditionalHtmlSafe() {
        return Tools.format(this.commentsAdditional);
    }

    public String getAuditLogHtmlSafe() {
        return Tools.format(this.auditLog);
    }

    @IsBbrSuperuser
    public void assignToUser(String currentUsername, SiteUserDao siteUserDao) {
        this.setStatus(FeedbackStatus.WITH_USER);
        this.setOwnedBy(siteUserDao.getUsercode());
        this.addAuditLog(currentUsername, "Assigning to user " + siteUserDao.getUsercode());
    }

    public void markDone(String currentUsername) {
        this.setStatus(FeedbackStatus.DONE);
        this.setOwnedBy(currentUsername);
        this.addAuditLog(currentUsername, "Setting status to done");
    }

    public void sendToOwner(String currentUsername) {
        this.setStatus(FeedbackStatus.OWNER);
        this.setOwnedBy("owner");
        this.addAuditLog(currentUsername, "Assigning to owner");
    }

    public void markClosed(String currentUsername) {
        this.setStatus(FeedbackStatus.CLOSED);
        this.setOwnedBy(currentUsername);
        this.addAuditLog(currentUsername, "Setting status closed");
    }

    public void markInconclusive(String currentUsername) {
        this.setStatus(FeedbackStatus.INCONCLUSIVE);
        this.setOwnedBy(currentUsername);
        this.addAuditLog(currentUsername, "Setting status inconclusive");
    }

    public void markAsSpam(String currentUsername) {
        this.setStatus(FeedbackStatus.SPAM);
        this.setOwnedBy(currentUsername);
        this.addAuditLog(currentUsername, "Setting status to spam");
    }
}
