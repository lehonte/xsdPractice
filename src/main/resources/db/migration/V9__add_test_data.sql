INSERT INTO users(username, password)
VALUES (
        'me',
        '$2a$12$LRHC.Nbbt3RXV2xbSbeNeOFRcRM.uLTdI.ayChs6nROwDadqeQp1y'
);

INSERT INTO accounts(owner, account_number, balance, email)
VALUES (
        'me',
        '123',
        '1000',
        'me@gmail.com'
);

INSERT INTO users(username, password)
VALUES (
        'you',
        '$2a$12$Jd9VUe1ou4fcSRWngO8OEe8wqYZvwZVJUsvlXoAVlsnIfBNvbsQAS'
);

INSERT INTO accounts(owner, account_number, balance, email)
VALUES (
        'you',
        '789',
        '1000',
        'you@gmail.com'
       );
