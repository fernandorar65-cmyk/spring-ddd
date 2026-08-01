-- Organization reference catalogs (DDL only). Seeded from Java when needed.

CREATE TABLE organization_departments (
    id UUID NOT NULL PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    description VARCHAR(100) NOT NULL,
    CONSTRAINT uq_organization_departments_name UNIQUE (name)
);

CREATE TABLE organization_jobs (
    id UUID NOT NULL PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    description VARCHAR(100) NOT NULL,
    CONSTRAINT uq_organization_jobs_name UNIQUE (name)
);

CREATE TABLE organization_statuses (
    id UUID NOT NULL PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    description VARCHAR(100) NOT NULL,
    CONSTRAINT uq_organization_statuses_name UNIQUE (name)
);

CREATE TABLE organization_member_statuses (
    id UUID NOT NULL PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    description VARCHAR(100) NOT NULL,
    CONSTRAINT uq_organization_member_statuses_name UNIQUE (name)
);
