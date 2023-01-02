package uk.co.bbr.services.people;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import uk.co.bbr.services.people.dao.PersonAliasDao;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.security.JwtService;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.ex.AuthenticationFailedException;
import uk.co.bbr.web.LoginMixin;
import uk.co.bbr.web.security.support.TestUser;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest(properties = { "spring.config.name=person-alternative-tests-h2", "spring.datasource.url=jdbc:h2:mem:person-alternative-tests-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CreatePersonAliasTests implements LoginMixin {

    @Autowired private PersonService personService;
    @Autowired private SecurityService securityService;
    @Autowired private JwtService jwtService;

    @Test
    void createAlternativeNameWorksSuccessfully() throws AuthenticationFailedException {
        // arrange
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        PersonDao person = new PersonDao();
        person.setNotes(" Notes ");
        person.setDeceased(false);
        person.setKnownFor(" Rothwell Temperance ");
        person.setSuffix(" III ");
        person.setSurname(" Sawyer ");
        person.setFirstNames(" Tim ");
        person.setOldId(" 123 ");
        PersonDao returnedPerson = this.personService.create(person);

        PersonAliasDao altName = new PersonAliasDao();
        altName.setOldName("Timothy Sawyer");

        // act
        this.personService.createAlternativeName(person, altName);

        // assert
        List<PersonAliasDao> altNames = this.personService.fetchAlternateNames(person);
        assertEquals(1, altNames.size());
        assertEquals("Timothy Sawyer", altNames.get(0).getOldName());
        assertEquals(person.getName(), altNames.get(0).getPerson().getName());

        logoutTestUser();
    }
}
