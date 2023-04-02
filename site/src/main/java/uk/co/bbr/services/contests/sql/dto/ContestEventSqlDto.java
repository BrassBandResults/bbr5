package uk.co.bbr.services.contests.sql.dto;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class ContestEventSqlDto {
    @Getter
    private List<ContestEventResultSqlDto> events = new ArrayList<>();

    public void add(ContestEventResultSqlDto eachReturnObject) {
        this.events.add(eachReturnObject);
    }
}
