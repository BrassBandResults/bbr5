package uk.co.bbr.services.bands;

import org.junit.jupiter.api.Test;
import uk.co.bbr.services.bands.types.BandStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BandStatusTests {

    @Test
    void testExtinctIsExtinct() {
        assertTrue(BandStatus.EXTINCT.isExtinct());
        assertFalse(BandStatus.EXTINCT.isNotExtinct());
    }

    @Test
    void testCompetingIsNotExtinct() {
        assertTrue(BandStatus.COMPETING.isNotExtinct());
        assertFalse(BandStatus.COMPETING.isExtinct());
    }

    @Test
    void testCreateFromDescription() {
        BandStatus competing = BandStatus.fromDescription("Competing");
        assertEquals(BandStatus.COMPETING, competing);

        BandStatus extinct = BandStatus.fromDescription("Extinct");
        assertEquals(BandStatus.EXTINCT, extinct);
    }

    @Test
    void testCreateFromCode() {
        BandStatus competing = BandStatus.fromCode(1);
        assertEquals(BandStatus.COMPETING, competing);

        BandStatus extinct = BandStatus.fromCode(0);
        assertEquals(BandStatus.EXTINCT, extinct);
    }

    @Test
    void testCode() {
        assertEquals(0, BandStatus.EXTINCT.getCode());
        assertEquals(1, BandStatus.COMPETING.getCode());
    }

    @Test
    void testTranslationKey() {
        assertEquals("status.extinct", BandStatus.EXTINCT.getTranslationKey());
        assertEquals("status.competing", BandStatus.COMPETING.getTranslationKey());
    }
}
