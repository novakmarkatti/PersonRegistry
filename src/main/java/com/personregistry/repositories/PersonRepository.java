package com.personregistry.repositories;

import org.springframework.data.repository.CrudRepository;
import com.personregistry.entities.Person;


public interface PersonRepository extends CrudRepository <Person, Integer> {

}