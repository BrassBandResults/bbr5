package uk.co.bbr.services.people.sql.dto;

import lombok.Getter;
import uk.co.bbr.services.framework.sql.AbstractSqlDto;
import uk.co.bbr.services.people.dao.PersonDao;

@Getter
public class PeopleListSqlDto extends AbstractSqlDto {

    private final String personSurname;
    private final String personFirstNames;
    private final String personSlug;
    private final String personSuffix;
    private final String personKnownFor;
    private final Integer conductor1Count;
    private final Integer conductor2Count;
    private final Integer conductor3Count;
    private final Integer adjudicatorCount;
    private final Integer composerCount;
    private final Integer arrangerCount;


    public PeopleListSqlDto(Object[] columnList) {
        this.personSurname = this.getString(columnList, 0);
        this.personFirstNames = this.getString(columnList, 1);
        this.personSlug = this.getString(columnList, 2);
        this.personSuffix = this.getString(columnList, 3);
        this.personKnownFor = this.getString(columnList, 4);
        this.conductor1Count = this.getIntegerOrZero(columnList, 5);
        this.conductor2Count = this.getIntegerOrZero(columnList, 6);
        this.conductor3Count = this.getIntegerOrZero(columnList, 7);
        this.adjudicatorCount = this.getIntegerOrZero(columnList, 8);
        this.composerCount = this.getIntegerOrZero(columnList, 9);
        this.arrangerCount = this.getIntegerOrZero(columnList, 10);
    }

    public PersonDao asPerson() {
        PersonDao returnPerson = new PersonDao();
        returnPerson.setSurname(this.personSurname);
        returnPerson.setFirstNames(this.personFirstNames);
        returnPerson.setSlug(this.personSlug);
        returnPerson.setSuffix(this.personSuffix);
        returnPerson.setKnownFor(this.personKnownFor);
        returnPerson.setConductingCount(this.conductor1Count + this.conductor2Count + this.conductor3Count);
        returnPerson.setAdjudicationsCount(this.adjudicatorCount);
        returnPerson.setCompositionsCount(this.composerCount);
        returnPerson.setArrangementsCount(this.arrangerCount);

        return returnPerson;
    }
}
