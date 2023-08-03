package uk.co.bbr.services.tags.sql.dto;

import lombok.Getter;
import uk.co.bbr.services.framework.sql.AbstractSqlDto;
import uk.co.bbr.services.tags.dao.ContestTagDao;

import java.math.BigInteger;

@Getter
public class TagListSqlDto extends AbstractSqlDto {

    private final String tagSlug;
    private final String tagName;
    private final Integer contestCount;
    private final Integer groupCount;

    public TagListSqlDto(Object[] columnList) {
        this.tagSlug = (String)columnList[0];
        this.tagName = (String)columnList[1];
        if (columnList[2] != null) {
            this.contestCount = this.getInteger(columnList, 2);
        }
        else {
            this.contestCount = 0;
        }
        if (columnList[3] != null) {
            this.groupCount = this.getInteger(columnList, 3);
        }
        else {
            this.groupCount = 0;
        }
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
