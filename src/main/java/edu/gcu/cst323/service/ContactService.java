package edu.gcu.cst323.service;

import edu.gcu.cst323.model.Contact;
import edu.gcu.cst323.repository.ContactRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Business service layer for Contact CRUD operations.
 * Every method logs entry, exit, and exception paths per the DevOps logging
 * requirements introduced in the Activity Guide.
 */
@Service
public class ContactService {

    private static final Logger log = LoggerFactory.getLogger(ContactService.class);

    private final ContactRepository contactRepository;

    public ContactService(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    /** READ - retrieve all contacts, sorted by last name then first name. */
    @Transactional(readOnly = true)
    public List<Contact> findAll() {
        log.info("ContactService.findAll() - ENTRY");
        try {
            List<Contact> contacts = contactRepository.findAllByOrderByLastNameAscFirstNameAsc();
            log.info("ContactService.findAll() - EXIT - retrieved {} contact(s)", contacts.size());
            return contacts;
        } catch (Exception e) {
            log.error("ContactService.findAll() - EXCEPTION - {}", e.getMessage(), e);
            throw e;
        }
    }

    /** READ - retrieve a single contact by primary key. */
    @Transactional(readOnly = true)
    public Optional<Contact> findById(Long id) {
        log.info("ContactService.findById() - ENTRY - id={}", id);
        try {
            Optional<Contact> contact = contactRepository.findById(id);
            log.info("ContactService.findById() - EXIT - found={}", contact.isPresent());
            return contact;
        } catch (Exception e) {
            log.error("ContactService.findById() - EXCEPTION - id={} - {}", id, e.getMessage(), e);
            throw e;
        }
    }

    /** CREATE and UPDATE - persists a new contact or updates an existing one. */
    @Transactional
    public Contact save(Contact contact) {
        log.info("ContactService.save() - ENTRY - contact={}", contact);
        try {
            Contact saved = contactRepository.save(contact);
            log.info("ContactService.save() - EXIT - persisted contactId={}", saved.getContactId());
            return saved;
        } catch (Exception e) {
            log.error("ContactService.save() - EXCEPTION - {}", e.getMessage(), e);
            throw e;
        }
    }

    /** DELETE - removes a contact by primary key. */
    @Transactional
    public void deleteById(Long id) {
        log.info("ContactService.deleteById() - ENTRY - id={}", id);
        try {
            contactRepository.deleteById(id);
            log.info("ContactService.deleteById() - EXIT - deleted contactId={}", id);
        } catch (Exception e) {
            log.error("ContactService.deleteById() - EXCEPTION - id={} - {}", id, e.getMessage(), e);
            throw e;
        }
    }

    /** Supporting check used by the controller to reject duplicate email addresses. */
    @Transactional(readOnly = true)
    public boolean emailAlreadyExists(String email) {
        log.debug("ContactService.emailAlreadyExists() - ENTRY - email={}", email);
        boolean exists = contactRepository.existsByEmailIgnoreCase(email);
        log.debug("ContactService.emailAlreadyExists() - EXIT - exists={}", exists);
        return exists;
    }

    /** Convenience count used by the home page dashboard. */
    @Transactional(readOnly = true)
    public long count() {
        log.debug("ContactService.count() - ENTRY");
        long total = contactRepository.count();
        log.debug("ContactService.count() - EXIT - total={}", total);
        return total;
    }
}
