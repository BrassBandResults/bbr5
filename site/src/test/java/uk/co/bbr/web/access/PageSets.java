package uk.co.bbr.web.access;

import com.stripe.model.Person;
import uk.co.bbr.services.bands.BandAliasService;
import uk.co.bbr.services.bands.BandRelationshipService;
import uk.co.bbr.services.bands.BandService;
import uk.co.bbr.services.bands.dao.BandAliasDao;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.bands.dao.BandRelationshipDao;
import uk.co.bbr.services.contests.ContestService;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.events.ContestEventService;
import uk.co.bbr.services.events.ResultService;
import uk.co.bbr.services.events.dao.ContestEventDao;
import uk.co.bbr.services.feedback.FeedbackService;
import uk.co.bbr.services.feedback.dao.FeedbackDao;
import uk.co.bbr.services.feedback.types.FeedbackStatus;
import uk.co.bbr.services.groups.ContestGroupService;
import uk.co.bbr.services.groups.dao.ContestGroupDao;
import uk.co.bbr.services.people.PersonAliasService;
import uk.co.bbr.services.people.PersonRelationshipService;
import uk.co.bbr.services.people.PersonService;
import uk.co.bbr.services.people.dao.PersonAliasDao;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.people.dao.PersonRelationshipDao;
import uk.co.bbr.services.pieces.PieceService;
import uk.co.bbr.services.pieces.dao.PieceDao;
import uk.co.bbr.services.regions.RegionService;
import uk.co.bbr.services.regions.dao.RegionDao;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.UserService;
import uk.co.bbr.services.tags.ContestTagService;
import uk.co.bbr.services.tags.dao.ContestTagDao;
import uk.co.bbr.services.venues.VenueService;
import uk.co.bbr.services.venues.dao.VenueDao;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class PageSets {

    protected void setupData(RegionService regionService,
                             BandService bandService, BandAliasService bandAliasService, BandRelationshipService bandRelationshipService,
                             PersonService personService, PersonRelationshipService personRelationshipService, PersonAliasService personAliasService,
                             VenueService venueService, PieceService pieceService,
                             ContestGroupService contestGroupService, ContestService contestService, ContestEventService contestEventService, ResultService contestResultService, ContestTagService contestTagService,
                             FeedbackService feedbackService, SecurityService securityService) {
        Optional<RegionDao> yorkshire = regionService.fetchBySlug("yorkshire");
        assertTrue(yorkshire.isPresent());

        BandDao rtb = bandService.create("Rothwell Temperance", yorkshire.get());
        BandAliasDao alias = bandAliasService.createAlias(rtb, "Temps");
        assertEquals(1, alias.getId());

        BandDao wallaceArnold = bandService.create("Wallace Arnold Rothwell", yorkshire.get());
        BandRelationshipDao bandrelationship = new BandRelationshipDao();
        bandrelationship.setLeftBand(wallaceArnold);
        bandrelationship.setRightBand(rtb);
        bandrelationship.setRelationship(bandRelationshipService.fetchIsParentOfRelationship());
        bandrelationship = bandRelationshipService.createRelationship(bandrelationship);
        assertEquals(1, bandrelationship.getId());

        PersonDao davidRoberts = personService.create("Roberts", "David");
        PersonAliasDao daveRoberts = new PersonAliasDao();
        daveRoberts.setOldName("Dave Roberts");
        personAliasService.createAlias(davidRoberts, daveRoberts);

        PersonDao gordonRoberts = personService.create("Roberts", "Gordon");
        PersonRelationshipDao personRelationship = new PersonRelationshipDao();
        personRelationship.setLeftPerson(gordonRoberts);
        personRelationship.setRightPerson(davidRoberts);
        personRelationship.setRelationship(personRelationshipService.fetchTypeByName("relationship.person.is-father-of"));
        personRelationship = personRelationshipService.createRelationship(personRelationship);
        assertEquals(1, personRelationship.getId());

        VenueDao stGeorges = venueService.create("St George's Hall");
        assertEquals("st-george-s-hall", stGeorges.getSlug());

        ContestDao yorkshireArea = contestService.create("Yorkshire Area");
        ContestEventDao yorkshireArea2010 = contestEventService.create(yorkshireArea, LocalDate.of(2000, 3, 1));
        yorkshireArea2010.setVenue(stGeorges);
        yorkshireArea2010 = contestEventService.update(yorkshireArea2010);
        contestResultService.addResult(yorkshireArea2010, "1", rtb, davidRoberts);

        ContestGroupDao yorkshireGroup = contestGroupService.create("Yorkshire Group");
        yorkshireArea = contestService.addContestToGroup(yorkshireArea, yorkshireGroup);

        ContestTagDao yorkshireTag = contestTagService.create("Yorkshire");
        yorkshireArea = contestService.addContestTag(yorkshireArea, yorkshireTag);
        yorkshireGroup = contestGroupService.addGroupTag(yorkshireGroup, yorkshireTag);

        ContestTagDao tagToDelete = contestTagService.create("Tag To Delete");

        PieceDao contestMusic = pieceService.create("Contest Music");

        FeedbackDao feedbackNew = new FeedbackDao();
        feedbackNew.setStatus(FeedbackStatus.NEW);
        feedbackNew.setComment("  This is a new feedback  ");
        feedbackNew.setBrowser("SquirrelSoft");
        feedbackNew.setIp("4.3.2.1");
        feedbackNew.setUrl("/bands");
        feedbackService.create(feedbackNew);

        securityService.createUser("tjs", "password", "test.1@brassbandresults.co.uk");
    }

    protected List<String> notFoundPages() {
        List<String> pageList = new ArrayList<>();
        pageList.add("/bands/not-a-band");
        pageList.add("/bands/not-a-band/whits");
        pageList.add("/contests/not-a-contest");
        pageList.add("/contests/not-a-contest/2000-03-01");
        pageList.add("/contests/NOT-A-GROUP");
        pageList.add("/people/not-a-person");
        pageList.add("/people/not-a-person/whits");
        pageList.add("/people/not-a-person/pieces");
        pageList.add("/pieces/not-a-piece");
        pageList.add("/regions/not-a-region");
        pageList.add("/regions/not-a-region/contests");
        pageList.add("/regions/not-a-region/links");
        pageList.add("/tags/not-a-tag");
        pageList.add("/venues/not-a-venue");
        return pageList;
    }

    protected List<String> publicPages() {
        List<String> pageList = new ArrayList<>();
        pageList.add("/");
        pageList.add("/about-us");
        pageList.add("/bands/");
        pageList.add("/bands/R");
        pageList.add("/bands/0");
        pageList.add("/bands/ALL");
        pageList.add("/bands/MAP");
        pageList.add("/bands/rothwell-temperance");
        pageList.add("/bands/rothwell-temperance/whits");
        pageList.add("/bands/rothwell-temperance/embed");
        pageList.add("/bands/rothwell-temperance/embed/non_whit");
        pageList.add("/bands/rothwell-temperance/embed/whit");
        pageList.add("/contests");
        pageList.add("/contests/Y");
        pageList.add("/contests/ALL");
        pageList.add("/contests/yorkshire-area");
        pageList.add("/contests/yorkshire-area/2000-03-01");
        pageList.add("/contests/YORKSHIRE-GROUP");
        pageList.add("/contest-groups");
        pageList.add("/contest-groups/Y");
        pageList.add("/contest-groups/ALL");
        pageList.add("/embed/band/rothwell-temperance/results/1");
        pageList.add("/embed/band/rothwell-temperance/results/2");
        pageList.add("/embed/band/rothwell-temperance/results-all/2023");
        pageList.add("/embed/band/rothwell-temperance/results-non_whit/2023");
        pageList.add("/embed/band/rothwell-temperance/results-whit/2023");
        pageList.add("/faq");
        pageList.add("/feedback/thanks?next=/");
        pageList.add("/leaderboard");
        pageList.add("/people");
        pageList.add("/people/R");
        pageList.add("/people/david-roberts");
        pageList.add("/people/david-roberts/whits");
        pageList.add("/people/david-roberts/pieces");
        pageList.add("/pieces");
        pageList.add("/pieces/C");
        pageList.add("/pieces/0");
        pageList.add("/pieces/ALL");
        pageList.add("/pieces/contest-music");
        pageList.add("/privacy");
        pageList.add("/regions");
        pageList.add("/regions/yorkshire");
        pageList.add("/regions/yorkshire/contests");
        pageList.add("/regions/yorkshire/links");
        pageList.add("/regions/yorkshire/championship/bands.json");
        pageList.add("/statistics");
        pageList.add("/tags");
        pageList.add("/tags/Y");
        pageList.add("/tags/ALL");
        pageList.add("/tags/yorkshire");
        pageList.add("/users/owner");
        pageList.add("/venues");
        pageList.add("/venues/S");
        pageList.add("/venues/0");
        pageList.add("/venues/ALL");
        pageList.add("/venues/MAP");
        pageList.add("/venues/st-george-s-hall");
        return pageList;
    }

    protected List<String> memberPages() {
        List<String> pageList = new ArrayList<>();
        pageList.add("/bands/rothwell-temperance/edit");
        pageList.add("/bands/rothwell-temperance/edit-aliases");
        pageList.add("/bands/rothwell-temperance/edit-aliases/1/show");
        pageList.add("/bands/rothwell-temperance/edit-aliases/1/hide");
        pageList.add("/bands/rothwell-temperance/edit-aliases/1/edit-dates");
        pageList.add("/bands/rothwell-temperance/edit-aliases/1/delete");
        pageList.add("/bands/rothwell-temperance/edit-rehearsals");
        pageList.add("/bands/rothwell-temperance/edit-relationships");
        pageList.add("/bands/rothwell-temperance/edit-relationships/1/delete");
        pageList.add("/contests/YORKSHIRE-GROUP/years");
        pageList.add("/contests/YORKSHIRE-GROUP/2000");
        pageList.add("/create/tag");
        pageList.add("/lookup/band/data.json?s=abc");
        pageList.add("/lookup/contest/data.json?s=abc");
        pageList.add("/lookup/person/data.json?s=abc");
        pageList.add("/profile");
        pageList.add("/people/david-roberts/edit");
        pageList.add("/people/david-roberts/edit-aliases");
        pageList.add("/people/david-roberts/edit-aliases/1/hide");
        pageList.add("/people/david-roberts/edit-aliases/1/show");
        pageList.add("/people/david-roberts/edit-aliases/1/delete");
        pageList.add("/people/david-roberts/edit-relationships");
        pageList.add("/people/david-roberts/edit-relationships/1/delete");
        pageList.add("/tags/tag-to-delete/delete");
        pageList.add("/years");
        return pageList;
    }

    protected List<String> proPages() {
        List<String> pageList = new ArrayList<>();
        pageList.add("/bands/COMPARE");
        pageList.add("/bands/COMPARE/rothwell-temperance");
        pageList.add("/bands/COMPARE/rothwell-temperance/wallace-arnold-rothwell");
        pageList.add("/bands/WINNERS");
        pageList.add("/bands/rothwell-temperance/filter/yorkshire-area");
        pageList.add("/bands/rothwell-temperance/filter/YORKSHIRE-GROUP");
        pageList.add("/bands/rothwell-temperance/tag/yorkshire");
        pageList.add("/contests/yorkshire-area/own-choice");
        pageList.add("/contests/yorkshire-area/wins");
        pageList.add("/people/david-roberts/filter/yorkshire-area");
        pageList.add("/people/david-roberts/filter/YORKSHIRE-GROUP");
        pageList.add("/people/david-roberts/tag/yorkshire");
        pageList.add("/people/COMPARE-CONDUCTORS");
        pageList.add("/people/COMPARE-CONDUCTORS/david-roberts");
        pageList.add("/people/COMPARE-CONDUCTORS/david-roberts/gordon-roberts");
        pageList.add("/people/WINNERS");
        pageList.add("/people/WINNERS/before/1950");
        pageList.add("/people/WINNERS/after/1950");
        pageList.add("/pieces/BY-SECTION/championship");
        pageList.add("/pieces/BEST-OWN-CHOICE");
        pageList.add("/regions/yorkshire/winners");
        pageList.add("/venues/st-george-s-hall/yorkshire-area");
        pageList.add("/venues/st-george-s-hall/years");
        pageList.add("/venues/st-george-s-hall/years/2000");
        pageList.add("/years/2000");
        return pageList;
    }

    protected List<String> superuserPages() {
        List<String> pageList = new ArrayList<>();
        pageList.add("/feedback/queue");
        pageList.add("/feedback/detail/1");
        pageList.add("/feedback/status-change/claim/tjs/1");
        pageList.add("/feedback/status-change/done/tjs/1");
        pageList.add("/feedback/status-change/owner/tjs/1");
        pageList.add("/feedback/status-change/closed/tjs/1");
        pageList.add("/feedback/status-change/inconclusive/tjs/1");
        pageList.add("/feedback/status-change/spam/tjs/1");
        return pageList;
    }

    protected List<String> adminPages() {
        List<String> pageList = new ArrayList<>();
        pageList.add("/feedback/owner");
        pageList.add("/feedback/inconclusive");
        pageList.add("/feedback/spam");
        pageList.add("/user-list");
        pageList.add("/user-list/pro");
        pageList.add("/user-list/superuser");
        pageList.add("/user-list/admin");
        return pageList;
    }
}
