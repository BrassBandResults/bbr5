package uk.co.bbr.services.people.sql.dto;

import lombok.Getter;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.framework.sql.AbstractSqlDto;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.regions.dao.RegionDao;

import java.math.BigInteger;

@Getter
public class PeopleWinnersSqlDto extends AbstractSqlDto {

    private final String personSlug;
    private final String personSurname;
    private final String personFirstNames;
    private final String personKnownFor;
    private final Integer wins;
    private final Integer contests;

    public PeopleWinnersSqlDto(Object[] columnList) {
        this.personSlug = (String)columnList[0];
        this.personSurname = (String)columnList[1];
        this.personFirstNames = (String)columnList[2];
        this.personKnownFor = (String)columnList[3];
        this.wins = columnList[4] instanceof BigInteger ? ((BigInteger)columnList[4]).intValue() : (Integer)columnList[4];
        this.contests = columnList[5] instanceof BigInteger ? ((BigInteger)columnList[5]).intValue() : (Integer)columnList[5];
    }

    public PersonDao getPerson() {
        PersonDao returnPerson = new PersonDao();
        returnPerson.setSurname(this.personSurname);
        returnPerson.setFirstNames(this.personFirstNames);
        returnPerson.setSlug(this.personSlug);
        returnPerson.setKnownFor(this.personKnownFor);

        return returnPerson;
    }

    public Integer getPercentage() {
        return (this.wins * 100) / this.contests;
    }
}
