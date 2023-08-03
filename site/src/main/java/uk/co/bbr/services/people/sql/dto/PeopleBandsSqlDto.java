package uk.co.bbr.services.people.sql.dto;

import lombok.Getter;
import uk.co.bbr.services.framework.sql.AbstractSqlDto;
import uk.co.bbr.services.people.dao.PersonDao;

import java.math.BigInteger;

@Getter
public class PeopleBandsSqlDto extends AbstractSqlDto {

    private final String personSlug;
    private final String personSurname;
    private final String personFirstNames;
    private final String personKnownFor;
    private final Integer bands;

    public PeopleBandsSqlDto(Object[] columnList) {
        this.personSlug = (String)columnList[0];
        this.personSurname = (String)columnList[1];
        this.personFirstNames = (String)columnList[2];
        this.personKnownFor = (String)columnList[3];
        this.bands = this.getInteger(columnList, 4);
    }

    public PersonDao getPerson() {
        PersonDao returnPerson = new PersonDao();
        returnPerson.setSurname(this.personSurname);
        returnPerson.setFirstNames(this.personFirstNames);
        returnPerson.setSlug(this.personSlug);
        returnPerson.setKnownFor(this.personKnownFor);

        return returnPerson;
    }
}
