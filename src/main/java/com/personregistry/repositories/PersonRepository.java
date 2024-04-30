package com.personregistry.repositories;

import org.springframework.data.repository.CrudRepository;
import com.personregistry.entities.Person;
import com.personregistry.enums.AddressType;
import java.util.List;

public interface PersonRepository extends CrudRepository<Person, Integer> {

    Person findByPersonName(String personName);
    Person findByPersonIdAndPersonName(Integer personId, String personName);
    List<Person> findByAddressesAddressType(AddressType addressType);
    List<Person> findByContactsContactTypeOrContactsContactInfo(String contactType, String contactInfo);

}