const handle500Error = (json) => {
  if (json.status && json.status === 500) {
    console.error(json.message);
    throw new Error(json.message);
  }
  return json;
}

const renderError = (message) => {
  alert(`Error calling Boots API: ${message}`);
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
      <p><b>&emsp;Contacts:</b></p>
    `;
    personCard.appendChild(personInfo);

    if (Array.isArray(person.contacts)) {
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

      personCard.appendChild(contactsList);
    } else {
      console.error('Invalid contacts data format:', person.contacts);
    }

    personsCardContainer.appendChild(personCard);
  });
}

const addPerson = () => {
  const addPersonForm = document.getElementById("addPersonForm").elements;
  const personName = addPersonForm["personName"].value;
  const contactType = addPersonForm["contactType"].value;
  const contactInfo = addPersonForm["contactInfo"].value;
  
  const person = { 
    personName,
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
  const contactId = updatePersonForm["contactId"].value;
  const contactType = updatePersonForm["contactType"].value;
  const contactInfo = updatePersonForm["contactInfo"].value;

  const person = {
    personId,
    personName,
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

const deletePersonById = () => {
  const deletePersonForm = document.getElementById("deletePerson").elements;
  const personId = deletePersonForm["personIdToDelete"].value;

  fetch(`/personregistry/persons/${personId}`, {
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

listPersons();
