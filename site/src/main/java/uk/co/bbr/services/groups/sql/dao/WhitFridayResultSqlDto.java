package uk.co.bbr.services.groups.sql.dao;

import lombok.Getter;
import uk.co.bbr.services.framework.sql.AbstractSqlDto;

@Getter
public class WhitFridayResultSqlDto  extends AbstractSqlDto {
    private final String bandName;
    private final String bandSlug;
    private final String competedAs;
    private final String regionSlug;
    private final String regionName;
    private final String regionCountryCode;
    private final int position;


    public WhitFridayResultSqlDto(Object[] columnList) {
        this.bandName = this.getString(columnList, 0);
        this.bandSlug = this.getString(columnList, 1);
        this.competedAs = this.getString(columnList, 2);
        this.regionSlug = this.getString(columnList, 3);
        this.regionName = this.getString(columnList, 4);
        this.regionCountryCode = this.getString(columnList, 5);
        this.position = this.getInteger(columnList, 6);
    }
}
