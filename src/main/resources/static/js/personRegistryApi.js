const handle500Error = (json) => {
  if (json.status && json.status === 500) {
    console.error(json.message);
    throw new Error(json.message);
  }
  return json;
}

const renderError = (message) => {
  alert(`Error: ${message}`);
}

const fetchPersons = (cb) => {
  fetch("/personregistry/persons")
    .then(res => res.json())
    .then(handle500Error)
    .then(cb)
    .catch(renderError);
}

const renderPersonsListCallback = (personsCardContainer) => (persons) => {
  if (!Array.isArray(persons)) {
    console.error('Invalid data format:', persons);
    return;
  }

  while (personsCardContainer.firstChild) {
    personsCardContainer.removeChild(personsCardContainer.firstChild);
  }

  persons.forEach((person) => {
    const personCard = document.createElement("div");
    personCard.classList.add("person-card");

    const personInfo = document.createElement("div");
    personInfo.classList.add("person-info");
    personInfo.innerHTML = `
      <h3>${person.personName}</h3>
      <p><i>&emsp;Person ID: ${person.personId}</i></p>
    `;
    personCard.appendChild(personInfo);

    if (Array.isArray(person.addresses) && person.addresses.length > 0) {
      const addressesSection = document.createElement("div");
      addressesSection.classList.add("addresses-section");
      addressesSection.innerHTML = "<p><b>&emsp;Addresses:</b></p>";
      const addressesList = document.createElement("ul");
      addressesList.classList.add("addresses-list");
      person.addresses.forEach((address) => {
        const addressItem = document.createElement("li");
        addressItem.innerHTML = `
          <p>&emsp;&emsp;<b>${address.addressType}:</b> ${address.addressInfo}</p>
        `;
        addressesList.appendChild(addressItem);
      });
      addressesSection.appendChild(addressesList);
      personCard.appendChild(addressesSection);
    }

    if (Array.isArray(person.contacts) && person.contacts.length > 0) {
      const contactsSection = document.createElement("div");
      contactsSection.classList.add("contacts-section");
      contactsSection.innerHTML = "<p><b>&emsp;Contacts:</b></p>";
      const contactsList = document.createElement("ul");
      contactsList.classList.add("contacts-list");
      person.contacts.forEach((contact) => {
        const contactItem = document.createElement("li");
        contactItem.innerHTML = `
          <p><i>&emsp;&emsp;Contact ID: ${contact.contactId}</i></p>
          <p>&emsp;&emsp;<b>${contact.contactType}:</b> ${contact.contactInfo}</p>
        `;
        contactsList.appendChild(contactItem);
      });
      contactsSection.appendChild(contactsList);
      personCard.appendChild(contactsSection);
    }

    personsCardContainer.appendChild(personCard);
  });
}

const addPerson = () => {
  const addPersonForm = document.getElementById("addPersonForm").elements;
  const personName = addPersonForm["personName"].value;
  const addressType = addPersonForm["addressType"].value;
  const addressInfo = addPersonForm["addressInfo"].value;
  const contactType = addPersonForm["contactType"].value;
  const contactInfo = addPersonForm["contactInfo"].value;
  
  const person = { 
    personName,
    addresses: [{
      addressType,
      addressInfo
    }],
    contacts: [{
      contactType,
      contactInfo
    }]
  };

  fetch("/personregistry/persons", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(person)
  })
  .then(handle500Error)
  .then(() => fetchPersons(renderPersonsListCallback(document.getElementById('personsCardContainer'))))
  .catch(renderError);
}

const updatePerson = () => {
  const updatePersonForm = document.getElementById("updatePersonForm").elements;
  const personId = updatePersonForm["personId"].value;
  const personName = updatePersonForm["personName"].value;
  const addressType = updatePersonForm["addressType"].value;
  const addressInfo = updatePersonForm["addressInfo"].value;
  const contactId = updatePersonForm["contactId"].value;
  const contactType = updatePersonForm["contactType"].value;
  const contactInfo = updatePersonForm["contactInfo"].value;

  const person = {
    personId,
    personName,
    addresses: [{
      addressType,
      addressInfo
    }],
    contacts: [{
      contactId,
      contactType,
      contactInfo
    }]
  };

  fetch(`/personregistry/persons/${personId}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(person)
  })
  .then(handle500Error)
  .then(() => fetchPersons(renderPersonsListCallback(document.getElementById('personsCardContainer'))))
  .catch(renderError);
}

const deletePerson = () => {
  const deletePersonForm = document.getElementById("deletePersonForm").elements;
  const personId = deletePersonForm["personId"].value;
  const deletePersonData = deletePersonForm["deletePersonData"].checked;
  const addressType = deletePersonForm["addressType"].value;
  const deleteAllContacts = deletePersonForm["deleteAllContacts"].checked;
  const deleteContactWithId = deletePersonForm["deleteContactWithId"].checked;
  const contactId = deletePersonForm["contactId"].value;

  let url = `/personregistry/persons/${personId}`;
  let params = new URLSearchParams();

  if (deletePersonData) {
    params.append('deletePersonData', 'true');
  } 
  if (addressType) {
    params.append('addressType', addressType);
  } 
  if (deleteAllContacts) {
    params.append('deleteAllContacts', 'true');
  }
  if (deleteContactWithId) {
    params.append('contactId', contactId);
  }
  if (params.toString() !== '') {
    url += '?' + params.toString();
  }

  fetch(url, {
    method: "DELETE"
  })
  .then(handle500Error)
  .then(() => fetchPersons(renderPersonsListCallback(document.getElementById('personsCardContainer'))))
  .catch(renderError);
}

const searchPerson = () => {
  const searchPersonForm = document.getElementById("searchPerson").elements;
  const personId = searchPersonForm["personIdToSearch"].value;
  const personName = searchPersonForm["personNameToSearch"].value;

  const queryParams = {};
  if (personId) queryParams.personId = personId;
  if (personName) queryParams.personName = personName;

  const queryString = Object.keys(queryParams)
      .map((key) => `${encodeURIComponent(key)}=${encodeURIComponent(queryParams[key])}`)
      .join('&');

  fetch(`/personregistry/persons/search?${queryString}`)
  .then(res => res.json())
  .then(handle500Error)
  .then(renderPersonsListCallback(document.getElementById('personsCardContainer')))
  .catch(renderError);
}

const listPersons = () => {
  fetchPersons(renderPersonsListCallback(document.getElementById('personsCardContainer')));
}

const fetchAddressTypes = async () => {
  try {
    const response = await fetch('/personregistry/addresstypes');
    const addressTypes = await response.json();
    return addressTypes;
  } catch (error) {
    renderError('Error fetching address types');
    return [];
  }
};

const populateAddressTypes = async () => {
  const addressTypeSelects = document.querySelectorAll('.addressType');
  const addressTypes = await fetchAddressTypes();

  addressTypeSelects.forEach((select) => {
    select.innerHTML = '';
    addressTypes.forEach((addressType) => {
      const option = document.createElement('option');
      option.text = addressType;
      option.value = addressType;
      if (addressType === 'EMPTY') {
        option.setAttribute('selected', 'selected');
      }
      select.appendChild(option);
    });
  });
};

window.addEventListener('DOMContentLoaded', populateAddressTypes);
listPersons();