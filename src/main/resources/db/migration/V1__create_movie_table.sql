CREATE TABLE movie (
    id SERIAL PRIMARY KEY,
    isan VARCHAR(26) NOT NULL UNIQUE,
    title VARCHAR(40) NOT NULL,
    release_year INTEGER,
    creation_date DATE,
    update_date DATE
);