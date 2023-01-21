package uk.co.bbr.services.migrate;

import org.jdom2.Element;

public interface GroupMigrationService {
    void migrate(Element rootNode);
}
