package com.personregistry.controllers;

import java.lang.Iterable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ArrayList;
import java.util.Arrays;
import org.springframework.web.bind.annotation.RestController;
import com.personregistry.entities.Address;
import com.personregistry.entities.Contact;
import com.personregistry.entities.Person;
import com.personregistry.enums.AddressType;
import com.personregistry.repositories.AddressRepository;
import com.personregistry.repositories.ContactRepository;
import com.personregistry.repositories.PersonRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.HttpStatus;
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

    private final AddressRepository addressRepository;
    private final ContactRepository contactRepository;
    private final PersonRepository personRepository;

    public PersonRegistryController(final AddressRepository addressRepository, final ContactRepository contactRepository, final PersonRepository personRepository) {
        this.addressRepository = addressRepository;
        this.contactRepository = contactRepository;
        this.personRepository = personRepository;
      }

	@GetMapping("/addresses")
	public Iterable<Address> getAddresses() {
		return this.addressRepository.findAll();
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
			return this.personRepository.findByPersonName(personName);
		}
		return personList;
	}

	@PostMapping("/persons")
	public Person addPerson(@RequestBody Person person) {
    	return this.personRepository.save(person);
	}

	@PutMapping("/persons/{id}")
	public Person updatePerson(@PathVariable("id") Integer id, @RequestBody Person person) {
      	Optional<Person> personToUpdateOptional = this.personRepository.findById(id);
		if(!personToUpdateOptional.isPresent()) {
			return null;
		}

      	Person personToUpdate = personToUpdateOptional.get();

		if(person.getPersonName() != null) {
			personToUpdate.setPersonName(person.getPersonName());
		}

		Person updatedPerson = personRepository.save(personToUpdate);
		return updatedPerson;
	}

	@DeleteMapping("/persons/{id}")
	public Person deletePerson(@PathVariable("id") Integer id) {
      	Optional<Person> personToDeleteOptional = this.personRepository.findById(id);
		if(!personToDeleteOptional.isPresent()) {
			return null;
		}

      	Person personToDelete = personToDeleteOptional.get();

		List<Address> addressesToDelete = addressRepository.findAllByPersonPersonId(personToDelete.getPersonId());
		addressRepository.deleteAll(addressesToDelete);

		List<Contact> contactsToDelete = contactRepository.findAllByPersonPersonId(personToDelete.getPersonId());
		contactRepository.deleteAll(contactsToDelete);

		personRepository.delete(personToDelete);
		return personToDelete;
	}

}