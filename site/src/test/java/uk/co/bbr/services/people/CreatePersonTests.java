package uk.co.bbr.services.people;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.framework.ValidationException;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.security.JwtService;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.ex.AuthenticationFailedException;
import uk.co.bbr.web.LoginMixin;
import uk.co.bbr.web.security.support.TestUser;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest(properties = { "spring.config.name=person-create-tests-h2", "spring.datasource.url=jdbc:h2:mem:person-create-tests-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CreatePersonTests implements LoginMixin {

    @Autowired private PeopleService peopleService;
    @Autowired private SecurityService securityService;
    @Autowired private JwtService jwtService;

    @Test
    void createPersonWorksSuccessfully() throws AuthenticationFailedException {
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

        // act
        PersonDao returnedPerson = this.peopleService.create(person);

        // assert
        assertEquals("Tim Sawyer III", returnedPerson.getName());
        assertEquals("Sawyer III, Tim", returnedPerson.getNameSurnameFirst());
        assertEquals("Tim", returnedPerson.getFirstNames());
        assertEquals("Sawyer", returnedPerson.getSurname());
        assertEquals("Notes", returnedPerson.getNotes());
        assertFalse(person.isDeceased());
        assertEquals("III", returnedPerson.getSuffix());
        assertEquals("123", returnedPerson.getOldId());
        assertEquals("Rothwell Temperance", returnedPerson.getKnownFor());

        logoutTestUser();
    }

    @Test
    void testCreatedPersonCanBeFetchedByIdSuccessfully() throws AuthenticationFailedException {
        // arrange
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        PersonDao person = new PersonDao();
        person.setNotes("My Notes ");
        person.setDeceased(false);
        person.setKnownFor(" Black Dyke ");
        person.setSuffix(" I ");
        person.setSurname(" Childs ");
        person.setFirstNames("    ");
        person.setOldId("432 ");
        PersonDao savedPerson = this.peopleService.create(person);

        // act
        PersonDao returnedPerson = this.peopleService.fetchById(savedPerson.getId());

        // assert
        assertEquals("Childs I", returnedPerson.getName());
        assertEquals("Childs I", returnedPerson.getNameSurnameFirst());
        assertEquals("", returnedPerson.getFirstNames());
        assertEquals("Childs", returnedPerson.getSurname());
        assertEquals("My Notes", returnedPerson.getNotes());
        assertFalse(person.isDeceased());
        assertEquals("I", returnedPerson.getSuffix());
        assertEquals("432", returnedPerson.getOldId());
        assertEquals("Black Dyke", returnedPerson.getKnownFor());

        logoutTestUser();
    }

    @Test
    void testCreatedPersonCanBeFetchedBySlugSuccessfully() throws AuthenticationFailedException {
        // arrange
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        PersonDao person = new PersonDao();
        person.setNotes(" No Notes ");
        person.setDeceased(true);
        person.setKnownFor("Carlton Main ");
        person.setSuffix(" ");
        person.setSurname(" Simpson ");
        person.setFirstNames(" Bart ");
        person.setOldId("111");
        PersonDao savedPerson = this.peopleService.create(person);

        // act
        PersonDao returnedPerson = this.peopleService.fetchBySlug(savedPerson.getSlug());

        // assert
        assertEquals("Bart Simpson", returnedPerson.getName());
        assertEquals("Simpson, Bart", returnedPerson.getNameSurnameFirst());
        assertEquals("Bart", returnedPerson.getFirstNames());
        assertEquals("Simpson", returnedPerson.getSurname());
        assertEquals("No Notes", returnedPerson.getNotes());
        assertTrue(person.isDeceased());
        assertEquals("", returnedPerson.getSuffix());
        assertEquals("111", returnedPerson.getOldId());
        assertEquals("Carlton Main", returnedPerson.getKnownFor());

        logoutTestUser();
    }

    @Test
    void testCreatePersonFromJustNameWorksSuccessfully() throws AuthenticationFailedException {
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        // act
        PersonDao returnedPerson = this.peopleService.create("Childs", "David");

        // assert
        assertEquals("David Childs", returnedPerson.getName());
        assertEquals("Childs, David", returnedPerson.getNameSurnameFirst());
        assertEquals("David", returnedPerson.getFirstNames());
        assertEquals("Childs", returnedPerson.getSurname());
        assertNull(returnedPerson.getNotes());
        assertFalse(returnedPerson.isDeceased());
        assertNull(returnedPerson.getSuffix());
        assertNull(returnedPerson.getOldId());
        assertNull(returnedPerson.getKnownFor());

        logoutTestUser();
    }

    @Test
    void testCreatingPersonWithDuplicateSlugFails() throws AuthenticationFailedException {
        // arrange
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        PersonDao person = this.peopleService.create(" PERSON  ", " FIRST ");

        // act
        ValidationException ex = assertThrows(ValidationException.class, ()-> {this.peopleService.create("Person", "First");});

        // assert
        assertEquals("Person with slug first-person already exists.", ex.getMessage());

        logoutTestUser();
    }
}
