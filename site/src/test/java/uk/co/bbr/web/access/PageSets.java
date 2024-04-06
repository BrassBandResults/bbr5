package uk.co.bbr.web.access;

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
import uk.co.bbr.services.events.dao.ContestResultDao;
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
import uk.co.bbr.services.performances.PerformanceService;
import uk.co.bbr.services.pieces.PieceAliasService;
import uk.co.bbr.services.pieces.PieceService;
import uk.co.bbr.services.pieces.dao.PieceAliasDao;
import uk.co.bbr.services.pieces.dao.PieceDao;
import uk.co.bbr.services.regions.RegionService;
import uk.co.bbr.services.regions.dao.RegionDao;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.dao.SiteUserDao;
import uk.co.bbr.services.tags.ContestTagService;
import uk.co.bbr.services.tags.dao.ContestTagDao;
import uk.co.bbr.services.venues.VenueAliasService;
import uk.co.bbr.services.venues.VenueService;
import uk.co.bbr.services.venues.dao.VenueAliasDao;
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
                             FeedbackService feedbackService, SecurityService securityService, PerformanceService performanceService, VenueAliasService venueAliasService, PieceAliasService pieceAliasService) {
        Optional<RegionDao> yorkshire = regionService.fetchBySlug("yorkshire");
        assertTrue(yorkshire.isPresent());

        SiteUserDao owner = securityService.getCurrentUser();

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
        VenueAliasDao newAlias = new VenueAliasDao();
        newAlias.setName("St Georges");
        VenueAliasDao stGeorgeALias = venueAliasService.createAlias(stGeorges, newAlias);

        ContestDao yorkshireArea = contestService.create("Yorkshire Area");
        ContestEventDao yorkshireArea2010 = contestEventService.create(yorkshireArea, LocalDate.of(2000, 3, 1));
        yorkshireArea2010.setVenue(stGeorges);
        yorkshireArea2010 = contestEventService.update(yorkshireArea2010);
        ContestResultDao result = contestResultService.addResult(yorkshireArea2010, "1", rtb, davidRoberts);
        assertEquals(1, result.getId());

        performanceService.linkUserPerformance(owner, result);

        ContestGroupDao yorkshireGroup = contestGroupService.create("Yorkshire Group");
        yorkshireArea = contestService.addContestToGroup(yorkshireArea, yorkshireGroup);

        ContestTagDao yorkshireTag = contestTagService.create("Yorkshire");
        yorkshireArea = contestService.addContestTag(yorkshireArea, yorkshireTag);
        yorkshireGroup = contestGroupService.addGroupTag(yorkshireGroup, yorkshireTag);

        ContestTagDao tagToDelete = contestTagService.create("Tag To Delete");

        PieceDao contestMusic = pieceService.create("Contest Music");
        PieceAliasDao pieceAlias = new PieceAliasDao();
        pieceAlias.setName("ContestMusic");
        PieceAliasDao musicAlias = pieceAliasService.createAlias(contestMusic, pieceAlias);

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
        pageList.add("/calendar/1799/1");
        pageList.add("/calendar/2101/12");
        pageList.add("/contests/not-a-contest");
        pageList.add("/contests/not-a-contest/2000-03-01");
        pageList.add("/contests/yorkshire-area/2000-12-40");
        pageList.add("/contests/yorkshire-area/2000-13-01");
        pageList.add("/contests/NOT-A-GROUP");
        pageList.add("/people/not-a-person");
        pageList.add("/people/not-a-person/whits");
        pageList.add("/people/not-a-person/pieces");
        pageList.add("/pieces/not-a-piece");
        pageList.add("/regions/not-a-region");
        pageList.add("/regions/not-a-region/contests");
        pageList.add("/tags/not-a-tag");
        pageList.add("/venues/not-a-venue");
        return pageList;
    }

    protected List<String> publicPages() {
        List<String> pageList = new ArrayList<>();
        pageList.add("/");
        pageList.add("/about-us");
        pageList.add("/acc/sign-up");
        pageList.add("/acc/register");
        pageList.add("/acc/sign-up-confirm");
        pageList.add("/acc/forgotten-password");
        pageList.add("/acc/forgotten-password/sent");
        pageList.add("/acc/forgotten-password/changed");
        pageList.add("/bands");
        pageList.add("/bands/R");
        pageList.add("/bands/0");
        pageList.add("/bands/rothwell-temperance");
        pageList.add("/bands/rothwell-temperance/whits");
        pageList.add("/bands/rothwell-temperance/embed");
        pageList.add("/bands/rothwell-temperance/embed/non_whit");
        pageList.add("/bands/rothwell-temperance/embed/whit");
        pageList.add("/calendar");
        pageList.add("/calendar/2000/12");
        pageList.add("/contests");
        pageList.add("/contests/Y");
        pageList.add("/contests/yorkshire-area");
        pageList.add("/contests/yorkshire-area/2000-03-01");
        pageList.add("/contests/YORKSHIRE-GROUP");
        pageList.add("/contests/YORKSHIRE-GROUP/2000");
        pageList.add("/contest-groups");
        pageList.add("/contest-groups/Y");
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
        pageList.add("/pieces/contest-music");
        pageList.add("/privacy");
        pageList.add("/regions");
        pageList.add("/regions/yorkshire");
        pageList.add("/regions/yorkshire/contests");
        pageList.add("/regions/yorkshire/championship/bands.json");
        pageList.add("/search?q=rothwell");
        pageList.add("/statistics");
        pageList.add("/tags");
        pageList.add("/tags/Y");
        pageList.add("/tags/yorkshire");
        pageList.add("/users/owner");
        pageList.add("/venues");
        pageList.add("/venues/S");
        pageList.add("/venues/0");
        pageList.add("/venues/st-george-s-hall");
        return pageList;
    }

    protected List<String> memberPages() {
        List<String> pageList = new ArrayList<>();
        pageList.add("/acc/change-password");
        pageList.add("/add-results");
        pageList.add("/bands/ALL");
        pageList.add("/bands/MAP");
        pageList.add("/bands/rothwell-temperance/edit");
        pageList.add("/bands/rothwell-temperance/edit-aliases");
        pageList.add("/bands/rothwell-temperance/edit-aliases/1/show");
        pageList.add("/bands/rothwell-temperance/edit-aliases/1/hide");
        pageList.add("/bands/rothwell-temperance/edit-aliases/1/edit-dates");
        pageList.add("/bands/rothwell-temperance/edit-aliases/1/delete");
        pageList.add("/bands/rothwell-temperance/edit-rehearsals");
        pageList.add("/bands/rothwell-temperance/edit-relationships");
        pageList.add("/bands/rothwell-temperance/edit-relationships/1/delete");
        pageList.add("/contest-groups/ALL");
        pageList.add("/contests/ALL");
        pageList.add("/contests/YORKSHIRE-GROUP/years");
        pageList.add("/contests/yorkshire-area/2000-03-01/edit-set-tests");
        pageList.add("/contests/yorkshire-area/2000-03-01/result/1/edit-pieces");
        pageList.add("/create/band");
        pageList.add("/create/group");
        pageList.add("/create/piece");
        pageList.add("/create/person");
        pageList.add("/create/tag");
        pageList.add("/create/venue");
        pageList.add("/lookup/band/data.json?s=abc");
        pageList.add("/lookup/contest/data.json?s=abc");
        pageList.add("/lookup/person/data.json?s=abc");
        pageList.add("/pieces/ALL");
        pageList.add("/pieces/contest-music/edit-aliases");
        pageList.add("/pieces/contest-music/edit-aliases/1/hide");
        pageList.add("/pieces/contest-music/edit-aliases/1/show");
        pageList.add("/pieces/contest-music/edit-aliases/1/delete");
        pageList.add("/profile");
        pageList.add("/profile/performances");
        pageList.add("/people/david-roberts/edit");
        pageList.add("/people/david-roberts/edit-aliases");
        pageList.add("/people/david-roberts/edit-aliases/1/hide");
        pageList.add("/people/david-roberts/edit-aliases/1/show");
        pageList.add("/people/david-roberts/edit-aliases/1/delete");
        pageList.add("/people/david-roberts/edit-relationships");
        pageList.add("/people/david-roberts/edit-relationships/1/delete");
        pageList.add("/tags/ALL");
        pageList.add("/tags/tag-to-delete/delete");
        pageList.add("/venues/ALL");
        pageList.add("/venues/st-george-s-hall/edit");
        pageList.add("/venues/st-george-s-hall/edit-aliases");
        pageList.add("/venues/st-george-s-hall/edit-aliases/1/delete");
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
        pageList.add("/contests/yorkshire-area/streaks");
        pageList.add("/contests/yorkshire-area/2000-03-01/competitors");
        pageList.add("/contests/yorkshire-area/2000-03-01/form-guide-bands");
        pageList.add("/contests/yorkshire-area/2000-03-01/form-guide-conductors");
        pageList.add("/contest-event-date/2000-03-01");
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
        pageList.add("/profile/people-profiles");
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
        pageList.add("/people/UNUSED");
        pageList.add("/tags/UNUSED");
        pageList.add("/venues/UNUSED");
        return pageList;
    }

    protected List<String> adminPages() {
        List<String> pageList = new ArrayList<>();
        pageList.add("/feedback/owner");
        pageList.add("/feedback/inconclusive");
        pageList.add("/feedback/spam");
        pageList.add("/people-profiles");
        pageList.add("/user-list");
        pageList.add("/user-list/pro");
        pageList.add("/user-list/superuser");
        pageList.add("/user-list/admin");
        pageList.add("/user-list/unactivated");
        return pageList;
    }
}
