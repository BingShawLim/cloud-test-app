package edu.gcu.cst323.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * Contact entity. Maps to the CONTACT table in the cst323 MySQL schema.
 */
@Entity
@Table(name = "contact")
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CONTACT_ID")
    private Long contactId;

    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name must be 50 characters or fewer")
    @Column(name = "FIRST_NAME", nullable = false, length = 50)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name must be 50 characters or fewer")
    @Column(name = "LAST_NAME", nullable = false, length = 50)
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid address")
    @Size(max = 100, message = "Email must be 100 characters or fewer")
    @Column(name = "EMAIL", nullable = false, length = 100, unique = true)
    private String email;

    @Size(max = 20, message = "Phone must be 20 characters or fewer")
    @Column(name = "PHONE", length = 20)
    private String phone;

    @Size(max = 100, message = "Company must be 100 characters or fewer")
    @Column(name = "COMPANY", length = 100)
    private String company;

    @Column(name = "CREATED_DATE", nullable = false)
    private LocalDateTime createdDate = LocalDateTime.now();

    public Contact() {
    }

    public Contact(String firstName, String lastName, String email, String phone, String company) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.company = company;
        this.createdDate = LocalDateTime.now();
    }

    public Long getContactId() { return contactId; }
    public void setContactId(Long contactId) { this.contactId = contactId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }

    public String getFullName() { return firstName + " " + lastName; }

    @Override
    public String toString() {
        return "Contact{contactId=" + contactId
                + ", firstName='" + firstName + '\''
                + ", lastName='" + lastName + '\''
                + ", email='" + email + '\''
                + ", phone='" + phone + '\''
                + ", company='" + company + '\''
                + '}';
    }
}
