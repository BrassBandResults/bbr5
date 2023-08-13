package uk.co.bbr.services.groups.sql.dao;

import lombok.Getter;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.framework.sql.AbstractSqlDto;

@Getter
public class ContestListSqlDto extends AbstractSqlDto {

    private final String contestSlug;
    private final String contestName;
    private final Integer eventCount;
    private final boolean extinct;

    public ContestListSqlDto(Object[] columnList) {
        this.contestName = this.getString(columnList, 0);
        this.contestSlug = this.getString(columnList, 1);
        this.extinct = this.getBoolean(columnList,2);
        this.eventCount = this.getInteger(columnList, 3);

    }

    public ContestDao asContest() {
        ContestDao returnContest = new ContestDao();
        returnContest.setName(this.contestName);
        returnContest.setSlug(this.contestSlug);
        if (this.eventCount != null) {
            returnContest.setEventsCount(this.eventCount);
        }
        returnContest.setExtinct(this.extinct);
        return returnContest;
    }
}
