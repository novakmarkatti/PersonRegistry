package com.personregistry.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import com.personregistry.dto.ContactDTO;
import com.personregistry.dto.PersonDTO;
import com.personregistry.entities.Contact;
import com.personregistry.entities.Person;
import com.personregistry.repositories.ContactRepository;

@ExtendWith(MockitoExtension.class)
public class ContactServiceTest {

    @Mock
    private ContactRepository contactRepository;

    @InjectMocks
    private ContactService contactService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void ContactService_AddContacts_ReturnsAddedContacts() {
        // Arrange
        Person person = Person.builder().build();
        PersonDTO personDTO = PersonDTO.builder().build();
        List<ContactDTO> contactDTOs = new ArrayList<>();
        contactDTOs.add(ContactDTO.builder().contactType("email").contactInfo("test@email.com").build());
        personDTO.setContacts(contactDTOs);
        when(contactRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        List<Contact> addedContacts = contactService.addContacts(person, personDTO);
        Contact addedContact = addedContacts.get(0);

        // Assert
        Assertions.assertThat(addedContacts).isNotNull();
        Assertions.assertThat(addedContacts).hasSize(1);
        Assertions.assertThat(addedContact.getContactType()).isEqualTo("email");
        Assertions.assertThat(addedContact.getContactInfo()).isEqualTo("test@email.com");
    }

    @Test
    public void ContactService_UpdateContacts_UpdatesContacts() {
        // Arrange
        List<ContactDTO> contacts = new ArrayList<>();
        contacts.add(ContactDTO.builder().contactId(1).contactType("email").contactInfo("test@email.com").build());
        when(contactRepository.findById(any())).thenReturn(Optional.of(new Contact()));
        when(contactRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        contactService.updateContacts(contacts);

        // Assert
        verify(contactRepository, times(1)).findById(any());
        verify(contactRepository, times(1)).save(any());
    }

    @Test
    public void ContactService_GetContacts_ReturnsAllContacts() {
        // Arrange
        List<Contact> mockContacts = new ArrayList<>();
        mockContacts.add(Contact.builder().contactType("email").contactInfo("test@email.com").build());
        mockContacts.add(Contact.builder().contactType("phone").contactInfo("123456789").build());
        when(contactRepository.findAll()).thenReturn(mockContacts);

        // Act
        Iterable<Contact> contacts = contactService.getContacts();

        // Assert
        Assertions.assertThat(contacts).isNotNull();
        Assertions.assertThat(contacts).hasSize(2);
        Assertions.assertThat(contacts).containsExactlyElementsOf(mockContacts);
    }

    @Test
    public void ContactService_DeleteAllContact_RemovesAllContacts() {
        // Arrange
        List<Contact> contacts = new ArrayList<>();
        contacts.add(Contact.builder().contactType("email").contactInfo("test@email.com").build());
        contacts.add(Contact.builder().contactType("phone").contactInfo("123456789").build());

        // Act
        contactService.deleteAllContact(contacts);

        // Assert
        Assertions.assertThat(contacts).isEmpty();
        verify(contactRepository, times(2)).delete(any());
    }

    @Test
    public void ContactService_DeleteContact_RemovesContactById() {
        // Arrange
        List<Contact> contacts = new ArrayList<>();
        Contact contact1 = Contact.builder().contactId(1).contactType("email").contactInfo("test@email.com").build();
        Contact contact2 = Contact.builder().contactId(2).contactType("phone").contactInfo("123456789").build();
        contacts.add(contact1);
        contacts.add(contact2);
        when(contactRepository.findById(1)).thenReturn(Optional.of(contact1));

        // Act
        contactService.deleteContact(contacts, 1);

        // Assert
        Assertions.assertThat(contacts).hasSize(1);
        Assertions.assertThat(contacts).containsExactly(contact2);
        verify(contactRepository, times(1)).delete(contact1);
    }

    @Test
    public void ContactService_DeleteContact_ContactNotFound_DoesNotRemoveContact() {
        // Arrange
        List<Contact> contacts = new ArrayList<>();
        Contact contact1 = Contact.builder().contactId(1).contactType("email").contactInfo("test@email.com").build();
        contacts.add(contact1);
        when(contactRepository.findById(2)).thenReturn(Optional.empty());

        // Act
        contactService.deleteContact(contacts, 2);

        // Assert
        Assertions.assertThat(contacts).hasSize(1);
        verify(contactRepository, never()).delete(any());
    }
}
