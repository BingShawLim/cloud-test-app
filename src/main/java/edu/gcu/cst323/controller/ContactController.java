package edu.gcu.cst323.controller;

import edu.gcu.cst323.model.Contact;
import edu.gcu.cst323.service.ContactService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

/**
 * MVC controller for the Contact Manager test application.
 *
 * Method entry, exit and exception logging is applied by
 * {@link edu.gcu.cst323.logging.LoggingInterceptor}; this class only logs business
 * events (not found, validation failure, handled save/delete failure).
 *
 * Page map:
 *   GET  /                  home / dashboard
 *   GET  /contacts          list all contacts        (READ)
 *   GET  /contacts/new      blank contact form       (CREATE - form)
 *   GET  /contacts/{id}     contact detail view      (READ)
 *   GET  /contacts/{id}/edit  populated form         (UPDATE - form)
 *   POST /contacts/save     insert or update         (CREATE / UPDATE)
 *   POST /contacts/{id}/delete  remove a contact     (DELETE)
 */
@Controller
@RequestMapping("/")
public class ContactController {

    private static final Logger log = LoggerFactory.getLogger(ContactController.class);

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @GetMapping
    public String home(Model model) {
        model.addAttribute("contactCount", contactService.count());
        return "index";
    }

    @GetMapping("/contacts")
    public String listContacts(Model model) {
        model.addAttribute("contacts", contactService.findAll());
        return "contact-list";
    }

    @GetMapping("/contacts/new")
    public String newContactForm(Model model) {
        model.addAttribute("contact", new Contact());
        model.addAttribute("pageTitle", "Add Contact");
        return "contact-form";
    }

    @GetMapping("/contacts/{id}")
    public String viewContact(@PathVariable Long id, Model model, RedirectAttributes redirect) {
        Optional<Contact> contact = contactService.findById(id);
        if (contact.isEmpty()) {
            log.warn("[CONTROLLER] ContactController.viewContact() - NOT FOUND - id={}", id);
            redirect.addFlashAttribute("errorMessage", "Contact " + id + " was not found.");
            return "redirect:/contacts";
        }
        model.addAttribute("contact", contact.get());
        return "contact-view";
    }

    @GetMapping("/contacts/{id}/edit")
    public String editContactForm(@PathVariable Long id, Model model, RedirectAttributes redirect) {
        Optional<Contact> contact = contactService.findById(id);
        if (contact.isEmpty()) {
            log.warn("[CONTROLLER] ContactController.editContactForm() - NOT FOUND - id={}", id);
            redirect.addFlashAttribute("errorMessage", "Contact " + id + " was not found.");
            return "redirect:/contacts";
        }
        model.addAttribute("contact", contact.get());
        model.addAttribute("pageTitle", "Edit Contact");
        return "contact-form";
    }

    @PostMapping("/contacts/save")
    public String saveContact(@Valid @ModelAttribute("contact") Contact contact,
                              BindingResult bindingResult,
                              Model model,
                              RedirectAttributes redirect) {
        boolean isNew = contact.getContactId() == null;

        // Business rule: EMAIL carries a UNIQUE constraint. Check on add *and* edit, so an
        // edit that collides with another contact is reported on the field instead of
        // failing later as a database constraint violation.
        if (contactService.emailInUseByAnotherContact(contact.getEmail(), contact.getContactId())) {
            bindingResult.rejectValue("email", "email.duplicate",
                    "That email address is already on file.");
        }

        if (bindingResult.hasErrors()) {
            log.warn("[CONTROLLER] ContactController.saveContact() - VALIDATION FAILED - {} error(s): {}",
                    bindingResult.getErrorCount(), bindingResult.getFieldErrors().stream()
                            .map(f -> f.getField() + "=" + f.getCode()).toList());
            model.addAttribute("pageTitle", isNew ? "Add Contact" : "Edit Contact");
            return "contact-form";
        }

        try {
            Contact saved = contactService.save(contact);
            redirect.addFlashAttribute("successMessage",
                    "Contact " + saved.getFullName() + (isNew ? " added." : " updated."));
            return "redirect:/contacts";
        } catch (Exception e) {
            log.error("[CONTROLLER] ContactController.saveContact() - SAVE FAILED - {}", e.getMessage(), e);
            model.addAttribute("pageTitle", isNew ? "Add Contact" : "Edit Contact");
            model.addAttribute("errorMessage", "The contact could not be saved. Please try again.");
            return "contact-form";
        }
    }

    @PostMapping("/contacts/{id}/delete")
    public String deleteContact(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            contactService.deleteById(id);
            redirect.addFlashAttribute("successMessage", "Contact deleted successfully.");
        } catch (Exception e) {
            log.error("[CONTROLLER] ContactController.deleteContact() - DELETE FAILED - id={} - {}", id, e.getMessage(), e);
            redirect.addFlashAttribute("errorMessage", "Unable to delete contact " + id + ".");
        }
        return "redirect:/contacts";
    }
}
