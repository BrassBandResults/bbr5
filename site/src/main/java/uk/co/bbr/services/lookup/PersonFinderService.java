package uk.co.bbr.services.lookup;

import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.people.dao.PersonDao;

import java.time.LocalDate;

public interface PersonFinderService {
    String findMatchByName(String personName, String bandSlug, LocalDate dateContext);
}
