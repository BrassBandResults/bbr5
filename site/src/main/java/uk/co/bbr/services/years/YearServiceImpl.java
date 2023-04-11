package uk.co.bbr.services.years;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.years.sql.YearSql;
import uk.co.bbr.services.years.sql.dto.YearListEntrySqlDto;

import javax.persistence.EntityManager;
import java.util.List;

@Service
@RequiredArgsConstructor
public class YearServiceImpl implements YearService {
    private final EntityManager entityManager;

    @Override
    public List<YearListEntrySqlDto> fetchFullYearList() {
        return YearSql.selectSetTestPieceUsage(this.entityManager);
    }
}
