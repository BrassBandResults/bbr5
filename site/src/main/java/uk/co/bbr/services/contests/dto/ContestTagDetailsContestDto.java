package uk.co.bbr.services.contests.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.contests.dao.ContestGroupDao;
import uk.co.bbr.services.framework.types.EntityType;

import java.util.Collection;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class ContestTagDetailsContestDto {
    private final String name;
    private final String slug;
    private final EntityType type;
}
