package uk.co.bbr.services.tags.sql.dto;

import lombok.Getter;
import uk.co.bbr.services.framework.sql.AbstractSqlDto;
import uk.co.bbr.services.tags.dao.ContestTagDao;

@Getter
public class TagListSqlDto extends AbstractSqlDto {

    private final String tagSlug;
    private final String tagName;
    private final Integer contestCount;
    private final Integer groupCount;

    public TagListSqlDto(Object[] columnList) {
        this.tagSlug = this.getString(columnList, 0);
        this.tagName = this.getString(columnList, 1);
        this.contestCount = this.getIntegerOrZero(columnList, 2);
        this.groupCount = this.getIntegerOrZero(columnList, 3);
    }

    public ContestTagDao asContestTag() {
        ContestTagDao contestTag = new ContestTagDao();
        contestTag.setSlug(this.tagSlug);
        contestTag.setName(this.tagName);
        contestTag.setContestCount(this.contestCount);
        contestTag.setGroupCount(this.groupCount);
        return contestTag;
    }
}
