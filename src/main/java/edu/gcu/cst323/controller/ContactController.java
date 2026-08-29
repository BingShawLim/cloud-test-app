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
        log.info("ContactController.home() - ENTRY");
        model.addAttribute("contactCount", contactService.count());
        log.info("ContactController.home() - EXIT - view=index");
        return "index";
    }

    @GetMapping("/contacts")
    public String listContacts(Model model) {
        log.info("ContactController.listContacts() - ENTRY");
        model.addAttribute("contacts", contactService.findAll());
        log.info("ContactController.listContacts() - EXIT - view=contact-list");
        return "contact-list";
    }

    @GetMapping("/contacts/new")
    public String newContactForm(Model model) {
        log.info("ContactController.newContactForm() - ENTRY");
        model.addAttribute("contact", new Contact());
        model.addAttribute("pageTitle", "Add Contact");
        log.info("ContactController.newContactForm() - EXIT - view=contact-form");
        return "contact-form";
    }

    @GetMapping("/contacts/{id}")
    public String viewContact(@PathVariable Long id, Model model, RedirectAttributes redirect) {
        log.info("ContactController.viewContact() - ENTRY - id={}", id);
        Optional<Contact> contact = contactService.findById(id);
        if (contact.isEmpty()) {
            log.warn("ContactController.viewContact() - contact not found - id={}", id);
            redirect.addFlashAttribute("errorMessage", "Contact " + id + " was not found.");
            return "redirect:/contacts";
        }
        model.addAttribute("contact", contact.get());
        log.info("ContactController.viewContact() - EXIT - view=contact-view");
        return "contact-view";
    }

    @GetMapping("/contacts/{id}/edit")
    public String editContactForm(@PathVariable Long id, Model model, RedirectAttributes redirect) {
        log.info("ContactController.editContactForm() - ENTRY - id={}", id);
        Optional<Contact> contact = contactService.findById(id);
        if (contact.isEmpty()) {
            log.warn("ContactController.editContactForm() - contact not found - id={}", id);
            redirect.addFlashAttribute("errorMessage", "Contact " + id + " was not found.");
            return "redirect:/contacts";
        }
        model.addAttribute("contact", contact.get());
        model.addAttribute("pageTitle", "Edit Contact");
        log.info("ContactController.editContactForm() - EXIT - view=contact-form");
        return "contact-form";
    }

    @PostMapping("/contacts/save")
    public String saveContact(@Valid @ModelAttribute("contact") Contact contact,
                              BindingResult bindingResult,
                              Model model,
                              RedirectAttributes redirect) {
        log.info("ContactController.saveContact() - ENTRY - contact={}", contact);

        boolean isNew = contact.getContactId() == null;

        if (isNew && contact.getEmail() != null
                && contactService.emailAlreadyExists(contact.getEmail())) {
            bindingResult.rejectValue("email", "email.duplicate",
                    "That email address is already on file.");
        }

        if (bindingResult.hasErrors()) {
            log.warn("ContactController.saveContact() - validation failed - {} error(s)",
                    bindingResult.getErrorCount());
            model.addAttribute("pageTitle", isNew ? "Add Contact" : "Edit Contact");
            return "contact-form";
        }

        try {
            Contact saved = contactService.save(contact);
            redirect.addFlashAttribute("successMessage",
                    "Contact " + saved.getFullName() + (isNew ? " added." : " updated."));
            log.info("ContactController.saveContact() - EXIT - redirect=/contacts");
            return "redirect:/contacts";
        } catch (Exception e) {
            log.error("ContactController.saveContact() - EXCEPTION - {}", e.getMessage(), e);
            model.addAttribute("pageTitle", isNew ? "Add Contact" : "Edit Contact");
            model.addAttribute("errorMessage", "The contact could not be saved. Please try again.");
            return "contact-form";
        }
    }

    @PostMapping("/contacts/{id}/delete")
    public String deleteContact(@PathVariable Long id, RedirectAttributes redirect) {
        log.info("ContactController.deleteContact() - ENTRY - id={}", id);
        try {
            contactService.deleteById(id);
            redirect.addFlashAttribute("successMessage", "Contact deleted successfully.");
        } catch (Exception e) {
            log.error("ContactController.deleteContact() - EXCEPTION - id={} - {}", id, e.getMessage(), e);
            redirect.addFlashAttribute("errorMessage", "Unable to delete contact " + id + ".");
        }
        log.info("ContactController.deleteContact() - EXIT - redirect=/contacts");
        return "redirect:/contacts";
    }
}
