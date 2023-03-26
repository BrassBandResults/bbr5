package uk.co.bbr.services.contests.sql.dto;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class PersonConductingSqlDto {
    private List<PersonConductingResultSqlDto> results = new ArrayList<>();

    public void add(PersonConductingResultSqlDto eachReturnObject) {
        this.results.add(eachReturnObject);
    }
}
