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

    /** Duplicate-email check used when adding a new contact. */
    boolean existsByEmailIgnoreCase(String email);

    /** Duplicate-email check used when editing, excluding the contact's own row. */
    boolean existsByEmailIgnoreCaseAndContactIdNot(String email, Long contactId);
}
