package uk.co.bbr.services.framework;

import org.junit.jupiter.api.Test;
import uk.co.bbr.services.framework.mixins.NameTools;
import uk.co.bbr.services.framework.mixins.SlugTools;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NameToolTests implements NameTools {

    @Test
    void testSimplifyPersonNameRemovesMultipleSpacesSuccessfully() {
        assertEquals("Bob Childs", simplifyPersonFullName("Bob     Childs"));
        assertEquals("David Q. T. P. Simpson", simplifyPersonFullName("David  Q.   T.     P.     Simpson"));
        assertEquals("P. I. Q. Brooks", simplifyPersonFullName("  P.      I.        Q.      Brooks"));
    }

    @Test
    void testSimplifyPersonNameTrimsEndOfInputSuccessfully() {
        assertEquals("Bob Childs", simplifyPersonFullName("   Bob   Childs  "));
        assertEquals("David Q. T. P. Simpson", simplifyPersonFullName(" David  Q.  T.    P.     Simpson   "));
        assertEquals("P. I. Q. Brooks", simplifyPersonFullName("    P.    I.     Q.    Brooks     "));
    }

    @Test
    void testSimplifyPersonNameAddsDotsAfterInitialsSuccessfully() {
        assertEquals("Bob T. Childs", simplifyPersonFullName("Bob T Childs  "));
        assertEquals("David Q. T. P. Simpson", simplifyPersonFullName("David Q T P Simpson"));
        assertEquals("P. I. Q. Brooks", simplifyPersonFullName("P I Q Brooks"));
    }

    @Test
    void testSimplifyPersonNameAddsDotsAfterInitialsDoesntWorkOnLowerCase() {
        assertEquals("Bob t Childs", simplifyPersonFullName("Bob t Childs  "));
        assertEquals("David q T. p Simpson", simplifyPersonFullName("David q T p Simpson"));
        assertEquals("p i Q. Brooks", simplifyPersonFullName("p i Q Brooks"));
    }

    @Test
    void testSimplifyPersonNameAddsDotsAfterSaintSuccessfully() {
        assertEquals("Bob T. St. Childs", simplifyPersonFullName("Bob T St Childs  "));
        assertEquals("St. David Q. T. P. Simpson", simplifyPersonFullName("St David Q T P Simpson"));
        assertEquals("St. Bathurst Town", simplifyPersonFullName("St Bathurst Town"));
        assertEquals("Bathgate West Lothian", simplifyPersonFullName("Bathgate West Lothian"));
    }


}
