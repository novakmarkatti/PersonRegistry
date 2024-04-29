package com.personregistry.dto;

import com.personregistry.enums.AddressType;

public class AddressDTO {

    private AddressType addressType;
    private String addressInfo;


    public AddressType getAddressType() {
        return addressType;
    }

    public void setAddressType(AddressType addressType) {
        this.addressType = addressType;
    }

    public String getAddressInfo() {
        return addressInfo;
    }

    public void setAddressInfo(String addressInfo) {
        this.addressInfo = addressInfo;
    }
}
