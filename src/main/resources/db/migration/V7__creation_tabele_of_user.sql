CREATE TABLE users(
    username VARCHAR(255) PRIMARY KEY,
    password VARCHAR(255) NOT NULL
);

INSERT INTO users (username, password) VALUES ('admin', '${admin_password}');

ALTER TABLE accounts DROP CONSTRAINT accounts_owner_key;

ALTER TABLE accounts ADD CONSTRAINT  fk_accounts_owner FOREIGN KEY (owner) REFERENCES users(username) ON UPDATE CASCADE;