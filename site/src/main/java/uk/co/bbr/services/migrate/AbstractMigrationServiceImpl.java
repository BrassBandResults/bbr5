package uk.co.bbr.services.migrate;

import org.jdom2.Element;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.dao.BbrUserDao;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class AbstractMigrationServiceImpl {

    protected final BbrUserDao createUser(String username, SecurityService securityService) {
        if (username == null) {
            return null;
        }

        Optional<BbrUserDao> user = securityService.fetchUserByUsercode(username);
        if (user.isPresent()) {
            return user.get();
        }

        BbrUserDao newUser = securityService.createUser(username, "NoPassword", "migrated@brassbandresults.co.uk");
        return newUser;
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