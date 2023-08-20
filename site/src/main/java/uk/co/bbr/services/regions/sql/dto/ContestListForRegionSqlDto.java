package uk.co.bbr.services.regions.sql.dto;

import lombok.Getter;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.framework.sql.AbstractSqlDto;

@Getter
public class ContestListForRegionSqlDto extends AbstractSqlDto {

    private final String contestSlug;
    private final String contestName;
    private final Integer eventCount;

    public ContestListForRegionSqlDto(Object[] columnList) {
        this.contestName = this.getString(columnList, 0);
        this.contestSlug = this.getString(columnList, 1);
        this.eventCount = this.getInteger(columnList, 2);
    }

    public ContestDao asContest() {
        ContestDao returnContest = new ContestDao();
        returnContest.setName(this.contestName);
        returnContest.setSlug(this.contestSlug);
        returnContest.setEventsCount(this.eventCount);
        return returnContest;
    }
}
