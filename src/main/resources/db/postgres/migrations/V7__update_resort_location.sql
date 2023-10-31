CREATE TABLE country
(
    id               BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    created_on       TIMESTAMP WITHOUT TIME ZONE,
    created_by       VARCHAR(255),
    last_modified_on TIMESTAMP WITHOUT TIME ZONE,
    last_modified_by VARCHAR(255),
    name             VARCHAR(450)                            NOT NULL,
    code             VARCHAR(255),
    CONSTRAINT pk_country PRIMARY KEY (id)
);

CREATE TABLE district
(
    id                   BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    created_on           TIMESTAMP WITHOUT TIME ZONE,
    created_by           VARCHAR(255),
    last_modified_on     TIMESTAMP WITHOUT TIME ZONE,
    last_modified_by     VARCHAR(255),
    code                 VARCHAR(255),
    name                 VARCHAR(450)                            NOT NULL,
    type                 VARCHAR(255),
    state_or_province_id BIGINT,
    CONSTRAINT pk_district PRIMARY KEY (id)
);

CREATE TABLE state_or_province
(
    id               BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    created_on       TIMESTAMP WITHOUT TIME ZONE,
    created_by       VARCHAR(255),
    last_modified_on TIMESTAMP WITHOUT TIME ZONE,
    last_modified_by VARCHAR(255),
    code             VARCHAR(255),
    name             VARCHAR(450)                            NOT NULL,
    type             VARCHAR(255),
    country_id       BIGINT,
    CONSTRAINT pk_state_or_province PRIMARY KEY (id)
);

ALTER TABLE resort
    ADD address_line VARCHAR(450);

ALTER TABLE resort
    ADD district BIGINT;

ALTER TABLE resort
    ADD latitude FLOAT;

ALTER TABLE resort
    ADD location_code VARCHAR(35);

ALTER TABLE resort
    ADD location_description VARCHAR(255);

ALTER TABLE resort
    ADD location_formatted_name VARCHAR(255);

ALTER TABLE resort
    ADD longitude FLOAT;

ALTER TABLE resort
    ADD postal_code VARCHAR(35);

ALTER TABLE district
    ADD CONSTRAINT FK_DISTRICT_ON_STATE_OR_PROVINCE FOREIGN KEY (state_or_province_id) REFERENCES state_or_province (id);

ALTER TABLE resort
    ADD CONSTRAINT FK_RESORT_ON_DISTRICT FOREIGN KEY (district) REFERENCES district (id);

ALTER TABLE state_or_province
    ADD CONSTRAINT FK_STATE_OR_PROVINCE_ON_COUNTRY FOREIGN KEY (country_id) REFERENCES country (id);

ALTER TABLE resort
    DROP CONSTRAINT fk_resort_on_location;

DROP TABLE location CASCADE;

ALTER TABLE resort
    DROP COLUMN location_id;