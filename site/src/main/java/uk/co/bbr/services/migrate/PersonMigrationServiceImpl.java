package uk.co.bbr.services.migrate;

import lombok.RequiredArgsConstructor;
import org.jdom2.Element;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.framework.annotations.IgnoreCoverage;
import uk.co.bbr.services.framework.mixins.SlugTools;
import uk.co.bbr.services.people.PersonService;
import uk.co.bbr.services.people.dao.PersonAliasDao;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.security.SecurityService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@IgnoreCoverage
public class PersonMigrationServiceImpl extends AbstractMigrationServiceImpl implements PersonMigrationService, SlugTools {

    private final PersonService personService;
    private final SecurityService securityService;

    @Override
    public void migrate(Element rootNode) {
        PersonDao newPerson = new PersonDao();
        newPerson.setOldId(rootNode.getAttributeValue("id"));
        newPerson.setSlug(rootNode.getChildText("slug"));
        newPerson.setFirstNames(rootNode.getChildText("first_names"));
        newPerson.setSuffix(rootNode.getChildText("suffix"));
        newPerson.setSurname(rootNode.getChildText("surname"));
        newPerson.setKnownFor(this.notBlank(rootNode, "bandname"));
        newPerson.setNotes(this.notBlank(rootNode, "notes"));
        newPerson.setDeceased("True".equals(rootNode.getChildText("deceased")));
        newPerson.setStartDate(this.notBlankDate(rootNode, "start"));
        newPerson.setEndDate(this.notBlankDate(rootNode, "end"));

        newPerson.setCreatedBy(this.createUser(this.notBlank(rootNode, "owner"), this.securityService));
        newPerson.setUpdatedBy(this.createUser(this.notBlank(rootNode, "lastChangedBy"), this.securityService));

        newPerson.setCreated(this.notBlankDateTime(rootNode, "created"));
        newPerson.setUpdated(this.notBlankDateTime(rootNode, "lastModified"));

        newPerson = this.personService.migrate(newPerson);

        Element previousNames = rootNode.getChild("previous_names");
        List<Element> previousNameNodes = previousNames.getChildren();
        for (Element eachOldName : previousNameNodes) {
            this.createPreviousName(newPerson, eachOldName);
        }

        System.out.println(newPerson.getSlug());
    }

    private void createPreviousName(PersonDao person, Element oldNameElement) {
        String name = oldNameElement.getChildText("name");
        // does it already exist?

        Optional<PersonAliasDao> existingAlias = this.personService.aliasExists(person, name);
        if (existingAlias.isEmpty()) {

            PersonAliasDao previousName = new PersonAliasDao();
            previousName.setCreatedBy(this.createUser(this.notBlank(oldNameElement, "owner"), this.securityService));
            previousName.setUpdatedBy(this.createUser(this.notBlank(oldNameElement, "lastChangedBy"), this.securityService));
            previousName.setCreated(this.notBlankDateTime(oldNameElement, "created"));
            previousName.setUpdated(this.notBlankDateTime(oldNameElement, "lastModified"));
            previousName.setOldName(name);
            previousName.setHidden(this.notBlankBoolean(oldNameElement, "hidden"));

            this.personService.migrateAlternativeName(person, previousName);
        }
    }
}
