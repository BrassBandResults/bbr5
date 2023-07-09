package uk.co.bbr.services.lookup.sql.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import uk.co.bbr.services.framework.sql.AbstractSqlDto;

@Getter
public class LookupSqlDto extends AbstractSqlDto {

    private final String name;
    private final String slug;
    private final String context;
    private final String offset;

    public LookupSqlDto(Object[] columnList) {
        this.name = (String)columnList[0];
        this.slug = (String)columnList[1];
        this.context = (String)columnList[2];
        this.offset = (String)columnList[3];
    }

    public ObjectNode asLookup(ObjectMapper objectMapper) {
        ObjectNode person = objectMapper.createObjectNode();
        person.put("slug", this.getSlug());
        person.put("name", this.name.replace("'", "`"));
        person.put("context", this.context);
        return person;
    }
}
