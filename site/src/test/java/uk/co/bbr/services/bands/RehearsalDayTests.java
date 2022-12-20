package uk.co.bbr.services.bands;

import org.junit.jupiter.api.Test;
import uk.co.bbr.services.bands.types.RehearsalDay;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RehearsalDayTests {

    @Test
    void testRehearsalDayCode() {
       assertEquals(0, RehearsalDay.SUNDAY.getCode());
       assertEquals(3, RehearsalDay.WEDNESDAY.getCode());
   }

    @Test
    void testFromCode() {
       assertEquals(RehearsalDay.MONDAY, RehearsalDay.fromCode(1));
       assertEquals(RehearsalDay.WEDNESDAY, RehearsalDay.fromCode(3));
   }

    @Test
    void testFromName() {
       assertEquals(RehearsalDay.MONDAY, RehearsalDay.fromName("Monday"));
       assertEquals(RehearsalDay.WEDNESDAY, RehearsalDay.fromName("Wednesday"));
   }

   @Test
   void testTranslationKey() {
        assertEquals("day.monday", RehearsalDay.MONDAY.getTranslationKey());
        assertEquals("day.wednesday", RehearsalDay.WEDNESDAY.getTranslationKey());
   }
}
