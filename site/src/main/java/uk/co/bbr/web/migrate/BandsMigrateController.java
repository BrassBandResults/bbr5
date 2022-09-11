package uk.co.bbr.web.migrate;

import lombok.RequiredArgsConstructor;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import uk.co.bbr.web.security.annotations.IsBbrAdmin;

import java.io.File;

@Controller
@RequiredArgsConstructor
public class BandsMigrateController {
    @GetMapping("/migrate/bands")
    @IsBbrAdmin
    public String home(Model model) throws GitAPIException {

        Git.cloneRepository()
                .setURI("https://github.com/BrassBandResults/bbr-data.git")
                .setDirectory(new File("/tmp/bbr"))
                .call();

        return "redirect:/";
    }
}
