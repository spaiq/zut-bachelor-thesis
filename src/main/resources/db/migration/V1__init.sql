CREATE TABLE users
(
    id             SERIAL       NOT NULL PRIMARY KEY,
    email          VARCHAR(255) NOT NULL,
    password       VARCHAR(255) NOT NULL,
    seed           VARCHAR(255) NOT NULL,
    totp_algorithm VARCHAR(10)  NOT NULL,
    name           VARCHAR(255) NOT NULL,
    second_name    VARCHAR(255) NOT NULL,
    surname        VARCHAR(255) NOT NULL,
    pesel          VARCHAR(11)  NOT NULL,
    phone_number   VARCHAR(9)   NOT NULL,
    role           VARCHAR(25)  NOT NULL

);

CREATE TABLE speciality
(
    id   SERIAL       NOT NULL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE doctor
(
    id            SERIAL     NOT NULL PRIMARY KEY,
    user_id       INTEGER    NOT NULL,
    speciality_id INTEGER    NOT NULL,
    pwz           VARCHAR(7) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (speciality_id) REFERENCES speciality (id)
);

CREATE TABLE appointment
(
    id           SERIAL       NOT NULL PRIMARY KEY,
    doctor_id    INTEGER      NOT NULL,
    patient_id   INTEGER,
    date_time    TIMESTAMP    NOT NULL,
    type         VARCHAR(10)  NOT NULL,
    state        VARCHAR(20)  NOT NULL,
    description  VARCHAR(255) NOT NULL,
    patient_note VARCHAR(255),
    rating       DECIMAL(2, 1),
    FOREIGN KEY (doctor_id) REFERENCES doctor (id),
    FOREIGN KEY (patient_id) REFERENCES users (id)
);

CREATE TABLE prescription
(
    id             SERIAL       NOT NULL PRIMARY KEY,
    appointment_id INTEGER      NOT NULL,
    date           DATE         NOT NULL,
    description    VARCHAR(255) NOT NULL,
    FOREIGN KEY (appointment_id) REFERENCES appointment (id)
);