package com.personregistry.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.personregistry.dto.ContactDTO;
import com.personregistry.dto.PersonDTO;
import com.personregistry.entities.Address;
import com.personregistry.entities.Contact;
import com.personregistry.entities.Person;
import com.personregistry.repositories.ContactRepository;

@Service
public class ContactService {

    @Autowired
    private ContactRepository contactRepository;

    public Iterable<Contact> getContacts() {
        return this.contactRepository.findAll();
    }

    public List<Contact> addContacts(Person person, PersonDTO personDTO) {
        List<Contact> contacts = person.getContacts();
        if (contacts == null) {
            contacts = new ArrayList<>();
        }
        for (ContactDTO contactDTO : personDTO.getContacts()) {
            if (isValidContact(contactDTO.getContactType(), contactDTO.getContactInfo())) {
                Contact contact = new Contact();
                contact.setContactType(contactDTO.getContactType());
                contact.setContactInfo(contactDTO.getContactInfo());
                contact.setPerson(person);
                contactRepository.save(contact);
                contacts.add(contact);
            }
        }
        return contacts;
    }

    public void updateContacts(List<ContactDTO> contacts) {
        if (contacts != null && !contacts.isEmpty()) {
            for (ContactDTO updatedContact : contacts) {
                if (updatedContact.getContactId() != null) {
                    Optional<Contact> contactOptional = contactRepository.findById(updatedContact.getContactId());
                    contactOptional.ifPresent(contactToUpdate -> {
                        if (isValidContactType(updatedContact.getContactType())) {
                            contactToUpdate.setContactType(updatedContact.getContactType());
                        }
                        if (isValidContactInfo(updatedContact.getContactInfo())) {
                            contactToUpdate.setContactInfo(updatedContact.getContactInfo());
                        }
                        contactRepository.save(contactToUpdate);
                    });
                }
            }
        }
    }

    public void deleteAllContact(List<Contact> contacts) {
        if (contacts != null && !contacts.isEmpty()) {
            Iterator<Contact> iterator = contacts.iterator();
            while (iterator.hasNext()) {
                Contact contact = iterator.next();
                iterator.remove();
                contactRepository.delete(contact);
            }
        }
    }
    
    public void deleteContact(List<Contact> contacts, Integer contactId) {
        Optional<Contact> contactOptional = contactRepository.findById(contactId);
        contactOptional.ifPresent(contactToDelete -> {
            if(contacts != null && !contacts.isEmpty()) {
                contacts.remove(contactToDelete);
            }
            contactRepository.delete(contactToDelete);
        });
    }
    
    private boolean isValidContact(String contactType, String contactInfo) {
        return isValidContactType(contactType) && isValidContactInfo(contactInfo);
    }

    private boolean isValidContactType(String contactType) {
        return contactType != null && !contactType.isEmpty();
    }

    private boolean isValidContactInfo(String contactInfo) {
        return contactInfo != null && !contactInfo.isEmpty();
    }
}
