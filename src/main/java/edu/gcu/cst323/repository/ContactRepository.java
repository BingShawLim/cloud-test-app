package edu.gcu.cst323.repository;

import edu.gcu.cst323.model.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for the Contact entity.
 * Supplies the CRUD operations required by the Activity 1 specification.
 */
@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {

    List<Contact> findAllByOrderByLastNameAscFirstNameAsc();

    boolean existsByEmailIgnoreCase(String email);
}
