package uk.co.bbr.services.migrate;

import org.jdom2.Element;

public interface TagsMigrationService {
    void migrate(Element rootNode);
}
