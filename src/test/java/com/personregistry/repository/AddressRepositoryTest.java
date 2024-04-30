package com.personregistry.repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import com.personregistry.entities.Address;
import com.personregistry.enums.AddressType;
import com.personregistry.repositories.AddressRepository;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class AddressRepositoryTest {

    @Autowired
    private AddressRepository addressRepository;

    @Test
    public void AddressRepository_Save_ReturnsSavedAddress() {
        // Arrange
        Address address = Address.builder().addressType(AddressType.TEMPORARY).addressInfo("TEMPORARY address").build();

        // Act
        Address savedAddress = addressRepository.save(address);

        // Assert
        Assertions.assertThat(savedAddress).isNotNull();
        Assertions.assertThat(savedAddress.getAddressId()).isGreaterThan(0);
        Assertions.assertThat(savedAddress.getAddressType()).isEqualTo(AddressType.TEMPORARY);
        Assertions.assertThat(savedAddress.getAddressInfo()).isEqualTo("TEMPORARY address");
    }

    @Test
    public void AddressRepository_FindById_ReturnsSavedAddress() {
        // Arrange
        Address address = Address.builder().addressType(AddressType.PERMANENT).addressInfo("PERMANENT address").build();
        addressRepository.save(address);

        // Act
        Address foundAddress = addressRepository.findById(address.getAddressId()).orElse(null);

        // Assert
        Assertions.assertThat(foundAddress).isNotNull();
        Assertions.assertThat(foundAddress.getAddressId()).isEqualTo(address.getAddressId());
        Assertions.assertThat(foundAddress.getAddressType()).isEqualTo(address.getAddressType());
        Assertions.assertThat(foundAddress.getAddressInfo()).isEqualTo(address.getAddressInfo());
    }

    @Test
    public void AddressRepository_SaveAllFindAll_ReturnsSavedAddresses() {
        // Arrange
        List<Address> addressesBeforeSave = StreamSupport.stream(addressRepository.findAll().spliterator(), false).collect(Collectors.toList());
        Address addressA = Address.builder().addressType(AddressType.PERMANENT).addressInfo("PERMANENT address").build();
        Address addressB = Address.builder().addressType(AddressType.TEMPORARY).addressInfo("TEMPORARY address").build();

        // Act
        addressRepository.saveAll(List.of(addressA, addressB));
        List<Address> addressesAfterSave = StreamSupport.stream(addressRepository.findAll().spliterator(), false).collect(Collectors.toList());

        // Assert
        Assertions.assertThat(addressesAfterSave).isNotNull();
        Assertions.assertThat(addressesAfterSave.size()).isEqualTo(addressesBeforeSave.size() + 2);
    }

    @Test
    public void AddressRepository_Delete_RemovesAddressesFromDatabase() {
        // Arrange
        Address addressA = Address.builder().addressType(AddressType.PERMANENT).addressInfo("PERMANENT address").build();
        addressRepository.save(addressA);

        Address addressB = Address.builder().addressType(AddressType.TEMPORARY).addressInfo("TEMPORARY address").build();
        addressRepository.save(addressB);

        // Act
        addressRepository.deleteById(addressA.getAddressId());
        Optional<Address> deletedAddressA = addressRepository.findById(addressA.getAddressId());

        addressRepository.delete(addressB);
        Optional<Address> deletedAddressB = addressRepository.findById(addressB.getAddressId());

        // Assert
        Assertions.assertThat(deletedAddressA).isEmpty();
        Assertions.assertThat(deletedAddressB).isEmpty();
    }

}
