-- Users bounded context tables + seed (ADMIN role and common permissions)

CREATE TABLE organizations (
    id UUID NOT NULL PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    slug VARCHAR(100) NOT NULL,
    logo VARCHAR(500),
    description TEXT,
    timezone VARCHAR(64) NOT NULL,
    language VARCHAR(10) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT uq_organizations_slug UNIQUE (slug)
);

CREATE TABLE permissions (
    id UUID NOT NULL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    module VARCHAR(50) NOT NULL,
    CONSTRAINT uq_permissions_name_module UNIQUE (name, module)
);

CREATE TABLE roles (
    id UUID NOT NULL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(30) NOT NULL,
    description VARCHAR(255),
    CONSTRAINT uq_roles_type UNIQUE (type)
);

CREATE TABLE role_permissions (
    role_id UUID NOT NULL,
    permission_id UUID NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    CONSTRAINT fk_role_permissions_role FOREIGN KEY (role_id) REFERENCES roles (id),
    CONSTRAINT fk_role_permissions_permission FOREIGN KEY (permission_id) REFERENCES permissions (id)
);

CREATE TABLE users (
    id UUID NOT NULL PRIMARY KEY,
    organization_id UUID NOT NULL,
    role_id UUID,
    email VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(80) NOT NULL,
    last_name VARCHAR(80) NOT NULL,
    status VARCHAR(20) NOT NULL,
    avatar VARCHAR(500),
    department VARCHAR(100),
    job_title VARCHAR(100),
    phone_number VARCHAR(30),
    birth_date DATE,
    bio TEXT,
    location VARCHAR(150),
    profile_avatar_url VARCHAR(500),
    last_login TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT uq_users_email UNIQUE (email),
    CONSTRAINT fk_users_organization FOREIGN KEY (organization_id) REFERENCES organizations (id),
    CONSTRAINT fk_users_role FOREIGN KEY (role_id) REFERENCES roles (id)
);

CREATE INDEX idx_users_organization_id ON users (organization_id);

INSERT INTO permissions (id, name, description, module) VALUES
    ('11111111-1111-1111-1111-111111111101', 'QUIZ_CREATE', 'Crear quizzes', 'quiz'),
    ('11111111-1111-1111-1111-111111111102', 'QUIZ_PUBLISH', 'Publicar quizzes', 'quiz'),
    ('11111111-1111-1111-1111-111111111103', 'GAME_HOST', 'Iniciar sesiones de juego', 'gameplay'),
    ('11111111-1111-1111-1111-111111111104', 'USER_MANAGE', 'Gestionar usuarios', 'user');

INSERT INTO roles (id, name, type, description) VALUES
    ('22222222-2222-2222-2222-222222222201', 'Administrator', 'ADMIN', 'Full access');

INSERT INTO role_permissions (role_id, permission_id) VALUES
    ('22222222-2222-2222-2222-222222222201', '11111111-1111-1111-1111-111111111101'),
    ('22222222-2222-2222-2222-222222222201', '11111111-1111-1111-1111-111111111102'),
    ('22222222-2222-2222-2222-222222222201', '11111111-1111-1111-1111-111111111103'),
    ('22222222-2222-2222-2222-222222222201', '11111111-1111-1111-1111-111111111104');
