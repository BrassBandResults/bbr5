package uk.co.bbr.services.people;

import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.people.dao.PersonDao;

import java.time.LocalDate;

public interface PersonFinderService {
    PersonDao findMatchByName(String personName, BandDao band, LocalDate dateContext);
}
