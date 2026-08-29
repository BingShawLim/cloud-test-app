-- ---------------------------------------------------------------------------
-- CST-323 Cloud Test Application - MySQL DDL script
-- Run against each cloud provider's MySQL instance in Topics 3 and 4.
-- ---------------------------------------------------------------------------

CREATE DATABASE IF NOT EXISTS cst323
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE cst323;

DROP TABLE IF EXISTS contact;

CREATE TABLE contact (
    CONTACT_ID   BIGINT       NOT NULL AUTO_INCREMENT,
    FIRST_NAME   VARCHAR(50)  NOT NULL,
    LAST_NAME    VARCHAR(50)  NOT NULL,
    EMAIL        VARCHAR(100) NOT NULL,
    PHONE        VARCHAR(20)      NULL,
    COMPANY      VARCHAR(100)     NULL,
    CREATED_DATE DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT PK_CONTACT PRIMARY KEY (CONTACT_ID),
    CONSTRAINT UQ_CONTACT_EMAIL UNIQUE (EMAIL)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX IX_CONTACT_LAST_NAME ON contact (LAST_NAME, FIRST_NAME);
