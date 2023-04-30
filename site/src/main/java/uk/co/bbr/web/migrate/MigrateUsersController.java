package uk.co.bbr.web.migrate;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import uk.co.bbr.services.security.dao.BbrUserDao;
import uk.co.bbr.services.security.dao.UserRole;
import uk.co.bbr.services.security.repo.BbrUserRepository;
import uk.co.bbr.web.security.annotations.IsBbrAdmin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static uk.co.bbr.web.migrate.MigrateController.BASE_PATH;

@Controller
@RequiredArgsConstructor
public class MigrateUsersController {

    private final BbrUserRepository bbrUserRepository;

    @GetMapping("/migrate/Users")
    @IsBbrAdmin
    public String processResults(Model model) throws IOException {
        List<String> messages = new ArrayList<>();
        messages.add("Importing users...");

        File userFile = new File("/tmp/bbr-users.csv");
        this.importUsers(userFile);

        model.addAttribute("Messages", messages);
        model.addAttribute("Next", "/");

        return "migrate/migrate";
    }

    private void importUsers(File userFile) throws IOException {
        List<String> csvLines = Files.readAllLines(Paths.get(userFile.getAbsolutePath()));
        for (String eachUserLine : csvLines) {
            if (eachUserLine.startsWith("id")) {
                continue;
            }
            String[] userLine = eachUserLine.split(",");
            this.upsertUser(userLine);
        }
    }

    private void upsertUser(String[] userLine){
        String oldUserId = userLine[0];
        String username = userLine[1];
        String email = userLine[2];
        String password = userLine[3];
        String lastLogin = userLine[4];
        String dateJoined = userLine[5];
        String oldProfileId = userLine[6];
        String points = userLine[7];
        String contestHistoryVisibility = userLine[8];
        String enhancedFunctionality = userLine[9];
        String superUser = userLine[10];
        String proMember = userLine[11];
        String stripeEmail = null;
        String stripeToken = null;
        String stripeCustomer = null;
        if (userLine.length > 12) {
            stripeEmail = userLine[12];
            stripeToken = userLine[13];
            if (userLine.length > 14) {
                stripeCustomer = userLine[14];
            }
        }

        Optional<BbrUserDao> matchingUser = this.bbrUserRepository.fetchByUsercode(username);
        if (matchingUser.isEmpty()) {
            System.out.println(username + " - creating");

            BbrUserDao user = new BbrUserDao();
            user.setUsercode(username);

            user.setOldId(Integer.parseInt(oldUserId));
            user.setEmail(email);
            user.setPasswordVersion("D");
            user.setPassword(password);
            user.setLastLogin(this.parseDateTime(lastLogin));
            user.setCreated(this.parseDateTime(dateJoined));
            user.setUpdated(LocalDateTime.now());
            user.setPoints(Integer.parseInt(points));
            user.setContestHistoryVisibility(contestHistoryVisibility);
            user.setAccessLevel(UserRole.MEMBER.getCode());
            if ("t".equals(proMember)) {
                user.setAccessLevel(UserRole.PRO.getCode());
            }
            if ("t".equals(superUser)) {
                user.setAccessLevel(UserRole.SUPERUSER.getCode());
            }
            user.setStripeEmail(stripeEmail);
            user.setStripeToken(stripeToken);
            user.setStripeCustomer(stripeCustomer);

            user.setSalt("DJANGO");

            user.setCreatedBy("tjs");
            user.setUpdatedBy("tjs");


            this.bbrUserRepository.saveAndFlush(user);
        }
        if (matchingUser.isPresent()) {
            // update user
            BbrUserDao user = matchingUser.get();

            System.out.println(username + " - updating");

            user.setOldId(Integer.parseInt(oldUserId));
            user.setEmail(email);
            user.setPasswordVersion("D");
            user.setPassword(password);
            user.setLastLogin(this.parseDateTime(lastLogin));
            user.setCreated(this.parseDateTime(dateJoined));
            user.setUpdated(LocalDateTime.now());
            user.setPoints(Integer.parseInt(points));
            user.setContestHistoryVisibility(contestHistoryVisibility);
            user.setAccessLevel(UserRole.MEMBER.getCode());
            if ("t".equals(proMember)) {
                user.setAccessLevel(UserRole.PRO.getCode());
            }
            if ("t".equals(superUser)) {
                user.setAccessLevel(UserRole.SUPERUSER.getCode());
            }
            user.setStripeEmail(stripeEmail);
            user.setStripeToken(stripeToken);
            user.setStripeCustomer(stripeCustomer);

            user.setSalt("DJANGO");

            user.setCreatedBy("tjs");
            user.setUpdatedBy("tjs");

            this.bbrUserRepository.saveAndFlush(user);
        }
    }

    private LocalDateTime parseDateTime(String dateTime) {
        if (dateTime.trim().length() == 0) {
            return null;
        }
        // 2015-08-18 08:08:27.143742

        if (dateTime.indexOf("+") > -1){
            dateTime = dateTime.substring(0, dateTime.indexOf("+"));
        }
        if (dateTime.indexOf(".") > -1){
            dateTime = dateTime.substring(0, dateTime.indexOf("."));
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(dateTime, formatter);
    }

}
