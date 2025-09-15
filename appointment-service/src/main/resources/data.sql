INSERT INTO users (username, password, role, enabled, created_at)
VALUES ('joao.silva', '$2a$10$qh2BLer14kkg58hXI2nWjOYAHn3/YapvsEPLdBvCnBPhQtXlWtTwu', 'DOCTOR', TRUE, NOW())
    ON CONFLICT (username) DO NOTHING;

INSERT INTO users (username, password, role, enabled, created_at)
VALUES ('maria.souza', '$2a$10$qh2BLer14kkg58hXI2nWjOYAHn3/YapvsEPLdBvCnBPhQtXlWtTw.', 'NURSE', TRUE, NOW())
    ON CONFLICT (username) DO NOTHING;

INSERT INTO users (username, password, role, enabled, created_at)
VALUES ('ana.lima', '$2a$10$qh2BLer14kkg58hXI2nWjOYAHn3/YapvsEPLdBvCnBPhQtXlWtTwu', 'PATIENT', TRUE, NOW())
    ON CONFLICT (username) DO NOTHING;

INSERT INTO doctors (user_id, name, specialty, crm)
SELECT (SELECT id FROM users WHERE username = 'joao.silva'), 'João Silva', 'Cardiologia', 'CRM12345'
    WHERE NOT EXISTS (
    SELECT 1 FROM doctors WHERE crm = 'CRM12345'
);

INSERT INTO nurses (user_id, name, coren)
SELECT (SELECT id FROM users WHERE username = 'maria.souza'), 'Maria Souza', 'COREN67890'
    WHERE NOT EXISTS (
    SELECT 1 FROM nurses WHERE coren = 'COREN67890'
);

INSERT INTO patients (user_id, name, date_of_birth, document, phone, email, address)
SELECT (SELECT id FROM users WHERE username = 'ana.lima'), 'Paciente Ana Lima', '1990-01-01', 'DOC123456', '11987654321', 'ana.lima@exemplo.com', 'Rua das Flores, 123'
    WHERE NOT EXISTS (
    SELECT 1 FROM patients WHERE document = 'DOC123456'
);

INSERT INTO appointments (patient_id, doctor_id, nurse_id, appointment_date, status, notes, created_at, updated_at)
SELECT
    (SELECT id FROM patients WHERE document = 'DOC123456'),
    (SELECT id FROM doctors WHERE crm = 'CRM12345'),
    (SELECT id FROM nurses WHERE coren = 'COREN67890'),
    NOW() + INTERVAL '1 DAY', 'AGENDADO', 'Consulta inicial', NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM appointments WHERE appointment_date = NOW() + INTERVAL '1 DAY'
    );