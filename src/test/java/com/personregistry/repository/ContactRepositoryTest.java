package com.personregistry.repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import com.personregistry.entities.Contact;
import com.personregistry.repositories.ContactRepository;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class ContactRepositoryTest {

    @Autowired
    private ContactRepository contactRepository;

    @Test
    public void ContactRepository_Save_ReturnsSavedContact() {
        // Arrange
        Contact contact = Contact.builder().contactType("email").contactInfo("test@email.com").build();

        // Act
        Contact savedContact = contactRepository.save(contact);

        // Assert
        Assertions.assertThat(savedContact).isNotNull();
        Assertions.assertThat(savedContact.getContactId()).isGreaterThan(0);
        Assertions.assertThat(savedContact.getContactType()).isEqualTo("email");
        Assertions.assertThat(savedContact.getContactInfo()).isEqualTo("test@email.com");
    }

    @Test
    public void ContactRepository_FindById_ReturnsSavedContact() {
        // Arrange
        Contact contact = Contact.builder().contactType("email").contactInfo("test@email.com").build();
        contactRepository.save(contact);

        // Act
        Contact foundContact = contactRepository.findById(contact.getContactId()).orElse(null);

        // Assert
        Assertions.assertThat(foundContact).isNotNull();
        Assertions.assertThat(foundContact.getContactId()).isEqualTo(contact.getContactId());
        Assertions.assertThat(foundContact.getContactType()).isEqualTo(contact.getContactType());
        Assertions.assertThat(foundContact.getContactInfo()).isEqualTo(contact.getContactInfo());
    }

    @Test
    public void ContactRepository_SaveAllFindAll_ReturnsSavedContacts() {
        // Arrange
        List<Contact> contactsBeforeSave = StreamSupport.stream(contactRepository.findAll().spliterator(), false).collect(Collectors.toList());
        Contact contactA = Contact.builder().contactType("email").contactInfo("test@email.com").build();
        Contact contactB = Contact.builder().contactType("phone").contactInfo("123456").build();

        // Act
        contactRepository.saveAll(List.of(contactA, contactB));
        List<Contact> contactsAfterSave = StreamSupport.stream(contactRepository.findAll().spliterator(), false).collect(Collectors.toList());

        // Assert
        Assertions.assertThat(contactsAfterSave).isNotNull();
        Assertions.assertThat(contactsAfterSave.size()).isEqualTo(contactsBeforeSave.size() + 2);
    }

    
    @Test
    public void ContactRepository_Delete_RemovesContactFromDatabase() {
        // Arrange
        Contact contactA = Contact.builder().contactType("email").contactInfo("test1@email.com").build();
        Contact contactB = Contact.builder().contactType("phone").contactInfo("123456").build();
        contactRepository.save(contactA);
        contactRepository.save(contactB);
    
        // Act
        contactRepository.deleteById(contactA.getContactId());
        contactRepository.delete(contactB);
        Optional<Contact> deletedContactA = contactRepository.findById(contactA.getContactId());
        Optional<Contact> deletedContactB = contactRepository.findById(contactB.getContactId());

        // Assert
        Assertions.assertThat(deletedContactA).isEmpty();
        Assertions.assertThat(deletedContactB).isEmpty();
    }
    
}
