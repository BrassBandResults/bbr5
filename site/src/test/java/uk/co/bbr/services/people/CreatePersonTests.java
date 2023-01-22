package uk.co.bbr.services.people;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import uk.co.bbr.services.framework.ValidationException;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.security.JwtService;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.ex.AuthenticationFailedException;
import uk.co.bbr.web.LoginMixin;
import uk.co.bbr.web.security.support.TestUser;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest(properties = { "spring.config.name=person-create-tests-h2", "spring.datasource.url=jdbc:h2:mem:person-create-tests-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CreatePersonTests implements LoginMixin {

    @Autowired private PersonService personService;
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
        PersonDao returnedPerson = this.personService.create(person);

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
        PersonDao savedPerson = this.personService.create(person);

        // act
        Optional<PersonDao> returnedPerson = this.personService.fetchById(savedPerson.getId());

        // assert
        assertTrue(returnedPerson.isPresent());
        assertFalse(returnedPerson.isEmpty());
        assertEquals("Childs I", returnedPerson.get().getName());
        assertEquals("Childs I", returnedPerson.get().getNameSurnameFirst());
        assertEquals("", returnedPerson.get().getFirstNames());
        assertEquals("Childs", returnedPerson.get().getSurname());
        assertEquals("My Notes", returnedPerson.get().getNotes());
        assertFalse(returnedPerson.get().isDeceased());
        assertEquals("I", returnedPerson.get().getSuffix());
        assertEquals("432", returnedPerson.get().getOldId());
        assertEquals("Black Dyke", returnedPerson.get().getKnownFor());

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
        PersonDao savedPerson = this.personService.create(person);

        // act
        Optional<PersonDao> returnedPerson = this.personService.fetchBySlug(savedPerson.getSlug());

        // assert
        assertTrue(returnedPerson.isPresent());
        assertFalse(returnedPerson.isEmpty());
        assertEquals("Bart Simpson", returnedPerson.get().getName());
        assertEquals("Simpson, Bart", returnedPerson.get().getNameSurnameFirst());
        assertEquals("Bart", returnedPerson.get().getFirstNames());
        assertEquals("Simpson", returnedPerson.get().getSurname());
        assertEquals("No Notes", returnedPerson.get().getNotes());
        assertTrue(returnedPerson.get().isDeceased());
        assertEquals("", returnedPerson.get().getSuffix());
        assertEquals("111", returnedPerson.get().getOldId());
        assertEquals("Carlton Main", returnedPerson.get().getKnownFor());

        logoutTestUser();
    }

    @Test
    void testCreatePersonFromJustNameWorksSuccessfully() throws AuthenticationFailedException {
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        // act
        PersonDao returnedPerson = this.personService.create("Childs", "David");

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

        PersonDao person = this.personService.create(" PERSON  ", " FIRST ");

        // act
        ValidationException ex = assertThrows(ValidationException.class, ()-> {this.personService.create("Person", "First");});

        // assert
        assertEquals("Person with slug first-person already exists.", ex.getMessage());

        logoutTestUser();
    }
}
