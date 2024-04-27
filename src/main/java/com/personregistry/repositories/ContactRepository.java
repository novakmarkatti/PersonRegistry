package com.personregistry.repositories;

import org.springframework.data.repository.CrudRepository;
import com.personregistry.entities.Contact;

public interface ContactRepository extends CrudRepository <Contact, Integer> {

}