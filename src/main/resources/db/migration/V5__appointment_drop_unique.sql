CREATE TABLE appointment_new
(
    id           SERIAL       NOT NULL PRIMARY KEY,
    doctor_id    INTEGER      NOT NULL,
    patient_id   INTEGER,
    date_time    TIMESTAMP    NOT NULL,
    type         VARCHAR(10)  NOT NULL,
    state        VARCHAR(20)  NOT NULL,
    note         VARCHAR(255) NOT NULL,
    address      VARCHAR(255) NOT NULL,
    patient_note VARCHAR(255),
    rating       DECIMAL(2, 1),
    FOREIGN KEY (doctor_id) REFERENCES doctor (id),
    FOREIGN KEY (patient_id) REFERENCES users (id)
);

INSERT INTO appointment_new (id, doctor_id, patient_id, date_time, type, state, note, address, patient_note, rating)
SELECT id,
       doctor_id,
       patient_id,
       date_time,
       type,
       state,
       note,
       address,
       patient_note,
       rating
FROM appointment;

ALTER TABLE prescription
    DROP CONSTRAINT prescription_appointment_id_fkey;

DROP TABLE appointment;

ALTER TABLE appointment_new
    RENAME TO appointment;

ALTER TABLE prescription
    ADD CONSTRAINT prescription_appointment_id_fkey FOREIGN KEY (appointment_id) REFERENCES appointment (id);