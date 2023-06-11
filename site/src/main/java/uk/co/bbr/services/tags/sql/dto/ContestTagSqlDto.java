package uk.co.bbr.services.tags.sql.dto;

import lombok.Getter;
import uk.co.bbr.services.framework.sql.AbstractSqlDto;

@Getter
public class ContestTagSqlDto extends AbstractSqlDto {

    private final String contestSlug;
    private final String tagSlug;
    private final String tagName;

    public ContestTagSqlDto(Object[] columnList) {
        this.contestSlug = (String)columnList[0];
        this.tagSlug = (String)columnList[1];
        this.tagName = (String)columnList[2];
    }
}
