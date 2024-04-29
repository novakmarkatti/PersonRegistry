package com.personregistry.controllers;

import java.lang.Iterable;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.util.Arrays;
import org.springframework.web.bind.annotation.RestController;
import com.personregistry.dto.ContactDTO;
import com.personregistry.dto.PersonDTO;
import com.personregistry.entities.Address;
import com.personregistry.entities.Contact;
import com.personregistry.entities.Person;
import com.personregistry.enums.AddressType;
import com.personregistry.repositories.ContactRepository;
import com.personregistry.repositories.PersonRepository;
import com.personregistry.service.AddressService;
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
    private final ContactRepository contactRepository;
    private final PersonRepository personRepository;

    public PersonRegistryController(final AddressService addressService, final ContactRepository contactRepository, final PersonRepository personRepository) {
        this.addressService = addressService;
        this.contactRepository = contactRepository;
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
		return this.contactRepository.findAll();
	}

    @GetMapping("/persons")
	public Iterable<Person> listPersons() {
		return this.personRepository.findAll();
	}

	@GetMapping("/persons/search")
	public List<Person> getPersons(@RequestParam(required = false) Integer personId, @RequestParam(required = false) String personName) {
		List<Person> personList = new ArrayList<>();
		if (personId != null && personName != null) {
			return this.personRepository.findByPersonIdAndPersonName(personId, personName);
		} else if (personId != null) {
			Optional<Person> personOptional = this.personRepository.findById(personId);
			if(!personOptional.isPresent()) {
				return personList;
			}
			personList.add(personOptional.get());
			return personList;
		} else if (personName != null) {
			return null; // this.personRepository.findByPersonName(personName);
		}
		return personList;
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

        savedPerson.setAddresses(addressService.setAddresses(savedPerson, personDTO));

		List<Contact> contacts = new ArrayList<>();
		for (ContactDTO contactDTO : personDTO.getContacts()) {
			Contact contact = new Contact();
			contact.setContactType(contactDTO.getContactType());
			contact.setContactInfo(contactDTO.getContactInfo());
			contact.setPerson(savedPerson);
			contactRepository.save(contact);
			contacts.add(contact);
		}
		savedPerson.setContacts(contacts);

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

		List<ContactDTO> updatedContacts = updatedPersonDTO.getContacts();
		if (updatedContacts != null && !updatedContacts.isEmpty()) {
			for (ContactDTO updatedContact : updatedContacts) {
				Integer contactId = updatedContact.getContactId();
				String contactType = updatedContact.getContactType();
				String contactInfo = updatedContact.getContactInfo();
				if (contactId != null) {
					Optional<Contact> contactOptional = contactRepository.findById(contactId);
					if (contactOptional.isPresent()) {
						Contact contactToUpdate = contactOptional.get();
						if (contactType != null && !contactType.isEmpty()) {
							contactToUpdate.setContactType(contactType);
						}
						if (contactInfo != null && !contactInfo.isEmpty()) {
							contactToUpdate.setContactInfo(contactInfo);
						}
						contactRepository.save(contactToUpdate);
					}
				} 
			}
		}

		Person updatedPerson = personRepository.save(personToUpdate);
		return ResponseEntity.ok().body(updatedPerson);
	}

	@DeleteMapping("/persons/{id}")
	public ResponseEntity<Person> deletePerson(@PathVariable("id") Integer id,
											   @RequestParam(value = "deletePersonData", required = false) boolean deletePersonData,
											   @RequestParam(value = "deleteAllContacts", required = false) boolean deleteAllContacts,
											   @RequestParam(value = "contactId", required = false) Integer contactId) {
		Optional<Person> personToDeleteOptional = personRepository.findById(id);
		if (!personToDeleteOptional.isPresent()) {
			return ResponseEntity.notFound().build();
		}

	    Person personToDelete = personToDeleteOptional.get();
		if (deletePersonData) {
			List<Contact> contactsToDelete = contactRepository.findAllByPersonPersonId(personToDelete.getPersonId());
			contactRepository.deleteAll(contactsToDelete);
	
			personRepository.delete(personToDelete);
			return ResponseEntity.ok().body(personToDelete);
		}
	
		if (deleteAllContacts) {
			List<Contact> contactsToDelete = contactRepository.findAllByPersonPersonId(personToDelete.getPersonId());
			contactRepository.deleteAll(contactsToDelete);
			return ResponseEntity.ok().body(personToDelete);
		}
	
		if (contactId != null) {
			Optional<Contact> contactOptional = contactRepository.findById(contactId);
			if (contactOptional.isPresent()) {
				Contact contactToDelete = contactOptional.get();
				personToDelete.getContacts().remove(contactToDelete);
				contactRepository.delete(contactToDelete);
				return ResponseEntity.ok().body(personToDelete);
			}
		}
	
		return ResponseEntity.badRequest().build();
	}
	
}