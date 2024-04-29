package com.personregistry.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Entity
@Table(name = "Persons")
public class Person {

  @Id
  @Column(name = "person_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer personId;

  @Column(name = "person_name")
  private String personName;

  @OneToMany(mappedBy = "person")
  @JsonManagedReference
  private List<Contact> contacts;


  public Integer getPersonId() {
    return this.personId;
  }

  public void setPersonId(Integer personId) {
    this.personId = personId;
  }

  public String getPersonName() {
    return this.personName;
  }

  public void setPersonName(String personName) {
    this.personName = personName;
  }

	public List<Contact> getContacts() {
		return contacts;
	}

	public void setContacts(List<Contact> contacts) {
		this.contacts = contacts;
	}

}