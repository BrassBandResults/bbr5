package uk.co.bbr.services.years.sql.dto;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class ContestsForYearSqlDto {

    @Getter
    List<ContestsForYearEventSqlDto> events = new ArrayList<>();

    public void add(ContestsForYearEventSqlDto eachReturnObject) {
        this.events.add(eachReturnObject);
    }
}
