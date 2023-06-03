package uk.co.bbr.services.people.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.co.bbr.services.bands.dao.BandRelationshipTypeDao;
import uk.co.bbr.services.people.dao.PersonAliasDao;
import uk.co.bbr.services.people.dao.PersonRelationshipTypeDao;

import java.util.List;
import java.util.Optional;

public interface PersonRelationshipTypeRepository extends JpaRepository<PersonRelationshipTypeDao, Long> {
    @Query("SELECT t FROM PersonRelationshipTypeDao t ORDER BY t.name")
    List<PersonRelationshipTypeDao> findAllOrderByName();

    @Query("SELECT t FROM PersonRelationshipTypeDao t WHERE t.name = :typeName")
    PersonRelationshipTypeDao fetchByName(String typeName);
}
