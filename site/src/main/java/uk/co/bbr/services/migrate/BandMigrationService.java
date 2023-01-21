package uk.co.bbr.services.migrate;

import org.jdom2.Element;

public interface BandMigrationService {
    void migrate(Element rootNode);
}
