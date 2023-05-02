package uk.co.bbr.services.migrate;

import lombok.RequiredArgsConstructor;
import org.jdom2.Element;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.framework.annotations.IgnoreCoverage;
import uk.co.bbr.services.framework.mixins.SlugTools;
import uk.co.bbr.services.people.PersonService;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.pieces.PieceService;
import uk.co.bbr.services.pieces.dao.PieceAliasDao;
import uk.co.bbr.services.pieces.dao.PieceDao;
import uk.co.bbr.services.security.SecurityService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@IgnoreCoverage
public class PieceMigrationServiceImpl extends AbstractMigrationServiceImpl implements PieceMigrationService, SlugTools {


    private final PieceService pieceService;
    private final SecurityService securityService;
    private final PersonService personService;

    @Override
    public void migrate(Element rootNode) {
        PieceDao newPiece = new PieceDao();
        newPiece.setOldId(rootNode.getAttributeValue("id"));
        newPiece.setSlug(rootNode.getChildText("slug"));
        newPiece.setName(rootNode.getChildText("name"));
        newPiece.setNotes(this.notBlank(rootNode, "notes"));
        newPiece.setYear(this.notBlank(rootNode, "year"));
        if (newPiece.getYear() != null && newPiece.getYear().length() > 4) {
            System.out.println("****" + newPiece.getYear());
            newPiece.setYear(newPiece.getYear().substring(0,4));
        }

        if (rootNode.getChildText("composer") != null) {
            Optional<PersonDao> composer = this.personService.fetchBySlug(rootNode.getChild("composer").getAttributeValue("slug"));
            if (composer.isEmpty()) {
                throw new UnsupportedOperationException("Composer not found");
            }
            newPiece.setComposer(composer.get());
        }

        if (rootNode.getChildText("arranger") != null) {
            Optional<PersonDao> arranger = this.personService.fetchBySlug(rootNode.getChild("arranger").getAttributeValue("slug"));
            if (arranger.isEmpty()) {
                throw new UnsupportedOperationException("Arranger not found");
            }
            newPiece.setArranger(arranger.get());
        }

        newPiece.setCreatedBy(this.createUser(this.notBlank(rootNode, "owner"), this.securityService));
        newPiece.setUpdatedBy(this.createUser(this.notBlank(rootNode, "lastChangedBy"), this.securityService));

        newPiece.setCreated(this.notBlankDateTime(rootNode, "created"));
        newPiece.setUpdated(this.notBlankDateTime(rootNode, "lastModified"));

        // notes
        newPiece = this.pieceService.migrate(newPiece);

        Element previousNames = rootNode.getChild("previous_names");
        List<Element> previousNameNodes = previousNames.getChildren();
        for (Element eachOldName : previousNameNodes) {
            this.createPreviousName(newPiece, eachOldName);
        }

        System.out.println(newPiece.getName());
    }

    private void createPreviousName(PieceDao piece, Element oldNameElement) {
        PieceAliasDao previousName = new PieceAliasDao();
        previousName.setCreatedBy(this.createUser(this.notBlank(oldNameElement, "owner"), this.securityService));
        previousName.setUpdatedBy(this.createUser(this.notBlank(oldNameElement, "lastChangedBy"), this.securityService));
        previousName.setCreated(this.notBlankDateTime(oldNameElement, "created"));
        previousName.setUpdated(this.notBlankDateTime(oldNameElement, "lastModified"));
        previousName.setName(oldNameElement.getChildText("name"));
        previousName.setHidden(false);

        this.pieceService.migrateAlternativeName(piece, previousName);
    }
}
