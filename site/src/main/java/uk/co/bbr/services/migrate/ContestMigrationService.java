package uk.co.bbr.services.migrate;

import org.jdom2.Element;

public interface ContestMigrationService {
    void migrate(Element rootNode);
}
