package uk.co.bbr.services.migrate;

import org.jdom2.Element;

public interface EventMigrationService {
    void migrate(Element rootNode);
}