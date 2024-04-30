package com.personregistry.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import com.personregistry.dto.AddressDTO;
import com.personregistry.dto.PersonDTO;
import com.personregistry.entities.Address;
import com.personregistry.entities.Person;
import com.personregistry.enums.AddressType;
import com.personregistry.repositories.AddressRepository;

@ExtendWith(MockitoExtension.class)
public class AddressServiceTest {
 
    @Mock
    private AddressRepository addressRepository;

    @InjectMocks
    private AddressService addressService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void AddressService_AddAddresses_ReturnsAddedAddresses() {
        // Arrange
        Person person = Person.builder().build();
        PersonDTO personDTO = PersonDTO.builder().build();
        List<AddressDTO> addressDTOs = new ArrayList<>();
        addressDTOs.add(AddressDTO.builder().addressType(AddressType.TEMPORARY).addressInfo("TEMPORARY address").build());
        personDTO.setAddresses(addressDTOs);
        when(addressRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        List<Address> addedAddresses = addressService.addAddresses(person, personDTO);
        Address addedAddress = addedAddresses.get(0);

        // Assert
        Assertions.assertThat(addedAddresses).isNotNull();
        Assertions.assertThat(addedAddresses).hasSize(1);
        Assertions.assertThat(addedAddress.getAddressType()).isEqualTo(AddressType.TEMPORARY);
        Assertions.assertThat(addedAddress.getAddressInfo()).isEqualTo("TEMPORARY address");
    }

    @Test
    public void AddressService_UpdateAddresses_UpdatesAddressInfo() {
        // Arrange
        List<Address> addresses = new ArrayList<>();
        addresses.add(Address.builder().addressType(AddressType.TEMPORARY).addressInfo("person's temp address").build());
        List<AddressDTO> updatedAddresses = new ArrayList<>();
        updatedAddresses.add(AddressDTO.builder().addressType(AddressType.TEMPORARY).addressInfo("TEMPORARY address").build());

        // Act
        addressService.updateAddresses(addresses, updatedAddresses);

        // Assert
        Assertions.assertThat(addresses).hasSize(1);
        Assertions.assertThat(addresses.get(0).getAddressInfo()).isEqualTo("TEMPORARY address");
    }

    @Test
    public void AddressService_GetAddresses_ReturnsAllAddresses() {
        // Arrange
        List<Address> mockAddresses = new ArrayList<>();
        mockAddresses.add(Address.builder().addressType(AddressType.TEMPORARY).addressInfo("TEMPORARY address").build());
        mockAddresses.add(Address.builder().addressType(AddressType.PERMANENT).addressInfo("PERMANENT address").build());
        when(addressRepository.findAll()).thenReturn(mockAddresses);

        // Act
        Iterable<Address> addresses = addressService.getAddresses();

        // Assert
        Assertions.assertThat(addresses).isNotNull();
        Assertions.assertThat(addresses).hasSize(2);
        Assertions.assertThat(addresses).containsExactlyElementsOf(mockAddresses);
    }

    @Test
    public void AddressService_DeleteAllAddress_RemovesAllAddresses() {
        // Arrange
        List<Address> addresses = new ArrayList<>();
        addresses.add(Address.builder().addressType(AddressType.TEMPORARY).addressInfo("TEMPORARY address").build());
        addresses.add(Address.builder().addressType(AddressType.PERMANENT).addressInfo("PERMANENT address").build());

        // Act
        addressService.deleteAllAddress(addresses);

        // Assert
        Assertions.assertThat(addresses).isEmpty();
        verify(addressRepository, times(2)).delete(any());
    }

    @Test
    public void AddressService_DeleteAddress_RemovesAddressByType() {
        // Arrange
        List<Address> addresses = new ArrayList<>();
        Address address1 = Address.builder().addressType(AddressType.TEMPORARY).addressInfo("TEMPORARY address").build();
        Address address2 = Address.builder().addressType(AddressType.PERMANENT).addressInfo("PERMANENT address").build();
        addresses.add(address1);
        addresses.add(address2);

        // Act
        addressService.deleteAddress(addresses, AddressType.PERMANENT);

        // Assert
        Assertions.assertThat(addresses).hasSize(1);
        Assertions.assertThat(addresses).containsExactly(address1);
        verify(addressRepository, times(1)).delete(address2);
    }

    @Test
    public void AddressService_AddAddress_NoAddressesInitially_ReturnsAddedAddresses() {
        // Arrange
        Person person = Person.builder().build();
        PersonDTO personDTO = PersonDTO.builder().addresses(Collections.singletonList(
                        AddressDTO.builder().addressType(AddressType.TEMPORARY).addressInfo("TEMPORARY address").build())).build();
        when(addressRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        List<Address> addedAddresses = addressService.addAddresses(person, personDTO);

        // Assert
        Assertions.assertThat(addedAddresses).isNotNull();
        Assertions.assertThat(addedAddresses).hasSize(1);
        Assertions.assertThat(addedAddresses.get(0).getAddressType()).isEqualTo(AddressType.TEMPORARY);
        Assertions.assertThat(addedAddresses.get(0).getAddressInfo()).isEqualTo("TEMPORARY address");
    }

    @Test
    public void AddressService_DeleteAllAddresses_EmptyAddressList_DoesNotDeleteAnything() {
        // Arrange
        List<Address> addresses = Collections.emptyList();

        // Act
        addressService.deleteAllAddress(addresses);

        // Assert
        verify(addressRepository, never()).delete(any());
    }

}