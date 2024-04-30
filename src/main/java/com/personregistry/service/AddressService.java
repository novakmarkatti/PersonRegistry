package com.personregistry.service;

import java.util.ArrayList;
import java.util.Iterator;
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

    public void updateAddresses(List<Address> addresses, List<AddressDTO> updatedAddresses) {
        if (addresses != null && !addresses.isEmpty()) {
            for (Address address : addresses) {
                for (AddressDTO updatedAddress : updatedAddresses) {
                    if (address.getAddressType().equals(updatedAddress.getAddressType()) && !isAddressTypeEmpty(updatedAddress.getAddressType())  
                        && isValidAddressInfo(updatedAddress.getAddressInfo()) ) {
                        address.setAddressInfo(updatedAddress.getAddressInfo());
                    }
                }
            }
        }
    }
    
    private boolean isValidAddressType(List<Address> addresses, AddressType addressType) {
        return !isAddressTypeEmpty(addressType) && !hasAddressType(addresses, addressType);
    }

    private boolean hasAddressType(List<Address> addresses, AddressType addressType) {
        return addresses.stream().anyMatch(address -> address.getAddressType() == addressType);
    }

    private boolean isAddressTypeEmpty(AddressType addressType) {
        return addressType == AddressType.EMPTY;
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

    public void deleteAllAddress(List<Address> addresses) {
        if (addresses != null && !addresses.isEmpty()) {
            Iterator<Address> iterator = addresses.iterator();
            while (iterator.hasNext()) {
                Address address = iterator.next();
                iterator.remove();
                addressRepository.delete(address);
            }
        }
    }

    public void deleteAddress(List<Address> addresses, AddressType addressType) {
        if (addresses != null && !addresses.isEmpty()) {
            Address addressToDelete = addresses.stream()
                    .filter(address -> address.getAddressType() == addressType)
                    .findFirst()
                    .orElse(null);
    
            if (addressToDelete != null) {
                addresses.remove(addressToDelete);
                addressRepository.delete(addressToDelete);
            }
        }
    }

}
