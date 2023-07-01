package uk.co.bbr.web.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import uk.co.bbr.services.bands.BandService;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.bands.sql.dto.BandWinnersSqlDto;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.services.regions.RegionService;
import uk.co.bbr.services.regions.dao.RegionDao;
import uk.co.bbr.services.regions.dto.LinkSectionDto;
import uk.co.bbr.services.regions.dto.RegionPageDto;
import uk.co.bbr.services.security.UserService;
import uk.co.bbr.services.security.dao.BbrUserDao;
import uk.co.bbr.services.security.dao.PendingUserDao;
import uk.co.bbr.web.security.annotations.IsBbrAdmin;
import uk.co.bbr.web.security.annotations.IsBbrPro;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @IsBbrAdmin
    @GetMapping("/user-list")
    public String userList(Model model) {
        List<BbrUserDao> users = this.userService.findAll();

        model.addAttribute("Users", users);
        model.addAttribute("Type", "all");
        return "users/list";
    }

    @IsBbrAdmin
    @GetMapping("/user-list/pro")
    public String proUserList(Model model) {
        List<BbrUserDao> users = this.userService.findAllPro();

        model.addAttribute("Users", users);
        model.addAttribute("Type", "pro");
        return "users/list";
    }

    @IsBbrAdmin
    @GetMapping("/user-list/superuser")
    public String superUserList(Model model) {
        List<BbrUserDao> users = this.userService.findAllSuperuser();

        model.addAttribute("Users", users);
        model.addAttribute("Type", "superuser");
        return "users/list";
    }

    @IsBbrAdmin
    @GetMapping("/user-list/admin")
    public String adminUserList(Model model) {
        List<BbrUserDao> users = this.userService.findAllAdmin();

        model.addAttribute("Users", users);
        model.addAttribute("Type", "admin");
        return "users/list";
    }

    @IsBbrAdmin
    @GetMapping("/user-list/unactivated")
    public String unactivatedUserList(Model model) {
        List<PendingUserDao> users = this.userService.listUnactivatedUsers();

        model.addAttribute("Users", users);
        return "users/list-unactivated";
    }

    @GetMapping("/users/{usercode:[a-zA-Z0-9@_\\-.]+}")
    public String publicUserPage(Model model, @PathVariable("usercode") String usercode) {

        Optional<BbrUserDao> user = this.userService.fetchUserByUsercode(usercode);
        if (user.isEmpty()) {
            throw NotFoundException.userNotFoundByUsercode(usercode);
        }

        model.addAttribute("User", user.get());

        return "users/public-user";
    }

}
