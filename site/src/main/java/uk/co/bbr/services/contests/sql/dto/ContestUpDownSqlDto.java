package uk.co.bbr.services.contests.sql.dto;

import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.framework.sql.AbstractSqlDto;

public class ContestUpDownSqlDto extends AbstractSqlDto {
    private final String contestName;
    private final String contestSlug;

    public ContestUpDownSqlDto(Object[] columnList) {
        this.contestName = this.getString(columnList, 0);
        this.contestSlug = this.getString(columnList, 1);
    }

    public ContestDao getContest() {
        ContestDao contest = new ContestDao();
        contest.setName(this.contestName);
        contest.setSlug(this.contestSlug);
        return contest;
    }
}
