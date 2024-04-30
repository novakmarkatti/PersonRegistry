CREATE TABLE Persons (
    person_id INT PRIMARY KEY IDENTITY,
    person_name VARCHAR(50) NOT NULL
);

CREATE TABLE Addresses (
    address_id INT PRIMARY KEY IDENTITY,
    person_id INT,
    address_type VARCHAR(20),
    country VARCHAR(50),
    state VARCHAR(50),
    city VARCHAR(50),
    street VARCHAR(100),
    zip_code INT,
    FOREIGN KEY (person_id) REFERENCES Persons(person_id)
);

CREATE TABLE Contacts (
    contact_id INT PRIMARY KEY IDENTITY,
    person_id INT,
    contact_type VARCHAR(20),
    contact_info VARCHAR(100),
    FOREIGN KEY (person_id) REFERENCES Persons(person_id)
);

CREATE TRIGGER trg_before_address_insert ON Addresses
BEFORE INSERT
AS
BEGIN
    IF (SELECT COUNT(*) FROM Addresses WHERE person_id = (SELECT person_id FROM inserted)) >= 2
    BEGIN
        RAISERROR ('A person cannot have more than two addresses');
        ROLLBACK TRANSACTION;
        RETURN;
    END;

    IF EXISTS (SELECT 1 FROM inserted WHERE address_type NOT IN ('TEMPORARY', 'PERMANENT'))
    BEGIN
        RAISERROR ('Address type must be either TEMPORARY or PERMANENT');
        ROLLBACK TRANSACTION;
        RETURN;
    END;
END;
