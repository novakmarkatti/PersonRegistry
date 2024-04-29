package com.personregistry.repositories;

import org.springframework.data.repository.CrudRepository;
import com.personregistry.entities.Person;
import java.util.List;

public interface PersonRepository extends CrudRepository <Person, Integer> {

    Person findByPersonName(String personName);
    List<Person> findByPersonIdAndPersonName(Integer PersonId, String personName);

}