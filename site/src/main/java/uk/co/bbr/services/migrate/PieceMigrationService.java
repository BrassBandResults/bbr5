package uk.co.bbr.services.migrate;

import org.jdom2.Element;

public interface PieceMigrationService {
    void migrate(Element rootNode);
}
