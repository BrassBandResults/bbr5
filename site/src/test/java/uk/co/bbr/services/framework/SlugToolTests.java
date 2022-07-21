package uk.co.bbr.services.framework;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SlugToolTests implements SlugTools {

    @Test
    void testSlugifyLowerCasesSuccessfully() {
        assertEquals("mixed1", slugify("MiXeD1"));
        assertEquals("upper2", slugify("UPPER2"));
        assertEquals("lower3", slugify("lower3"));
    }

    @Test
    void testSlugifyReplacesSpacesWithHyphensSuccessfully() {
        assertEquals("mixed-abc-123", slugify("MiXeD Abc 123"));
        assertEquals("upper-name-two-345", slugify("UPPER NAME TWO 345"));
        assertEquals("lower-this-name-456", slugify("lower  this name 456"));
        assertEquals("spaces-at-end-789", slugify("SPACES at END 789  "));
    }

    @Test
    void testSlugifyConvertsOddCharactersToHypensSuccessfully() {
        assertEquals("abc-5-8", slugify("ABC';^&^&£5 8"));
    }
}
