package com.personregistry.repositories;

import org.springframework.data.repository.CrudRepository;
import com.personregistry.entities.Address;


public interface AddressRepository extends CrudRepository <Address, Integer> {

}