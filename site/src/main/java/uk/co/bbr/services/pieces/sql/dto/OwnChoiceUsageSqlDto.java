package uk.co.bbr.services.pieces.sql.dto;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class OwnChoiceUsageSqlDto {
    @Getter
    List<OwnChoiceUsagePieceSqlDto> pieceList = new ArrayList<>();

    public void add(OwnChoiceUsagePieceSqlDto eachReturnObject) {
        this.pieceList.add(eachReturnObject);
    }
}
