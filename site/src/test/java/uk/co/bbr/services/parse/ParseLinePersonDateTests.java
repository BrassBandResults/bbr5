package uk.co.bbr.services.parse;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import uk.co.bbr.services.bands.BandAliasService;
import uk.co.bbr.services.bands.BandService;
import uk.co.bbr.services.bands.dao.BandAliasDao;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.people.PersonAliasService;
import uk.co.bbr.services.people.PersonService;
import uk.co.bbr.services.people.dao.PersonAliasDao;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.regions.RegionService;
import uk.co.bbr.services.regions.dao.RegionDao;
import uk.co.bbr.services.results.ParseResultService;
import uk.co.bbr.services.results.dto.ParseResultDto;
import uk.co.bbr.services.results.types.ParseOutcome;
import uk.co.bbr.services.security.JwtService;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.ex.AuthenticationFailedException;
import uk.co.bbr.web.LoginMixin;
import uk.co.bbr.web.security.support.TestUser;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest(properties = {  "spring.config.location=classpath:test-application.yml",
        "spring.datasource.url=jdbc:h2:mem:parse-line-services-tests-admin-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ParseLinePersonDateTests implements LoginMixin {

    @Autowired private SecurityService securityService;
    @Autowired private JwtService jwtService;
    @Autowired private ParseResultService parseResultService;
    @Autowired private BandService bandService;
    @Autowired private BandAliasService bandAliasService;
    @Autowired private RegionService regionService;
    @Autowired private PersonService personService;
    @Autowired private PersonAliasService personAliasService;

    @BeforeAll
    void setupData() throws AuthenticationFailedException {
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        RegionDao yorkshire = this.regionService.fetchBySlug("yorkshire").get();

        BandDao blackDyke = this.bandService.create("Black Dyke Band", yorkshire);
        BandDao rothwell = this.bandService.create("Rothwell Temperance B", yorkshire);
        BandDao wallaceArnold = this.bandService.create("Wallace Arnold (Rothwell) Band", yorkshire);
        BandDao rothwellOld = this.bandService.create("Rothwell Old", yorkshire);
        BandDao suffolkVolunteer = this.bandService.create("11th Suffolk Volunteer Rifles (Sudbury)", yorkshire);
        BandDao briggus = this.bandService.create("Brighouse & Rastrick", yorkshire);

        BandAliasDao rothwellJunior = new BandAliasDao();
        rothwellJunior.setOldName("Rothwell Temperance Junior Band");
        this.bandAliasService.createAlias(rothwell, rothwellJunior);

        PersonDao robertChilds = this.personService.create("Childs", "Robert");
        robertChilds.setStartDate(LocalDate.of(1950, 1, 1));
        robertChilds = this.personService.update(robertChilds);

        PersonDao davidChilds = this.personService.create("Childs", "David");
        davidChilds.setStartDate(LocalDate.of(1980, 2, 5));
        this.personService.update(davidChilds);

        PersonDao derekChilds = this.personService.create("Childs", "Derek");
        derekChilds.setStartDate(LocalDate.of(1880, 2, 5));
        derekChilds.setEndDate(LocalDate.of(1937, 2, 5));
        this.personService.update(derekChilds);

        PersonDao davidRoberts = this.personService.create("Roberts", "David");
        davidRoberts.setStartDate(LocalDate.of(1970, 6, 10));
        this.personService.update(davidRoberts);

        PersonDao johnRoberts = this.personService.create("Roberts", "John");
        johnRoberts.setStartDate(LocalDate.of(1965, 12, 25));
        this.personService.update(johnRoberts);

        PersonDao theoQWhigley = this.personService.create("Whigley", "Theo Q.");
        theoQWhigley.setStartDate(LocalDate.of(1886, 1, 25));
        theoQWhigley.setEndDate(LocalDate.of(1980, 2, 2));
        this.personService.update(theoQWhigley);

        PersonDao theoQPWhigley = this.personService.create("Whigley", "Theo Q. P.");
        theoQPWhigley.setStartDate(LocalDate.of(1885, 1, 25));
        theoQPWhigley.setEndDate(LocalDate.of(1960, 2, 2));
        this.personService.update(theoQPWhigley);

        PersonAliasDao bobChilds = new PersonAliasDao();
        bobChilds.setOldName("Bob Childs");
        this.personAliasService.createAlias(robertChilds, bobChilds);

        logoutTestUser();
    }

    @Test
    void testFindingWithInitialAndDateWorksSuccessfullyWhenInDateRange() {
        String line = "1. Rothwell Temperance B, D. Childs, 5";
        ParseResultDto parseResult = this.parseResultService.parseLine(line, LocalDate.of(1900, 1, 1));

        // assert
        assertEquals("1", parseResult.getRawPosition());
        assertEquals("Rothwell Temperance B", parseResult.getRawBandName());
        assertEquals("D. Childs", parseResult.getRawConductorName());
        assertEquals(5, parseResult.getRawDraw());
        assertEquals("", parseResult.getRawPoints());

        assertEquals(ParseOutcome.GREEN_MATCHES_FOUND_IN_DATABASE, parseResult.getOutcome());

        assertEquals("rothwell-temperance-b", parseResult.getMatchedBand().getSlug());
        assertEquals("derek-childs", parseResult.getMatchedConductor().getSlug());
        assertEquals("Childs", parseResult.getMatchedConductor().getSurname());
        assertEquals("Derek", parseResult.getMatchedConductor().getFirstNames());

        assertNotNull(parseResult.buildContestResult(null));
    }

    @Test
    void testFindingWithInitialAndDateWorksSuccessfullyWhenInDateStartRange() {
        String line = "1. Rothwell Temperance B, D. Childs, 5";
        ParseResultDto parseResult = this.parseResultService.parseLine(line, LocalDate.of(1982, 1, 1));

        // assert
        assertEquals("1", parseResult.getRawPosition());
        assertEquals("Rothwell Temperance B", parseResult.getRawBandName());
        assertEquals("D. Childs", parseResult.getRawConductorName());
        assertEquals(5, parseResult.getRawDraw());
        assertEquals("", parseResult.getRawPoints());

        assertEquals(ParseOutcome.GREEN_MATCHES_FOUND_IN_DATABASE, parseResult.getOutcome());

        assertEquals("rothwell-temperance-b", parseResult.getMatchedBand().getSlug());
        assertEquals("david-childs", parseResult.getMatchedConductor().getSlug());
        assertEquals("Childs", parseResult.getMatchedConductor().getSurname());
        assertEquals("David", parseResult.getMatchedConductor().getFirstNames());

        assertNotNull(parseResult.buildContestResult(null));
    }

}
