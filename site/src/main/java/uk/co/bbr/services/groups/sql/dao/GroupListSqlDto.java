package uk.co.bbr.services.groups.sql.dao;

import lombok.Getter;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.framework.sql.AbstractSqlDto;
import uk.co.bbr.services.groups.dao.ContestGroupDao;
import uk.co.bbr.services.regions.dao.RegionDao;

import java.math.BigInteger;

@Getter
public class GroupListSqlDto extends AbstractSqlDto {

    private final String groupSlug;
    private final String groupName;
    private final Integer eventCount;
    private final Integer contestCount;

    public GroupListSqlDto(Object[] columnList) {
        this.groupName = this.getString(columnList, 0);
        this.groupSlug = this.getString(columnList, 1);
        if (columnList[2] != null) {
            this.eventCount = this.getInteger(columnList, 2);
        }
        else {
            this.eventCount = 0;
        }
        if (columnList[3] != null) {
            this.contestCount = this.getInteger(columnList, 3);
        }
        else {
            this.contestCount = 0;
        }

    }

    public ContestGroupDao asGroup() {
        ContestGroupDao returnGroup = new ContestGroupDao();
        returnGroup.setName(this.groupName);
        returnGroup.setSlug(this.groupSlug.toUpperCase());
        returnGroup.setEventCount(this.eventCount);
        returnGroup.setContestCount(this.contestCount);

        return returnGroup;
    }
}
