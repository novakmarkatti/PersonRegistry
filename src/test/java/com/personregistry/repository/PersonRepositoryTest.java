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
import com.personregistry.entities.Person;
import com.personregistry.repositories.PersonRepository;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class PersonRepositoryTest {

    @Autowired
    private PersonRepository personRepository;


    @Test
    public void PersonEntity_Initialization() {
        // Arrange
        Person personA = Person.builder().personName("personA").build();

        // Act
        Person personEmpty = Person.builder().build();
        personA.setPersonName("personB");

        // Assert
        Assertions.assertThat(personEmpty).isNotNull();
        Assertions.assertThat(personA).isNotNull();
        Assertions.assertThat(personA.getPersonName()).isEqualTo("personB");
    }

    @Test
    public void PersonRepository_Save_ReturnsSavedPerson() {
        // Arrange
        Person person = Person.builder().personName("ASD").build();

        // Act
        Person savedPerson = personRepository.save(person);

        // Assert
        Assertions.assertThat(savedPerson).isNotNull();
        Assertions.assertThat(savedPerson.getPersonId()).isGreaterThan(0);
        Assertions.assertThat(savedPerson.getPersonName()).isEqualTo("ASD");
    }

    @Test
    public void PersonRepository_FindById_ReturnsSavedPerson() {
        // Arrange
        Person personA = Person.builder().personName("personA").build();
        personRepository.save(personA);

        // Act
        Person foundedPerson = personRepository.findById(personA.getPersonId()).get();

        // Assert
        Assertions.assertThat(foundedPerson).isNotNull();
        Assertions.assertThat(personA.getPersonId()).isEqualTo(foundedPerson.getPersonId());
        Assertions.assertThat(personA.getPersonName()).isEqualTo(foundedPerson.getPersonName());
        Assertions.assertThat(personA).isEqualTo(foundedPerson);
    }

    @Test
    public void PersonRepository_FindByPersonName_ReturnsCorrectPerson() {
        // Arrange
        Person personA = Person.builder().personName("personA").build();
        personRepository.save(personA);

        // Act
        Person foundedPerson = personRepository.findByPersonName("personA");

        // Assert
        Assertions.assertThat(foundedPerson).isNotNull();
        Assertions.assertThat(personA.getPersonId()).isEqualTo(foundedPerson.getPersonId());
        Assertions.assertThat(personA.getPersonName()).isEqualTo(foundedPerson.getPersonName());
        Assertions.assertThat(personA).isEqualTo(foundedPerson);
    }

    @Test
    public void PersonRepository_FindByPersonIdAndPersonName_ReturnsCorrectPerson() {
        // Arrange
        Person personA = Person.builder().personName("personA").build();
        personRepository.save(personA);

        // Act
        Person foundedPerson = personRepository.findByPersonIdAndPersonName(personA.getPersonId(), "personA");

        // Assert
        Assertions.assertThat(foundedPerson).isNotNull();
        Assertions.assertThat(personA.getPersonId()).isEqualTo(foundedPerson.getPersonId());
        Assertions.assertThat(personA.getPersonName()).isEqualTo(foundedPerson.getPersonName());
        Assertions.assertThat(personA).isEqualTo(foundedPerson);
    }


    @Test
    public void PersonRepository_FindAll_ReturnsSavedPersons() {
        // Arrange
        List<Person> listPersonBeforeSave = StreamSupport.stream(personRepository.findAll().spliterator(), false).collect(Collectors.toList());
        Person personA = Person.builder().personName("personA").build();
        Person personB = Person.builder().personName("personB").build();
        personRepository.save(personA);
        personRepository.save(personB);

        // Act
        List<Person> listPersonAfterSave = StreamSupport.stream(personRepository.findAll().spliterator(), false).collect(Collectors.toList());

        // Assert
        Assertions.assertThat(listPersonAfterSave).isNotNull();
        Assertions.assertThat(listPersonAfterSave.size()).isEqualTo(listPersonBeforeSave.size() + 2);
    }

    @Test
    public void PersonRepository_Delete_RemovesPersonsFromDatabase() {
        // Arrange
        Person personA = Person.builder().personName("personA").build();
        personRepository.save(personA);

        Person personB = Person.builder().personName("personB").build();
        personRepository.save(personB);

        // Act
        personRepository.deleteById(personA.getPersonId());
        Optional<Person> deletedPersonA = personRepository.findById(personA.getPersonId());

        personRepository.delete(personB);
        Optional<Person> deletedPersonB = personRepository.findById(personB.getPersonId());

        // Assert
        Assertions.assertThat(deletedPersonA).isEmpty();
        Assertions.assertThat(deletedPersonB).isEmpty();
    }
    
}
