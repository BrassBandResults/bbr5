package uk.co.bbr.services.people.sql.dto;

import lombok.Getter;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.framework.sql.AbstractSqlDto;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.regions.dao.RegionDao;

import java.math.BigInteger;

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
        this.personSurname = (String)columnList[0];
        this.personFirstNames = (String)columnList[1];
        this.personSlug = (String)columnList[2];
        this.personSuffix = (String)columnList[3];
        this.personKnownFor = (String)columnList[4];
        if (columnList[5] != null) {
            this.conductor1Count = columnList[5] instanceof BigInteger ? ((BigInteger) columnList[5]).intValue() : (Integer) columnList[5];
        }
        else {
            this.conductor1Count = 0;
        }
        if (columnList[6] != null) {
            this.conductor2Count = columnList[6] instanceof BigInteger ? ((BigInteger) columnList[6]).intValue() : (Integer) columnList[6];
        }
        else {
            this.conductor2Count = 0;
        }
        if (columnList[7] != null) {
            this.conductor3Count = columnList[7] instanceof BigInteger ? ((BigInteger) columnList[7]).intValue() : (Integer) columnList[7];
        }
        else {
            this.conductor3Count = 0;
        }
        if (columnList[8] != null) {
            this.adjudicatorCount = columnList[8] instanceof BigInteger ? ((BigInteger) columnList[8]).intValue() : (Integer) columnList[8];
        }
        else {
            this.adjudicatorCount = 0;
        }
        if (columnList[9] != null) {
            this.composerCount = columnList[9] instanceof BigInteger ? ((BigInteger) columnList[9]).intValue() : (Integer) columnList[9];
        }
        else {
            this.composerCount = 0;
        }
        if (columnList[10] != null) {
            this.arrangerCount = columnList[10] instanceof BigInteger ? ((BigInteger) columnList[10]).intValue() : (Integer) columnList[10];
        }
        else {
            this.arrangerCount = 0;
        }
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
