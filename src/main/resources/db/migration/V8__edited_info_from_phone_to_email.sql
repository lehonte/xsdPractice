ALTER TABLE accounts DROP COLUMN phone_number;

ALTER TABLE accounts ADD COLUMN email VARCHAR(255) DEFAULT 0;

UPDATE accounts
SET email='${admin_email}'
WHERE owner='admin';

COMMIT;