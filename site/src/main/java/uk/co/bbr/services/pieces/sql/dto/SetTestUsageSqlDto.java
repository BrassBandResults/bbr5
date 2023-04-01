package uk.co.bbr.services.pieces.sql.dto;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class SetTestUsageSqlDto {

    @Getter
    List<SetTestUsagePieceSqlDto> setTests = new ArrayList<>();

    public void add(SetTestUsagePieceSqlDto eachReturnObject) {
        this.setTests.add(eachReturnObject);
    }
}
