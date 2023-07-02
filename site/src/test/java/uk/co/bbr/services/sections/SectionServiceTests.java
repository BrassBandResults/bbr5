package uk.co.bbr.services.sections;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import uk.co.bbr.services.sections.dao.SectionDao;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest(properties = {  "spring.config.location=classpath:test-application.yml",
        "spring.datasource.url=jdbc:h2:mem:sections-section-services-tests-admin-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"})
class SectionServiceTests {

    @Autowired
    private SectionService sectionService;

    @Test
    void testFetchByNameWorksSuccessfully() {
        // act
        Optional<SectionDao> sectionOptional = this.sectionService.fetchByName("Championship");

        // assert
        assertTrue(sectionOptional.isPresent());
        assertFalse(sectionOptional.isEmpty());

        SectionDao section = sectionOptional.get();

        assertEquals("Championship", section.getName());
        assertEquals("championship", section.getSlug());
        assertEquals(30, section.getPosition());
        assertEquals("C", section.getMapShortCode());
        assertEquals("section.championship", section.getTranslationKey());
    }

    @Test
    void testFetchBySlugWorksSuccessfully() {
        // act
        Optional<SectionDao> sectionOptional = this.sectionService.fetchBySlug("first");

        // assert
        assertTrue(sectionOptional.isPresent());
        assertFalse(sectionOptional.isEmpty());

        SectionDao section = sectionOptional.get();

        assertEquals("First", section.getName());
        assertEquals("first", section.getSlug());
        assertEquals(110, section.getPosition());
        assertEquals("1", section.getMapShortCode());
        assertEquals("section.first", section.getTranslationKey());
    }
}
