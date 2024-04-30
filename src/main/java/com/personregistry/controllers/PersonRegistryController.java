package com.personregistry.controllers;

import java.lang.Iterable;
import java.util.List;
import java.util.Optional;
import java.util.Arrays;
import java.util.Collections;
import org.springframework.web.bind.annotation.RestController;

import com.personregistry.dto.PersonDTO;
import com.personregistry.entities.Address;
import com.personregistry.entities.Contact;
import com.personregistry.entities.Person;
import com.personregistry.enums.AddressType;
import com.personregistry.repositories.PersonRepository;
import com.personregistry.service.AddressService;
import com.personregistry.service.ContactService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/personregistry")
public class PersonRegistryController {

    private final AddressService addressService;
	private final ContactService contactService;
    private final PersonRepository personRepository;

    public PersonRegistryController(final AddressService addressService, final ContactService contactService, final PersonRepository personRepository) {
        this.addressService = addressService;
        this.contactService = contactService;
        this.personRepository = personRepository;
      }

    @GetMapping("/addresses")
    public Iterable<Address> getAddresses() {
        return addressService.getAddresses();
    }

    @GetMapping("/addresstypes")
	public List<AddressType> getAddressTypes() {
		return Arrays.asList(AddressType.values());
	}

    @GetMapping("/contacts")
	public Iterable<Contact> getContacts() {
		return contactService.getContacts();
	}

    @GetMapping("/persons")
	public Iterable<Person> listPersons() {
		return this.personRepository.findAll();
	}

    @PostMapping("/persons")
    public ResponseEntity<Person> addPerson(@RequestBody PersonDTO personDTO) {
        Person existingPerson = personRepository.findByPersonName(personDTO.getPersonName());
        Person savedPerson;
        if (existingPerson != null) {
            savedPerson = existingPerson;
        } else {
            Person person = new Person();
            person.setPersonName(personDTO.getPersonName());
            person.setAddresses(null);
            savedPerson = personRepository.save(person);
        }
        savedPerson.setAddresses(addressService.addAddresses(savedPerson, personDTO));
        savedPerson.setContacts(contactService.addContacts(savedPerson, personDTO));
        return ResponseEntity.ok().body(personRepository.save(savedPerson));
    }

    @PutMapping("/persons/{id}")
    public ResponseEntity<Person> updatePerson(@PathVariable("id") Integer id, @RequestBody PersonDTO updatedPersonDTO) {
        Optional<Person> personToUpdateOptional = personRepository.findById(id);
        if (!personToUpdateOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Person personToUpdate = personToUpdateOptional.get();
        if (updatedPersonDTO.getPersonName() != null && !updatedPersonDTO.getPersonName().isEmpty()) {
            personToUpdate.setPersonName(updatedPersonDTO.getPersonName());
        }

        addressService.updateAddresses(personToUpdate.getAddresses(), updatedPersonDTO.getAddresses());
        contactService.updateContacts(updatedPersonDTO.getContacts());
        return ResponseEntity.ok().body(personRepository.save(personToUpdate));
    }

	@DeleteMapping("/persons/{id}")
	public ResponseEntity<Person> deletePerson(@PathVariable("id") Integer id,
											   @RequestParam(value = "deletePersonData", required = false) boolean deletePersonData,
											   @RequestParam(value = "deleteAllContacts", required = false) boolean deleteAllContacts,
											   @RequestParam(value = "contactId", required = false) Integer contactId,
											   @RequestParam(value = "addressType", required = false) AddressType addressType) {
		Optional<Person> personToDeleteOptional = personRepository.findById(id);
		if (!personToDeleteOptional.isPresent()) {
			return ResponseEntity.notFound().build();
		}
	
		Person personToDelete = personToDeleteOptional.get();
		if (deletePersonData) {
    		addressService.deleteAllAddress(personToDelete.getAddresses());
    		contactService.deleteAllContact(personToDelete.getContacts());
			personRepository.delete(personToDelete);
			return ResponseEntity.ok().body(personToDelete);
		}

		int count = 0;
		if (deleteAllContacts) {
			contactService.deleteAllContact(personToDelete.getContacts());
			count++;
		} else if (contactId != null) {
			contactService.deleteContact(personToDelete.getContacts(), contactId);
			count++;
		}
		if (addressType != null && addressType != AddressType.EMPTY) {
			addressService.deleteAddress(personToDelete.getAddresses(), addressType);
			count++;
		}

		if(count > 0) {
			personRepository.save(personToDelete);
			return ResponseEntity.ok().body(personToDelete);
		} else {
			return ResponseEntity.badRequest().build();
		}
	}

	@GetMapping("/persons/search")
	public ResponseEntity<List<Person>> searchPersons(@RequestParam(required = false) Integer personId,
													  @RequestParam(required = false) String personName,
													  @RequestParam(value = "addressType", required = false) AddressType addressType,
													  @RequestParam(required = false) String contactType,
													  @RequestParam(required = false) String contactInfo) {
		List<Person> personList = Collections.emptyList();
		if (personId != null) {
			Optional<Person> personOptional = personRepository.findById(personId);
			personList = !personOptional.isPresent() ? Collections.emptyList() : Collections.singletonList(personOptional.get());
		} else if (personName != null) {
			Person person = personRepository.findByPersonName(personName);
			personList = person != null ? Collections.singletonList(person) : Collections.emptyList();
		} else if (addressType != null && addressType != AddressType.EMPTY) {
			personList = personRepository.findByAddressesAddressType(addressType);
		} else if (contactType != null || contactInfo != null) {
			personList = personRepository.findByContactsContactTypeOrContactsContactInfo(contactType, contactInfo);
		}
		return personList.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(personList);
	}
	
}