package edu.gcu.cst323.service;

import edu.gcu.cst323.model.Contact;

import java.util.List;
import java.util.Optional;

/**
 * Business service contract for Contact CRUD operations.
 *
 * The controller depends on this interface rather than on a concrete class, which keeps
 * the presentation layer decoupled from the business layer as required by the n-layer
 * design documented in the Activity 1 design report.
 */
public interface ContactService {

    /** READ - retrieve all contacts, sorted by last name then first name. */
    List<Contact> findAll();

    /** READ - retrieve a single contact by primary key. */
    Optional<Contact> findById(Long id);

    /** CREATE and UPDATE - persists a new contact or updates an existing one. */
    Contact save(Contact contact);

    /** DELETE - removes a contact by primary key. */
    void deleteById(Long id);

    /**
     * Business rule check: is this email address already used by a different contact?
     *
     * @param email     the address to test
     * @param contactId the contact being edited, or {@code null} when adding a new contact
     * @return true when another row already holds this address
     */
    boolean emailInUseByAnotherContact(String email, Long contactId);

    /** Convenience count used by the home page dashboard. */
    long count();
}
