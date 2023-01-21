package uk.co.bbr.services.migrate;

import lombok.RequiredArgsConstructor;
import org.jdom2.Element;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.contests.ContestGroupService;
import uk.co.bbr.services.contests.ContestTagService;
import uk.co.bbr.services.contests.dao.ContestGroupAliasDao;
import uk.co.bbr.services.contests.dao.ContestGroupDao;
import uk.co.bbr.services.contests.dao.ContestTagDao;
import uk.co.bbr.services.contests.types.ContestGroupType;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.services.framework.mixins.SlugTools;
import uk.co.bbr.services.security.SecurityService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EventMigrationServiceImpl extends AbstractMigrationServiceImpl implements EventMigrationService, SlugTools {


    @Override
    public void migrate(Element rootNode) {
        System.out.println(rootNode.getChildText("name"));
    }
}
