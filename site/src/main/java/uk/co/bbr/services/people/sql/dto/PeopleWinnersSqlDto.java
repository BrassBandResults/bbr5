package uk.co.bbr.services.people.sql.dto;

import lombok.Getter;
import uk.co.bbr.services.framework.sql.AbstractSqlDto;
import uk.co.bbr.services.people.dao.PersonDao;

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
        this.wins = this.getInteger(columnList, 4);
        this.contests = this.getInteger(columnList, 5);
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
