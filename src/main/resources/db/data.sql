INSERT INTO Persons (person_name) VALUES ('PersonA');
INSERT INTO Persons (person_name) VALUES ('PersonB');
INSERT INTO Persons (person_name) VALUES ('PersonC');

INSERT INTO Addresses (person_id, address_type, address_info) VALUES 
((SELECT person_id FROM Persons WHERE person_name = 'PersonA'), 'permanent', '1111 A Country A street'),
((SELECT person_id FROM Persons WHERE person_name = 'PersonA'), 'temporary', '2222 A Country A street'),
((SELECT person_id FROM Persons WHERE person_name = 'PersonB'), 'temporary', '3333 B Country B street');

INSERT INTO Contacts (person_id, contact_type, contact_info) VALUES 
((SELECT person_id FROM Persons WHERE person_name = 'PersonA'), 'email', 'asd@asd.com'),
((SELECT person_id FROM Persons WHERE person_name = 'PersonA'), 'phone', '123456789'),
((SELECT person_id FROM Persons WHERE person_name = 'PersonB'), 'email', 'dfg@dfg.com');