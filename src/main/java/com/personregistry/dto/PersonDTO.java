package com.personregistry.dto;

import java.util.List;

public class PersonDTO {
    
    private String personName;
    private List<AddressDTO> addresses;
    private List<ContactDTO> contacts;


    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public List<AddressDTO> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<AddressDTO> addresses) {
        this.addresses = addresses;
    }

    public List<ContactDTO> getContacts() {
        return contacts;
    }

    public void setContacts(List<ContactDTO> contacts) {
        this.contacts = contacts;
    }
}