package uk.co.bbr.services.tags.sql;

import lombok.experimental.UtilityClass;
import uk.co.bbr.services.framework.sql.SqlExec;
import uk.co.bbr.services.tags.sql.dto.ContestTagSqlDto;
import uk.co.bbr.services.tags.sql.dto.TagListSqlDto;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Set;

@UtilityClass
public class ContestTagSql {

    private static final String CONTEST_TAGS_SQL = """
            SELECT c.slug as contest_slug, t.slug as tag_slug, t.name as tag_name
            FROM contest_tag_link tl
            INNER JOIN contest c ON tl.contest_id = c.id
            INNER JOIN contest_tag t ON tl.contest_tag_id = t.id
            WHERE c.slug IN (?1)""";

    public static List<ContestTagSqlDto> selectTagsForContestSlugs(EntityManager entityManager, Set<String> contestSlugs) {
        return SqlExec.execute(entityManager, CONTEST_TAGS_SQL, contestSlugs, ContestTagSqlDto.class);
    }

    private static final String GROUP_TAGS_SQL = """
            SELECT g.slug as group_slug, t.slug as tag_slug, t.name as tag_name,
            FROM contest_group_tag_link tl
            INNER JOIN contest_group g ON tl.contest_group_id = g.id
            INNER JOIN contest_tag t ON tl.contest_tag_id = t.id
            WHERE UPPER(g.slug) IN (?1)""";

    public static List<ContestTagSqlDto> selectTagsForGroupSlugs(EntityManager entityManager, Set<String> groupSlugs) {
        return SqlExec.execute(entityManager, GROUP_TAGS_SQL, groupSlugs, ContestTagSqlDto.class);
    }

    private static final String ALL_TAGS_SQL = """
            SELECT t.slug, t.name,
            (SELECT count(*) FROM contest_tag_link ct WHERE ct.contest_tag_id = t.id) as contest_count,
            (SELECT count(*) FROM contest_group_tag_link gt WHERE gt.contest_tag_id = t.id) as group_count
            FROM contest_tag t
            ORDER BY t.name
            """;

    public static List<TagListSqlDto> selectAllTagsWithCounts(EntityManager entityManager) {
        return SqlExec.execute(entityManager, ALL_TAGS_SQL, TagListSqlDto.class);
    }

    private static final String TAGS_FOR_PREFIX_SQL = """
            SELECT t.slug, t.name,
            (SELECT count(*) FROM contest_tag_link ct WHERE ct.contest_tag_id = t.id) as contest_count,
            (SELECT count(*) FROM contest_group_tag_link gt WHERE gt.contest_tag_id = t.id) as group_count
            FROM contest_tag t
            WHERE UPPER(t.name) LIKE ?1
            ORDER BY t.name
            """;

    public static List<TagListSqlDto> selectTagsForPrefixWithCount(EntityManager entityManager, String prefix) {
        return SqlExec.execute(entityManager, TAGS_FOR_PREFIX_SQL, prefix + "%", TagListSqlDto.class);
    }
}
