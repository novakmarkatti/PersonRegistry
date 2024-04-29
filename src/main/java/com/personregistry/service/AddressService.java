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

    public List<Address> addAddresses(Person person, PersonDTO personDTO) {
        List<Address> addresses = person.getAddresses();
        if (addresses == null) {
            addresses = new ArrayList<>();
        }
        for (AddressDTO addressDTO : personDTO.getAddresses()) {
            if (isValidAddressType(addresses, addressDTO.getAddressType()) && isValidAddressInfo(addressDTO.getAddressInfo())) {
                addresses.add(addAddressToRepository(addressDTO, person));
            }
        }
        return addresses;
    }

    private boolean isValidAddressType(List<Address> addresses, AddressType addressType) {
        return addressType != AddressType.EMPTY && !hasAddressType(addresses, addressType);
    }

    private boolean hasAddressType(List<Address> addresses, AddressType addressType) {
        return addresses.stream().anyMatch(address -> address.getAddressType() == addressType);
    }

    private boolean isValidAddressInfo(String addressInfo) {
        return addressInfo != null && !addressInfo.isEmpty();
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
