package uk.co.bbr.services.groups.sql.dao;

import lombok.Getter;
import uk.co.bbr.services.framework.sql.AbstractSqlDto;
import uk.co.bbr.services.groups.dao.ContestGroupDao;

@Getter
public class GroupListSqlDto extends AbstractSqlDto {

    private final String groupSlug;
    private final String groupName;
    private final Integer eventCount;
    private final Integer contestCount;

    public GroupListSqlDto(Object[] columnList) {
        this.groupName = this.getString(columnList, 0);
        this.groupSlug = this.getString(columnList, 1);
        this.eventCount = this.getIntegerOrZero(columnList, 2);
        this.contestCount = this.getIntegerOrZero(columnList, 3);
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
