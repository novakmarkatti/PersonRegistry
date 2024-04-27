INSERT INTO Persons (person_name) VALUES ('PersonA');
INSERT INTO Persons (person_name) VALUES ('PersonB');
INSERT INTO Persons (person_name) VALUES ('PersonC');

INSERT INTO Addresses (person_id, address_type, country, state, city, street, zip_code) VALUES 
((SELECT person_id FROM Persons WHERE person_name = 'PersonA'), 'permanent', 'HUNGARY', 'stateA', 'cityA', 'streetA1', 1111),
((SELECT person_id FROM Persons WHERE person_name = 'PersonA'), 'temporary', 'HUNGARY', 'stateA', 'cityA', 'streetA2', 2222),
((SELECT person_id FROM Persons WHERE person_name = 'PersonB'), 'temporary', 'HUNGARY', 'stateB', 'cityB', 'streetB3', 3333);

INSERT INTO Contacts (person_id, contact_type, contact_info) VALUES 
((SELECT person_id FROM Persons WHERE person_name = 'PersonA'), 'email', 'asd@asd.com'),
((SELECT person_id FROM Persons WHERE person_name = 'PersonA'), 'phone', '123456789'),
((SELECT person_id FROM Persons WHERE person_name = 'PersonB'), 'email', 'dfg@dfg.com');