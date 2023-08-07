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
        this.contestName = (String)columnList[0];
        this.contestSlug = (String)columnList[1];
        this.extinct = columnList[2] != null && (Boolean)columnList[2];
        this.eventCount = this.getInteger(columnList, 3);

    }

    public ContestDao asContest() {
        ContestDao returnContest = new ContestDao();
        returnContest.setName(this.contestName);
        returnContest.setSlug(this.contestSlug);
        returnContest.setEventsCount(this.eventCount);
        returnContest.setExtinct(this.extinct);
        return returnContest;
    }
}
