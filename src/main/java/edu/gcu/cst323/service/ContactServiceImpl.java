package edu.gcu.cst323.service;

import edu.gcu.cst323.model.Contact;
import edu.gcu.cst323.repository.ContactRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Business service implementation for Contact CRUD operations.
 * Every method logs entry, exit, and exception paths per the DevOps logging
 * requirements introduced in the Activity Guide.
 */
@Service
public class ContactServiceImpl implements ContactService {

    private static final Logger log = LoggerFactory.getLogger(ContactServiceImpl.class);

    private final ContactRepository contactRepository;

    public ContactServiceImpl(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    @Override
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

    @Override
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
        log.info("ContactService.save() - ENTRY - contact={}", contact);
        try {
            if (contact.getContactId() != null) {
                contactRepository.findById(contact.getContactId())
                        .ifPresent(existing -> contact.setCreatedDate(existing.getCreatedDate()));
            }
            if (contact.getCreatedDate() == null) {
                contact.setCreatedDate(LocalDateTime.now());
            }

            Contact saved = contactRepository.save(contact);
            log.info("ContactService.save() - EXIT - persisted contactId={}", saved.getContactId());
            return saved;
        } catch (Exception e) {
            log.error("ContactService.save() - EXCEPTION - {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
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

    /**
     * Applies to both add and edit. On an edit the contact's own row is excluded, so
     * re-saving a contact without changing its email is not treated as a duplicate.
     */
    @Override
    @Transactional(readOnly = true)
    public boolean emailInUseByAnotherContact(String email, Long contactId) {
        log.debug("ContactService.emailInUseByAnotherContact() - ENTRY - email={}, contactId={}",
                email, contactId);
        if (email == null || email.isBlank()) {
            log.debug("ContactService.emailInUseByAnotherContact() - EXIT - blank email, exists=false");
            return false;
        }
        boolean exists = (contactId == null)
                ? contactRepository.existsByEmailIgnoreCase(email)
                : contactRepository.existsByEmailIgnoreCaseAndContactIdNot(email, contactId);
        log.debug("ContactService.emailInUseByAnotherContact() - EXIT - exists={}", exists);
        return exists;
    }

    @Override
    @Transactional(readOnly = true)
    public long count() {
        log.debug("ContactService.count() - ENTRY");
        long total = contactRepository.count();
        log.debug("ContactService.count() - EXIT - total={}", total);
        return total;
    }
}
