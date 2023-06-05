package uk.co.bbr.services.migrate;

import lombok.RequiredArgsConstructor;
import org.jdom2.Element;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.tags.ContestTagService;
import uk.co.bbr.services.tags.dao.ContestTagDao;
import uk.co.bbr.services.framework.annotations.IgnoreCoverage;
import uk.co.bbr.services.framework.mixins.SlugTools;
import uk.co.bbr.services.security.SecurityService;

@Service
@RequiredArgsConstructor
@IgnoreCoverage
public class TagsMigrationServiceImpl extends AbstractMigrationServiceImpl implements TagsMigrationService, SlugTools {

    private final SecurityService securityService;
    private final ContestTagService contestTagService;

    @Override
    public void migrate(Element rootNode) {
        ContestTagDao contestTag = new ContestTagDao();
        contestTag.setOldId(rootNode.getAttributeValue("id"));
        contestTag.setSlug(rootNode.getChildText("slug"));
        contestTag.setName(rootNode.getChildText("name"));

        contestTag.setCreatedBy(this.createUser(this.notBlank(rootNode, "owner"), this.securityService));
        contestTag.setUpdatedBy(this.createUser(this.notBlank(rootNode, "lastChangedBy"), this.securityService));

        contestTag.setCreated(this.notBlankDateTime(rootNode, "created"));
        contestTag.setUpdated(this.notBlankDateTime(rootNode, "lastModified"));

        // notes
        contestTag = this.contestTagService.migrate(contestTag);

        System.out.println(contestTag.getName());
    }
}
