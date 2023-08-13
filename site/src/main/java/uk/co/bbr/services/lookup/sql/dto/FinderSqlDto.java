package uk.co.bbr.services.lookup.sql.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import uk.co.bbr.services.framework.sql.AbstractSqlDto;
import uk.co.bbr.services.framework.types.EntityType;

import java.sql.Date;
import java.time.LocalDate;

@Getter
public class FinderSqlDto extends AbstractSqlDto {

    private final String name;
    private final String slug;
    private final LocalDate startDate;
    private final LocalDate endDate;

    public FinderSqlDto(Object[] columnList) {
        this.name = this.getString(columnList, 0);
        this.slug = this.getString(columnList, 1);
        this.startDate = this.getLocalDate(columnList, 2);
        this.endDate = this.getLocalDate(columnList, 3);
    }
}
