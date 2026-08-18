ALTER TABLE accounts ADD COLUMN phone_number VARCHAR(20) NOT NULL UNIQUE DEFAULT 0;

UPDATE accounts
SET phone_number='${admin_number}'
WHERE owner='admin' AND account_number='12345';

COMMIT;