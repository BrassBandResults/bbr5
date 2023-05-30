package uk.co.bbr.services.migrate.sql;

import lombok.experimental.UtilityClass;

import javax.persistence.EntityManager;
import javax.persistence.Query;

@UtilityClass
public class PersonMigrateSql {

    private static final String CLEAR_UP_UNKNOWN_USER_SQL = """
            UPDATE contest_result SET conductor_id = null WHERE conductor_id IN (SELECT id FROM person WHERE slug = 'unknown');
            UPDATE piece SET composer_id = null WHERE composer_id IN (SELECT id FROM person WHERE slug = 'unknown');
            UPDATE piece SET arranger_id = null WHERE arranger_id IN (SELECT id FROM person WHERE slug = 'unknown');
            DELETE FROM person_alias WHERE person_id IN (SELECT id FROM person WHERE slug = 'unknown');
            DELETE FROM person WHERE slug = 'unknown';""";

    public static void clearUnknownPerson(EntityManager entityManager) {
        String[] sql = CLEAR_UP_UNKNOWN_USER_SQL.split("\n");
        for (String eachSql : sql) {
            entityManager.joinTransaction();
            Query query = entityManager.createNativeQuery(eachSql);
            query.executeUpdate();
        }
    }
}
