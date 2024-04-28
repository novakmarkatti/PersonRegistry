  const handle500Error = (json) => {
    if (json.status && json.status === 500) {
      console.log(json.message);
      throw new Error(json.message)
    }
    return json
  }

  const renderError = (message) => {
    alert(`Error calling Boots API: ${message}`);
  }

  const fetchPersons = (cb) => {
    fetch("/personregistry/persons")
      .then(res => res.json())
      .then(handle500Error)
      .then(json => cb(json))
      .catch(renderError);
  }

  const renderPersonsListCallback = (personsTableBody) => (persons) => {
    while (personsTableBody.firstChild) {
      personsTableBody.removeChild(personsTableBody.firstChild);
    }
    persons.forEach((person) => {
      const personsRow = document.createElement("tr");
      personsRow.innerHTML = `
        <td>${person.personId}</td>
        <td>${person.personName}</td>
      `;
      personsTableBody.appendChild(personsRow);
    });
  }
  
  const addPerson = (cb) => {
    const addPersonForm = document.getElementById("addPerson").elements;
    const personName = addPersonForm["personNameToAdd"].value;
    const person = { personName }

    fetch("/personregistry/persons", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(person)
    })
      .then(res => res.json())
      .then(handle500Error)
      .then(() => fetchPersons(cb))
      .catch(renderError);
  }

  const updatePerson = (cb) => {
    const updatePersonForm = document.getElementById("updatePerson").elements;
    const personId = updatePersonForm["personIdToUpdate"].value;
    const personName = updatePersonForm["personNameToUpdate"].value;
    const person = { personId, personName }

    fetch(`/personregistry/persons/${personId}`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(person)
    })
      .then(res => res.json())
      .then(handle500Error)
      .then(() => fetchPersons(cb))
      .catch(renderError);
  }

  const deletePersonById = (cb) => {
    const deletePersonForm = document.getElementById("deletePerson").elements;
    const personId = deletePersonForm["personIdToDelete"].value;

    fetch(`/personregistry/persons/${personId}`, {
      method: "DELETE"
    })
      .then(res => res.json())
      .then(handle500Error)
      .then(() => fetchPersons(cb))
      .catch(renderError);
  }

  const searchPerson = (cb) => {
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
      .then((persons) => { cb(persons); })
      .catch(renderError);
  }

  const listPersons = (cb) => {
    fetch("/personregistry/persons")
      .then(res => res.json())
      .then(handle500Error)
      .then(() => fetchPersons(cb))
      .catch(renderError);
  }

  fetchPersons(
    renderPersonsListCallback(
      document.getElementById("personsTableBody")
    )
  );