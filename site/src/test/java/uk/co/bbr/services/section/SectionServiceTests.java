package uk.co.bbr.services.section;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import uk.co.bbr.services.section.dao.SectionDao;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.dao.BbrUserDao;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest(properties = { "spring.config.name=section-tests-h2", "spring.datasource.url=jdbc:h2:mem:section-tests-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"})
public class SectionServiceTests {

    @Autowired
    private SectionService sectionService;

    @Test
    void testFetchByNameWorksSuccessfully() {
        // act
        SectionDao section = this.sectionService.fetchByName("Championship");

        // assert
        assertEquals("Championship", section.getName());
        assertEquals("championship", section.getSlug());
        assertEquals(30, section.getPosition());
        assertEquals("C", section.getMapShortCode());
        assertEquals("section.championship", section.getTranslationKey());
    }

    @Test
    void testFetchBySlugWorksSuccessfully() {
        // act
        SectionDao section = this.sectionService.fetchBySlug("first");

        // assert
        assertEquals("First", section.getName());
        assertEquals("first", section.getSlug());
        assertEquals(110, section.getPosition());
        assertEquals("1", section.getMapShortCode());
        assertEquals("section.first", section.getTranslationKey());
    }
}
