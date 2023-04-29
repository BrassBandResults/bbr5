package uk.co.bbr.services.parse;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import uk.co.bbr.services.bands.BandService;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.bands.dao.BandPreviousNameDao;
import uk.co.bbr.services.parse.dto.ParseResultDto;
import uk.co.bbr.services.people.PersonService;
import uk.co.bbr.services.people.dao.PersonAliasDao;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.regions.RegionService;
import uk.co.bbr.services.regions.dao.RegionDao;
import uk.co.bbr.services.security.JwtService;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.ex.AuthenticationFailedException;
import uk.co.bbr.web.LoginMixin;
import uk.co.bbr.web.security.support.TestUser;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest(properties = { "spring.config.name=parse-tests-h2", "spring.datasource.url=jdbc:h2:mem:parse-tests-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE", "spring.jpa.database-platform=org.hibernate.dialect.SQLServerDialect"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ParseTests implements LoginMixin {

    @Autowired private SecurityService securityService;
    @Autowired private JwtService jwtService;
    @Autowired private ParseService parseService;
    @Autowired private BandService bandService;
    @Autowired private RegionService regionService;
    @Autowired private PersonService personService;

    @BeforeAll
    void setupData() throws AuthenticationFailedException {
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        RegionDao yorkshire = this.regionService.fetchBySlug("yorkshire").get();

        BandDao blackDyke = this.bandService.create("Black Dyke Band", yorkshire);
        BandDao rothwell = this.bandService.create("Rothwell Temperance B", yorkshire);
        BandDao wallaceArnold = this.bandService.create("Wallace Arnold (Rothwell) Band", yorkshire);
        BandDao rothwellOld = this.bandService.create("Rothwell Old", yorkshire);

        BandPreviousNameDao rothwellJunior = new BandPreviousNameDao();
        rothwellJunior.setOldName("Rothwell Temperance Junior Band");
        this.bandService.createPreviousName(rothwell, rothwellJunior);

        PersonDao robertChilds = this.personService.create("Childs", "Robert");
        PersonDao davidChilds = this.personService.create("Childs", "David");
        PersonDao davidRoberts = this.personService.create("Roberts", "David");
        PersonDao johnRoberts = this.personService.create("Roberts", "John");

        PersonAliasDao bobChilds = new PersonAliasDao();
        bobChilds.setOldName("Bob Childs");
        this.personService.createAlternativeName(robertChilds, bobChilds);

        logoutTestUser();
    }

    @Test
    void testParseContestResultLineWithGibberishFails() {
        // arrange
        String testEntry = "Gibberish";

        // act
        ParseResultDto parseResult = this.parseService.parseLine(testEntry);

        // assert
        assertFalse(parseResult.isSuccess());
   }
}
