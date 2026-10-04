package edu.gcu.cst323.service;

import edu.gcu.cst323.model.Contact;
import edu.gcu.cst323.repository.ContactRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Business service implementation for Contact CRUD operations.
 * Entry, exit and exception logging for every method is applied by
 * {@link edu.gcu.cst323.logging.LoggingInterceptor} (interceptor pattern, Activity 6).
 */
@Service
public class ContactServiceImpl implements ContactService {

    private final ContactRepository contactRepository;

    public ContactServiceImpl(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Contact> findAll() {
        return contactRepository.findAllByOrderByLastNameAscFirstNameAsc();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Contact> findById(Long id) {
        return contactRepository.findById(id);
    }

    /**
     * CREATE and UPDATE.
     *
     * CREATED_DATE is an audit column owned by the business layer, not by the form. On an
     * update the value is read back from the database and re-applied, so the column keeps
     * the original creation timestamp and can never be set or cleared by whatever the
     * browser posts.
     */
    @Override
    @Transactional
    public Contact save(Contact contact) {
        if (contact.getContactId() != null) {
            contactRepository.findById(contact.getContactId())
                    .ifPresent(existing -> contact.setCreatedDate(existing.getCreatedDate()));
        }
        if (contact.getCreatedDate() == null) {
            contact.setCreatedDate(LocalDateTime.now());
        }

        return contactRepository.save(contact);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        contactRepository.deleteById(id);
    }

    /**
     * Applies to both add and edit. On an edit the contact's own row is excluded, so
     * re-saving a contact without changing its email is not treated as a duplicate.
     */
    @Override
    @Transactional(readOnly = true)
    public boolean emailInUseByAnotherContact(String email, Long contactId) {
        if (email == null || email.isBlank()) {
            return false;
        }
        return (contactId == null)
                ? contactRepository.existsByEmailIgnoreCase(email)
                : contactRepository.existsByEmailIgnoreCaseAndContactIdNot(email, contactId);
    }

    @Override
    @Transactional(readOnly = true)
    public long count() {
        return contactRepository.count();
    }
}
