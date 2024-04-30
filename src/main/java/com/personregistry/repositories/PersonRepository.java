package com.personregistry.repositories;

import org.springframework.data.repository.CrudRepository;
import com.personregistry.entities.Person;
import com.personregistry.enums.AddressType;

import java.util.List;

public interface PersonRepository extends CrudRepository<Person, Integer> {

    Person findByPersonName(String personName);
    List<Person> findByPersonIdAndPersonName(Integer personId, String personName);
    List<Person> findByPersonId(Integer personId);
    List<Person> findByAddressesAddressType(AddressType addressType);
    List<Person> findByContactsContactType(String contactType);
    List<Person> findByContactsContactInfo(String contactInfo);
    List<Person> findByAddressesAddressTypeAndContactsContactType(AddressType addressType, String contactType);
    List<Person> findByAddressesAddressTypeAndContactsContactInfo(AddressType addressType, String contactInfo);
    List<Person> findByContactsContactTypeAndContactsContactInfo(String contactType, String contactInfo);
    List<Person> findByAddressesAddressTypeAndContactsContactTypeAndContactsContactInfo(AddressType addressType, String contactType, String contactInfo);
    List<Person> findByPersonNameContainingIgnoreCase(String personName);
    List<Person> findByContactsContactTypeOrContactsContactInfo(String contactType, String contactInfo);

}