package edu.gcu.cst323;

import edu.gcu.cst323.model.Contact;
import edu.gcu.cst323.service.ContactService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * Automated tests run by `mvn package` locally and by the build stage of the CI/CD pipeline.
 * Covers full CRUD through the service layer, the duplicate-email business rule, the
 * controller pages, and that the LoggingInterceptor logs every layer.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(OutputCaptureExtension.class)
class ContactManagerIntegrationTest {

    @Autowired
    private ContactService contactService;

    @Autowired
    private MockMvc mvc;

    @Test
    void crudLifecycle() {
        Contact created = contactService.save(
                new Contact("Katherine", "Johnson", "katherine.johnson@example.com", "512-555-0104", "NASA Langley"));
        assertThat(created.getContactId()).isNotNull();
        assertThat(contactService.findById(created.getContactId())).isPresent();

        created.setCompany("NASA");
        contactService.save(created);
        assertThat(contactService.findById(created.getContactId()).orElseThrow().getCompany()).isEqualTo("NASA");

        contactService.deleteById(created.getContactId());
        assertThat(contactService.findById(created.getContactId())).isEmpty();
    }

    @Test
    void duplicateEmailRule() {
        Contact ada = contactService.save(new Contact("Ada", "Lovelace", "ada@example.com", null, null));
        assertThat(contactService.emailInUseByAnotherContact("ADA@example.com", null)).isTrue();
        assertThat(contactService.emailInUseByAnotherContact("ada@example.com", ada.getContactId())).isFalse();
        contactService.deleteById(ada.getContactId());
    }

    @Test
    void pagesRender() throws Exception {
        mvc.perform(get("/")).andExpect(status().isOk()).andExpect(view().name("index"));
        mvc.perform(get("/contacts")).andExpect(status().isOk()).andExpect(view().name("contact-list"));
        mvc.perform(get("/contacts/new")).andExpect(status().isOk()).andExpect(view().name("contact-form"));
        mvc.perform(get("/contacts/999999")).andExpect(status().is3xxRedirection());
        mvc.perform(post("/contacts/save").param("firstName", "").param("lastName", "X").param("email", "bad"))
                .andExpect(status().isOk()).andExpect(view().name("contact-form"));
    }

    @Test
    void interceptorLogsEveryLayer(CapturedOutput output) throws Exception {
        mvc.perform(get("/contacts")).andExpect(status().isOk());
        assertThat(output.getOut())
                .contains("[CONTROLLER] ContactController.listContacts() - ENTRY")
                .contains("[CONTROLLER] ContactController.listContacts() - EXIT")
                .contains("[SERVICE] ContactServiceImpl.findAll() - ENTRY")
                .contains("[SERVICE] ContactServiceImpl.findAll() - EXIT")
                .contains("[DATA] ContactRepository.findAllByOrderByLastNameAscFirstNameAsc() - ENTRY")
                .contains("[DATA] ContactRepository.findAllByOrderByLastNameAscFirstNameAsc() - EXIT");
    }

    @Test
    void interceptorLogsExceptions(CapturedOutput output) {
        try {
            contactService.findById(null);
        } catch (Exception expected) {
            // a null id is rejected by Spring Data; the interceptor must log it at ERROR
        }
        assertThat(output.getOut()).contains("ERROR").contains("[SERVICE] ContactServiceImpl.findById() - EXCEPTION");
    }
}
