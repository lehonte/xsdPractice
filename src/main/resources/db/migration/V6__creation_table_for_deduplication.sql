CREATE TABLE results_of_checking(
    transaction_number VARCHAR(255) PRIMARY KEY,
    processed_at TIMESTAMP DEFAULT NOW()
);