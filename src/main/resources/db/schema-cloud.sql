-- ---------------------------------------------------------------------------
-- CST-323 Cloud Test Application - MySQL DDL script (cloud variant)
--
-- Identical to schema.sql except that CREATE DATABASE and USE are omitted.
-- Managed MySQL services create the schema for you and connect the session to
-- it: Azure Database for MySQL Flexible Server creates 'cst323' from the
-- portal, and JawsDB hands out a pre-created schema with a generated name that
-- the account has no privilege to re-create. Run this script while already
-- connected to the target schema.
-- ---------------------------------------------------------------------------

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
