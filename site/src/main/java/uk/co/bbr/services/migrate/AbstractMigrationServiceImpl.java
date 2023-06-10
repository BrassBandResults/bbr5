package uk.co.bbr.services.migrate;

import org.jdom2.Element;
import uk.co.bbr.services.framework.annotations.IgnoreCoverage;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.UserService;
import uk.co.bbr.services.security.dao.BbrUserDao;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@IgnoreCoverage
public class AbstractMigrationServiceImpl {

    protected final String createUser(String username, SecurityService securityService, UserService userService) {
        if (username == null) {
            return null;
        }

        Optional<BbrUserDao> user = userService.fetchUserByUsercode(username);
        if (user.isPresent()) {
            return user.get().getUsercode();
        }

        BbrUserDao newUser = securityService.createUser(username, "NoPassword", "migrated@brassbandresults.co.uk");
        return newUser.getUsercode();
    }

    protected final String notBlank(Element node, String childName) {
        if (node == null) {
            throw new UnsupportedOperationException("Node passed is null");
        }
        String value = node.getChildText(childName);
        if ("None".equals(value)) {
            return null;
        }
        if (value == null) {
            return null;
        }

        if (value.trim().length() == 0) {
            return null;
        }

        return value;
    }

    protected final LocalDate notBlankDate(Element node, String childName) {
        String value = this.notBlank(node, childName);
        if (value == null) {
            return null;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(value, formatter);
    }

    protected final LocalDateTime notBlankDateTime(Element node, String childName) {
        String value = this.notBlank(node, childName);
        if (value == null) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime returnValue = LocalDateTime.parse(value, formatter);
        return returnValue;
    }

    protected final boolean notBlankBoolean(Element node, String childName) {
        String value = this.notBlank(node, childName);
        return "true".equalsIgnoreCase(value);
    }

    protected final Integer notBlankInteger(Element node, String childName) {
        String value = this.notBlank(node, childName);
        if (value == null) {
            return null;
        }
        return Integer.parseInt(value);
    }
}
