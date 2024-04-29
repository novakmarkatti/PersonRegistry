package com.personregistry.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.personregistry.dto.AddressDTO;
import com.personregistry.dto.PersonDTO;
import com.personregistry.entities.Address;
import com.personregistry.entities.Person;
import com.personregistry.enums.AddressType;
import com.personregistry.repositories.AddressRepository;

@Service
public class AddressService {

    @Autowired
    private AddressRepository addressRepository;

    public Iterable<Address> getAddresses() {
        return this.addressRepository.findAll();
    }

    public List<Address> setAddresses(Person person, PersonDTO personDTO) {
        List<Address> addresses = new ArrayList<>();
        List<Address> personsAddresses = person.getAddresses();
        if (personsAddresses != null && !personsAddresses.isEmpty()) { // Existing person
            for (AddressDTO addressDTO : personDTO.getAddresses()) {
                if (addressDTO.getAddressType() != AddressType.EMPTY) {
                    if (!hasAddressType(personsAddresses, addressDTO.getAddressType())) { // A person can only have 1 Address Type
                        addresses.add(addAddressToRepository(addressDTO, person));
                    }
                }
            }
        } else { // New person
            for (AddressDTO addressDTO : personDTO.getAddresses()) {
                if (addressDTO.getAddressType() != AddressType.EMPTY) {
                    addresses.add(addAddressToRepository(addressDTO, person));
                }
            }
        }
        return addresses;
    }

    private boolean hasAddressType(List<Address> addresses, AddressType addressType) {
        for (Address address : addresses) {
            if (address.getAddressType() == addressType) {
                return true;
            }
        }
        return false;
    }

    private Address addAddressToRepository(AddressDTO addressDTO, Person person) {
        Address address = new Address();
        address.setAddressType(addressDTO.getAddressType());
        address.setAddressInfo(addressDTO.getAddressInfo());
        address.setPerson(person);
        addressRepository.save(address);
        return address;
    }

}
