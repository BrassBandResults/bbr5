package uk.co.bbr.services.people;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import uk.co.bbr.services.people.dao.PersonAlternativeNameDao;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.web.LoginMixin;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest(properties = { "spring.config.name=person-alternative-tests-h2", "spring.datasource.url=jdbc:h2:mem:person-alternative-tests-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AlternativeNamePersonTests implements LoginMixin {

    @Autowired private PeopleService peopleService;

    @Test
    void createAlternativeNameWorksSuccessfully() {
        // arrange
        PersonDao person = new PersonDao();
        person.setNotes(" Notes ");
        person.setDeceased(false);
        person.setKnownFor(" Rothwell Temperance ");
        person.setSuffix(" III ");
        person.setSurname(" Sawyer ");
        person.setFirstNames(" Tim ");
        person.setOldId(" 123 ");
        PersonDao returnedPerson = this.peopleService.create(person);

        PersonAlternativeNameDao altName = new PersonAlternativeNameDao();
        altName.setOldName("Timothy Sawyer");

        // act
        this.peopleService.createAlternativeName(person, altName);

        // assert
        List<PersonAlternativeNameDao> altNames = this.peopleService.fetchAlternateNames(person);
        assertEquals(1, altNames.size());
        assertEquals("Timothy Sawyer", altNames.get(0).getOldName());
        assertEquals(person.getName(), altNames.get(0).getPerson().getName());
    }
}
