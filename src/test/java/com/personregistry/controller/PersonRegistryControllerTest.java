package com.personregistry.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import java.util.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import com.personregistry.controllers.PersonRegistryController;
import com.personregistry.dto.AddressDTO;
import com.personregistry.dto.ContactDTO;
import com.personregistry.dto.PersonDTO;
import com.personregistry.entities.Address;
import com.personregistry.entities.Contact;
import com.personregistry.entities.Person;
import com.personregistry.enums.AddressType;
import com.personregistry.repositories.PersonRepository;
import com.personregistry.service.AddressService;
import com.personregistry.service.ContactService;

@ExtendWith(MockitoExtension.class)
public class PersonRegistryControllerTest {

    @Mock
    private AddressService addressService;

    @Mock
    private ContactService contactService;

    @Mock
    private PersonRepository personRepository;

    @InjectMocks
    private PersonRegistryController personRegistryController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void PersonRegistryController_addPerson_ReturnsSavedPerson() {
        // Arrange
        PersonDTO personDTO = PersonDTO.builder().personName("personA").build();
        when(personRepository.findByPersonName(anyString())).thenReturn(null);
        when(personRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        ResponseEntity<Person> response = personRegistryController.addPerson(personDTO);

        // Assert
        Assertions.assertThat(response.getStatusCodeValue()).isEqualTo(200);
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody().getPersonName()).isEqualTo("personA");
        verify(addressService, times(1)).addAddresses(any(), any());
        verify(contactService, times(1)).addContacts(any(), any());
    }

    @Test
    public void updatePerson_ReturnsUpdatedPerson() {
        // Arrange
        Person existingPerson = new Person();
        existingPerson.setPersonName("ASD");
        existingPerson.setAddresses(Collections.singletonList(Address.builder().addressType(AddressType.TEMPORARY).addressInfo("Old Address").build()));
        existingPerson.setContacts(Collections.singletonList(Contact.builder().contactType("email").contactInfo("old@email.com").build()));

        PersonDTO updatedPersonDTO = PersonDTO.builder()
                .personName("Updated Name")
                .addresses(Collections.singletonList(AddressDTO.builder().addressType(AddressType.PERMANENT).addressInfo("New Address").build()))
                .contacts(Collections.singletonList(ContactDTO.builder().contactType("phone").contactInfo("123456789").build()))
                .build();
        Optional<Person> existingPersonOptional = Optional.of(existingPerson);

        when(personRepository.findById(anyInt())).thenReturn(existingPersonOptional);
        when(personRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        ResponseEntity<Person> response = personRegistryController.updatePerson(1, updatedPersonDTO);

        // Assert
        Assertions.assertThat(response.getStatusCodeValue()).isEqualTo(200);
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody().getPersonName()).isEqualTo("Updated Name");
        verify(addressService, times(1)).updateAddresses(anyList(), anyList());
        verify(contactService, times(1)).updateContacts(anyList());
    }

}
