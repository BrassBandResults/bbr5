package uk.co.bbr.web.people;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import uk.co.bbr.services.bands.types.ResultSetCategory;
import uk.co.bbr.services.contests.ContestService;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.events.PersonResultService;
import uk.co.bbr.services.events.dao.ContestAdjudicatorDao;
import uk.co.bbr.services.events.dao.ContestResultDao;
import uk.co.bbr.services.events.dto.ResultDetailsDto;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.services.groups.ContestGroupService;
import uk.co.bbr.services.groups.dao.ContestGroupDao;
import uk.co.bbr.services.people.PersonAliasService;
import uk.co.bbr.services.people.PersonRelationshipService;
import uk.co.bbr.services.people.PersonService;
import uk.co.bbr.services.people.dao.PersonAliasDao;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.people.dao.PersonProfileDao;
import uk.co.bbr.services.people.dao.PersonRelationshipDao;
import uk.co.bbr.services.people.repo.PersonProfileRepository;
import uk.co.bbr.services.pieces.PieceService;
import uk.co.bbr.services.pieces.dao.PieceDao;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.dao.SiteUserDao;
import uk.co.bbr.services.tags.ContestTagService;
import uk.co.bbr.services.tags.dao.ContestTagDao;
import uk.co.bbr.web.Tools;
import uk.co.bbr.web.security.annotations.IsBbrPro;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class PersonController {

    private final PersonService personService;
    private final PersonAliasService personAliasService;
    private final ContestService contestService;
    private final ContestGroupService contestGroupService;
    private final ContestTagService contestTagService;
    private final PersonRelationshipService personRelationshipService;
    private final PersonProfileRepository personProfileRepository;
    private final PersonResultService personResultService;
    private final PieceService pieceService;
    private final SecurityService securityService;


    @GetMapping("/people/{personSlug:[\\-a-z\\d]{2,}}")
    public String conductingOrProfile(@PathVariable("personSlug") String personSlug) {
        Optional<PersonDao> person = this.personService.fetchBySlug(personSlug);
        if (person.isEmpty()) {
            throw NotFoundException.personNotFoundBySlug(personSlug);
        }

        Optional<PersonProfileDao> profileOpt = this.personProfileRepository.fetchProfileForUser(person.get().getId());
        if (profileOpt.isPresent()) {
            return "redirect:/people/{personSlug}/profile";
        }
        return "redirect:/people/{personSlug}/conductor";
    }

    @GetMapping("/people/{personSlug:[\\-a-z\\d]{2,}}/profile")
    public String personProfile(Model model, @PathVariable("personSlug") String personSlug) {

        Optional<PersonDao> person = this.personService.fetchBySlug(personSlug);
        if (person.isEmpty()) {
            throw NotFoundException.personNotFoundBySlug(personSlug);
        }

        Optional<PersonProfileDao> profileOpt = this.personProfileRepository.fetchProfileForUser(person.get().getId());
        PersonProfileDao profile = null;
        if (profileOpt.isPresent()) {
            profile = profileOpt.get();
        }

        SiteUserDao currentUser = this.securityService.getCurrentUser();
        List<PersonAliasDao> previousNames = this.personAliasService.findVisibleAliases(person.get());
        ResultDetailsDto personConductingResults = this.personResultService.findResultsForConductor(person.get(), ResultSetCategory.ALL);
        int adjudicationsCount = this.personService.fetchAdjudicationCount(person.get());
        int composerCount = this.personService.fetchComposerCount(person.get());
        int arrangerCount = this.personService.fetchArrangerCount(person.get());
        int userAdjudicationsCount = this.personService.fetchUserAdjudicationsCount(currentUser, person.get());
        List<PersonRelationshipDao> personRelationships = this.personRelationshipService.fetchRelationshipsForPerson(person.get());

        model.addAttribute("Person", person.get());
        model.addAttribute("PersonProfile", profile);
        if (profile != null) {
            model.addAttribute("ProfileMarkdown", Tools.markdownToHTML(profile.getProfile()));
        }
        model.addAttribute("PreviousNames", previousNames);
        model.addAttribute("ResultsCount", personConductingResults.getBandNonWhitResults().size());
        model.addAttribute("WhitCount", personConductingResults.getBandWhitResults().size());
        model.addAttribute("AdjudicationsCount", adjudicationsCount);
        model.addAttribute("UserAdjudicationsCount", userAdjudicationsCount);
        model.addAttribute("PieceCount", composerCount + arrangerCount);
        model.addAttribute("PersonRelationships", personRelationships);

        return "people/tabs/person-profile";
    }

    @GetMapping("/people/{personSlug:[\\-a-z\\d]{2,}}/conductor")
    public String personConducting(Model model, @PathVariable("personSlug") String personSlug) {
        Optional<PersonDao> person = this.personService.fetchBySlug(personSlug);
        if (person.isEmpty()) {
            throw NotFoundException.personNotFoundBySlug(personSlug);
        }

        SiteUserDao currentUser = this.securityService.getCurrentUser();
        List<PersonAliasDao> previousNames = this.personAliasService.findVisibleAliases(person.get());
        ResultDetailsDto personConductingResults = this.personResultService.findResultsForConductor(person.get(), ResultSetCategory.ALL);
        int adjudicationsCount = this.personService.fetchAdjudicationCount(person.get());
        int composerCount = this.personService.fetchComposerCount(person.get());
        int arrangerCount = this.personService.fetchArrangerCount(person.get());
        int userAdjudicationsCount = this.personService.fetchUserAdjudicationsCount(currentUser, person.get());
        List<PersonRelationshipDao> personRelationships = this.personRelationshipService.fetchRelationshipsForPerson(person.get());

        if (personConductingResults.getBandAllResults().isEmpty() && adjudicationsCount > 0) {
            return "redirect:/people/{personSlug}/adjudicator";
        }

        if (personConductingResults.getBandAllResults().isEmpty() && (composerCount + arrangerCount) > 0) {
            return "redirect:/people/{personSlug}/pieces";
        }

        if (personConductingResults.getBandAllResults().isEmpty() && (personConductingResults.getBandWhitResults().size()) > 0) {
            return "redirect:/people/{personSlug}/whits";
        }

        Optional<PersonProfileDao> profileOpt = this.personProfileRepository.fetchProfileForUser(person.get().getId());
        PersonProfileDao profile = null;
        if (profileOpt.isPresent()) {
            profile = profileOpt.get();
        }

        model.addAttribute("Person", person.get());
        model.addAttribute("PersonProfile", profile);
        model.addAttribute("PreviousNames", previousNames);
        model.addAttribute("ConductingResults", personConductingResults.getBandNonWhitResults());
        model.addAttribute("WhitResults", personConductingResults.getBandWhitResults());
        model.addAttribute("ResultsCount", personConductingResults.getBandNonWhitResults().size());
        model.addAttribute("WhitCount", personConductingResults.getBandWhitResults().size());
        model.addAttribute("AdjudicationsCount", adjudicationsCount);
        model.addAttribute("UserAdjudicationsCount", userAdjudicationsCount);
        model.addAttribute("PieceCount", composerCount + arrangerCount);
        model.addAttribute("PersonRelationships", personRelationships);

        return "people/tabs/person-conducting";
    }

    @GetMapping("/people/{personSlug:[\\-a-z\\d]{2,}}/whits")
    public String personWhitFriday(Model model, @PathVariable("personSlug") String personSlug) {
        Optional<PersonDao> person = this.personService.fetchBySlug(personSlug);
        if (person.isEmpty()) {
            throw NotFoundException.personNotFoundBySlug(personSlug);
        }

        SiteUserDao currentUser = this.securityService.getCurrentUser();
        List<PersonAliasDao> previousNames = this.personAliasService.findVisibleAliases(person.get());
        ResultDetailsDto personConductingResults = this.personResultService.findResultsForConductor(person.get(), ResultSetCategory.ALL);
        int adjudicationsCount = this.personService.fetchAdjudicationCount(person.get());
        int composerCount = this.personService.fetchComposerCount(person.get());
        int arrangerCount = this.personService.fetchArrangerCount(person.get());
        int userAdjudicationsCount = this.personService.fetchUserAdjudicationsCount(currentUser, person.get());
        List<PersonRelationshipDao> personRelationships = this.personRelationshipService.fetchRelationshipsForPerson(person.get());

        Optional<PersonProfileDao> profileOpt = this.personProfileRepository.fetchProfileForUser(person.get().getId());
        PersonProfileDao profile = null;
        if (profileOpt.isPresent()) {
            profile = profileOpt.get();
        }

        model.addAttribute("Person", person.get());
        model.addAttribute("PersonProfile", profile);
        model.addAttribute("PreviousNames", previousNames);
        model.addAttribute("ConductingResults", personConductingResults.getBandNonWhitResults());
        model.addAttribute("WhitResults", personConductingResults.getBandWhitResults());
        model.addAttribute("ResultsCount", personConductingResults.getBandNonWhitResults().size());
        model.addAttribute("WhitCount", personConductingResults.getBandWhitResults().size());
        model.addAttribute("AdjudicationsCount", adjudicationsCount);
        model.addAttribute("UserAdjudicationsCount", userAdjudicationsCount);
        model.addAttribute("PieceCount", composerCount + arrangerCount);
        model.addAttribute("PersonRelationships", personRelationships);

        return "people/tabs/person-whits";
    }

    @GetMapping("/people/{personSlug:[\\-a-z\\d]{2,}}/pieces")
    public String personPieces(Model model, @PathVariable("personSlug") String personSlug) {
        Optional<PersonDao> person = this.personService.fetchBySlug(personSlug);
        if (person.isEmpty()) {
            throw NotFoundException.personNotFoundBySlug(personSlug);
        }

        SiteUserDao currentUser = this.securityService.getCurrentUser();
        List<PersonAliasDao> previousNames = this.personAliasService.findVisibleAliases(person.get());
        List<PieceDao> personPieces = this.pieceService.findPiecesForPerson(person.get());
        ResultDetailsDto personConductingResults = this.personResultService.findResultsForConductor(person.get(), ResultSetCategory.ALL);
        int adjudicationsCount = this.personService.fetchAdjudicationCount(person.get());
        int composerCount = this.personService.fetchComposerCount(person.get());
        int arrangerCount = this.personService.fetchArrangerCount(person.get());
        int userAdjudicationsCount = this.personService.fetchUserAdjudicationsCount(currentUser, person.get());
        List<PersonRelationshipDao> personRelationships = this.personRelationshipService.fetchRelationshipsForPerson(person.get());

        Optional<PersonProfileDao> profileOpt = this.personProfileRepository.fetchProfileForUser(person.get().getId());
        PersonProfileDao profile = null;
        if (profileOpt.isPresent()) {
            profile = profileOpt.get();
        }

        model.addAttribute("Person", person.get());
        model.addAttribute("PersonProfile", profile);
        model.addAttribute("PreviousNames", previousNames);
        model.addAttribute("ResultsCount", personConductingResults.getBandNonWhitResults().size());
        model.addAttribute("WhitCount", personConductingResults.getBandWhitResults().size());
        model.addAttribute("AdjudicationsCount", adjudicationsCount);
        model.addAttribute("UserAdjudicationsCount", userAdjudicationsCount);
        model.addAttribute("PieceCount", composerCount + arrangerCount);
        model.addAttribute("PersonRelationships", personRelationships);
        model.addAttribute("Pieces", personPieces);

        return "people/tabs/person-pieces";
    }

    @GetMapping("/people/{personSlug:[\\-a-z\\d]{2,}}/adjudicator")
    public String personAdjudications(Model model, @PathVariable("personSlug") String personSlug) {
        Optional<PersonDao> person = this.personService.fetchBySlug(personSlug);
        if (person.isEmpty()) {
            throw NotFoundException.personNotFoundBySlug(personSlug);
        }

        SiteUserDao currentUser = this.securityService.getCurrentUser();
        List<PersonAliasDao> previousNames = this.personAliasService.findVisibleAliases(person.get());
        List<ContestAdjudicatorDao> adjudications = this.personService.fetchAdjudications(person.get());
        ResultDetailsDto personConductingResults = this.personResultService.findResultsForConductor(person.get(), ResultSetCategory.ALL);
        int adjudicationsCount = this.personService.fetchAdjudicationCount(person.get());
        int composerCount = this.personService.fetchComposerCount(person.get());
        int arrangerCount = this.personService.fetchArrangerCount(person.get());
        int userAdjudicationsCount = this.personService.fetchUserAdjudicationsCount(currentUser, person.get());
        List<PersonRelationshipDao> personRelationships = this.personRelationshipService.fetchRelationshipsForPerson(person.get());

        Optional<PersonProfileDao> profileOpt = this.personProfileRepository.fetchProfileForUser(person.get().getId());
        PersonProfileDao profile = null;
        if (profileOpt.isPresent()) {
            profile = profileOpt.get();
        }

        model.addAttribute("Person", person.get());
        model.addAttribute("PersonProfile", profile);
        model.addAttribute("PreviousNames", previousNames);
        model.addAttribute("ResultsCount", personConductingResults.getBandNonWhitResults().size());
        model.addAttribute("WhitCount", personConductingResults.getBandWhitResults().size());
        model.addAttribute("AdjudicationsCount", adjudicationsCount);
        model.addAttribute("UserAdjudicationsCount", userAdjudicationsCount);
        model.addAttribute("PieceCount", composerCount + arrangerCount);
        model.addAttribute("PersonRelationships", personRelationships);
        model.addAttribute("Adjudications", adjudications);

        return "people/tabs/person-adjudications";
    }

    @IsBbrPro
    @GetMapping("/people/{personSlug:[\\-a-z\\d]{2,}}/user-adjudications")
    public String personAdjudicationsForCurrentUser(Model model, @PathVariable("personSlug") String personSlug) {
        Optional<PersonDao> person = this.personService.fetchBySlug(personSlug);
        if (person.isEmpty()) {
            throw NotFoundException.personNotFoundBySlug(personSlug);
        }

        List<PersonAliasDao> previousNames = this.personAliasService.findVisibleAliases(person.get());
        SiteUserDao currentUser = this.securityService.getCurrentUser();
        List<ContestResultDao> userAdjudications = this.personService.fetchPersonalAdjudications(currentUser, person.get());
        ResultDetailsDto personConductingResults = this.personResultService.findResultsForConductor(person.get(), ResultSetCategory.ALL);
        int adjudicationsCount = this.personService.fetchAdjudicationCount(person.get());
        int composerCount = this.personService.fetchComposerCount(person.get());
        int arrangerCount = this.personService.fetchArrangerCount(person.get());
        List<PersonRelationshipDao> personRelationships = this.personRelationshipService.fetchRelationshipsForPerson(person.get());

        Optional<PersonProfileDao> profileOpt = this.personProfileRepository.fetchProfileForUser(person.get().getId());
        PersonProfileDao profile = null;
        if (profileOpt.isPresent()) {
            profile = profileOpt.get();
        }

        model.addAttribute("Person", person.get());
        model.addAttribute("PersonProfile", profile);
        model.addAttribute("PreviousNames", previousNames);
        model.addAttribute("ResultsCount", personConductingResults.getBandNonWhitResults().size());
        model.addAttribute("WhitCount", personConductingResults.getBandWhitResults().size());
        model.addAttribute("AdjudicationsCount", adjudicationsCount);
        model.addAttribute("UserAdjudicationsCount", userAdjudications.size());
        model.addAttribute("PieceCount", composerCount + arrangerCount);
        model.addAttribute("PersonRelationships", personRelationships);
        model.addAttribute("UserAdjudications", userAdjudications);

        return "people/tabs/user-adjudications";
    }

    @IsBbrPro
    @GetMapping("/people/{personSlug:[\\-a-z\\d]{2,}}/filter/{contestSlug:[\\-a-z\\d]{2,}}")
    public String personFilterToContest(Model model, @PathVariable("personSlug") String personSlug, @PathVariable("contestSlug") String contestSlug) {
        Optional<PersonDao> person = this.personService.fetchBySlug(personSlug);
        if (person.isEmpty()) {
            throw NotFoundException.personNotFoundBySlug(personSlug);
        }
        Optional<ContestDao> contest = this.contestService.fetchBySlug(contestSlug);
        if (contest.isEmpty()) {
            throw NotFoundException.contestNotFoundBySlug(contestSlug);
        }

        List<PersonAliasDao> previousNames = this.personAliasService.findVisibleAliases(person.get());
        ResultDetailsDto personConductingResults = this.personResultService.findResultsForConductor(person.get(), ResultSetCategory.ALL, contest.get());
        int adjudicationsCount = this.personService.fetchAdjudicationCount(person.get());
        int composerCount = this.personService.fetchComposerCount(person.get());
        int arrangerCount = this.personService.fetchArrangerCount(person.get());
        List<PersonRelationshipDao> personRelationships = this.personRelationshipService.fetchRelationshipsForPerson(person.get());

        Optional<PersonProfileDao> profileOpt = this.personProfileRepository.fetchProfileForUser(person.get().getId());
        PersonProfileDao profile = null;
        if (profileOpt.isPresent()) {
            profile = profileOpt.get();
        }

        model.addAttribute("Person", person.get());
        model.addAttribute("PersonProfile", profile);
        model.addAttribute("PreviousNames", previousNames);
        model.addAttribute("ConductingResults", personConductingResults.getBandNonWhitResults());
        model.addAttribute("WhitResults", personConductingResults.getBandWhitResults());
        model.addAttribute("ResultsCount", personConductingResults.getBandNonWhitResults().size());
        model.addAttribute("WhitCount", personConductingResults.getBandWhitResults().size());
        model.addAttribute("AdjudicationsCount", adjudicationsCount);
        model.addAttribute("PieceCount", composerCount + arrangerCount);
        model.addAttribute("PersonRelationships", personRelationships);
        model.addAttribute("FilteredTo", contest.get().getName());

        return "people/tabs/person-conducting";
    }

    @IsBbrPro
    @GetMapping("/people/{personSlug:[\\-a-z\\d]{2,}}/filter/{groupSlug:[\\-A-Z\\d]{2,}}")
    public String personFilterToContestGroup(Model model, @PathVariable("personSlug") String personSlug, @PathVariable("groupSlug") String groupSlug) {
        Optional<PersonDao> person = this.personService.fetchBySlug(personSlug);
        if (person.isEmpty()) {
            throw NotFoundException.personNotFoundBySlug(personSlug);
        }
        Optional<ContestGroupDao> group = this.contestGroupService.fetchBySlug(groupSlug);
        if (group.isEmpty()) {
            throw NotFoundException.groupNotFoundBySlug(groupSlug);
        }

        List<PersonAliasDao> previousNames = this.personAliasService.findVisibleAliases(person.get());
        ResultDetailsDto personConductingResults = this.personResultService.findResultsForConductor(person.get(), ResultSetCategory.ALL, group.get());
        int adjudicationsCount = this.personService.fetchAdjudicationCount(person.get());
        int composerCount = this.personService.fetchComposerCount(person.get());
        int arrangerCount = this.personService.fetchArrangerCount(person.get());
        List<PersonRelationshipDao> personRelationships = this.personRelationshipService.fetchRelationshipsForPerson(person.get());

        Optional<PersonProfileDao> profileOpt = this.personProfileRepository.fetchProfileForUser(person.get().getId());
        PersonProfileDao profile = null;
        if (profileOpt.isPresent()) {
            profile = profileOpt.get();
        }

        model.addAttribute("Person", person.get());
        model.addAttribute("PersonProfile", profile);
        model.addAttribute("PreviousNames", previousNames);
        model.addAttribute("ConductingResults", personConductingResults.getBandNonWhitResults());
        model.addAttribute("WhitResults", personConductingResults.getBandWhitResults());
        model.addAttribute("ResultsCount", personConductingResults.getBandNonWhitResults().size());
        model.addAttribute("WhitCount", personConductingResults.getBandWhitResults().size());
        model.addAttribute("AdjudicationsCount", adjudicationsCount);
        model.addAttribute("PieceCount", composerCount + arrangerCount);
        model.addAttribute("PersonRelationships", personRelationships);
        model.addAttribute("FilteredTo", group.get().getName());

        return "people/tabs/person-conducting";
    }

    @IsBbrPro
    @GetMapping("/people/{personSlug:[\\-a-z\\d]{2,}}/tag/{tagSlug:[\\-a-z\\d]{2,}}")
    public String personFilterToTag(Model model, @PathVariable("personSlug") String personSlug, @PathVariable("tagSlug") String tagSlug) {
        Optional<PersonDao> person = this.personService.fetchBySlug(personSlug);
        if (person.isEmpty()) {
            throw NotFoundException.personNotFoundBySlug(personSlug);
        }
        Optional<ContestTagDao> tag = this.contestTagService.fetchBySlug(tagSlug);
        if (tag.isEmpty()) {
            throw NotFoundException.tagNotFoundBySlug(tagSlug);
        }

        List<PersonAliasDao> previousNames = this.personAliasService.findVisibleAliases(person.get());
        ResultDetailsDto personConductingResults = this.personResultService.findResultsForConductor(person.get(), ResultSetCategory.ALL, tag.get());
        int adjudicationsCount = this.personService.fetchAdjudicationCount(person.get());
        int composerCount = this.personService.fetchComposerCount(person.get());
        int arrangerCount = this.personService.fetchArrangerCount(person.get());
        List<PersonRelationshipDao> personRelationships = this.personRelationshipService.fetchRelationshipsForPerson(person.get());

        Optional<PersonProfileDao> profileOpt = this.personProfileRepository.fetchProfileForUser(person.get().getId());
        PersonProfileDao profile = null;
        if (profileOpt.isPresent()) {
            profile = profileOpt.get();
        }

        model.addAttribute("Person", person.get());
        model.addAttribute("PersonProfile", profile);
        model.addAttribute("PreviousNames", previousNames);
        model.addAttribute("ConductingResults", personConductingResults.getBandNonWhitResults());
        model.addAttribute("WhitResults", personConductingResults.getBandWhitResults());
        model.addAttribute("ResultsCount", personConductingResults.getBandNonWhitResults().size());
        model.addAttribute("WhitCount", personConductingResults.getBandWhitResults().size());
        model.addAttribute("AdjudicationsCount", adjudicationsCount);
        model.addAttribute("PieceCount", composerCount + arrangerCount);
        model.addAttribute("PersonRelationships", personRelationships);
        model.addAttribute("FilteredTo", tag.get().getName());

        return "people/tabs/person-conducting";
    }
}
