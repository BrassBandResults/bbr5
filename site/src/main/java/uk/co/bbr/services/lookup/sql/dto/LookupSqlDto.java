package uk.co.bbr.services.lookup.sql.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import uk.co.bbr.services.framework.sql.AbstractSqlDto;
import uk.co.bbr.services.framework.types.EntityType;

@Getter
public class LookupSqlDto extends AbstractSqlDto {

    private final String name;
    private final String slug;
    private final String context;
    private final String offset;
    private final String aliasOrObject;

    public LookupSqlDto(Object[] columnList) {
        this.name = this.getString(columnList, 0);
        this.slug = this.getString(columnList, 1);
        this.context = this.getString(columnList, 2);
        this.offset = this.getString(columnList, 3);
        this.aliasOrObject = this.getString(columnList, 4);
    }

    public boolean isAlias() {
        return this.aliasOrObject.equals("A");
    }

    public ObjectNode asLookup(ObjectMapper objectMapper) {
        ObjectNode person = objectMapper.createObjectNode();
        person.put("slug", this.getSlug());
        person.put("name", this.name.replace("'", "`"));
        person.put("context", this.context);
        return person;
    }

    public EntityType getType() {
        return EntityType.fromOffset(this.offset);
    }
}
