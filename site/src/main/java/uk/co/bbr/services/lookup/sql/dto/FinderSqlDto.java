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
        this.name = (String)columnList[0];
        this.slug = (String)columnList[1];
        Date tempStartDate = (Date)columnList[2];
        if (tempStartDate != null) {
            this.startDate = tempStartDate.toLocalDate();
        } else {
            this.startDate = null;
        }
        Date tempEndDate = (Date)columnList[3];
        if (tempEndDate != null) {
            this.endDate = tempEndDate.toLocalDate();
        } else {
            this.endDate = null;
        }
    }
}
