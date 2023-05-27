package uk.co.bbr.web.people;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import uk.co.bbr.services.people.PersonService;
import uk.co.bbr.services.people.sql.dto.PeopleBandsSqlDto;
import uk.co.bbr.web.people.forms.ComparePeopleForm;

import javax.swing.*;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class CompareController {
    private final PersonService personService;

    @GetMapping("/people/COMPARE-CONDUCTORS")
    public String showPeopleBands(Model model) {
        ComparePeopleForm comparePeopleForm = new ComparePeopleForm();
        model.addAttribute("ComparePeopleForm", comparePeopleForm);

        return "people/compare-select";
    }
}
