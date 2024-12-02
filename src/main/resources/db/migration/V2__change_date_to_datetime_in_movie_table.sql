ALTER TABLE movie RENAME COLUMN creation_date TO creation_date_time;
ALTER TABLE movie RENAME COLUMN update_date TO update_date_time;

ALTER TABLE movie ALTER COLUMN creation_date_time TYPE TIMESTAMP;
ALTER TABLE movie ALTER COLUMN update_date_time TYPE TIMESTAMP;