package uk.co.bbr.services.bands;

import uk.co.bbr.services.bands.dao.BandDao;

import java.time.LocalDate;

public interface BandFinderService {

    String findMatchByName(String bandName, LocalDate dateContext);
}
