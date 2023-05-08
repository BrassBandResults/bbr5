package uk.co.bbr.services.migrate;

import org.jdom2.Element;

public interface VenueMigrationService {
    void migrate(Element rootNode, int pass);
}
