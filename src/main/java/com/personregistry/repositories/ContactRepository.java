package com.personregistry.repositories;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import com.personregistry.entities.Contact;

public interface ContactRepository extends CrudRepository <Contact, Integer> {

    List<Contact> findAllByPersonPersonId(Integer personId);
    
}