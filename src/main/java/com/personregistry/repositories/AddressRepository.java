package com.personregistry.repositories;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import com.personregistry.entities.Address;

public interface AddressRepository extends CrudRepository <Address, Integer> {

    List<Address> findAllByPersonPersonId(Integer personId);
    
}