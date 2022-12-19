package uk.co.bbr.services.people.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import uk.co.bbr.services.people.dao.PersonDao;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class PeopleListDto {
    private final int returnedPeopleCount;
    private final long allPeopleCount;
    private final String searchPrefix;
    private final List<PersonDao> returnedPeople;
}
