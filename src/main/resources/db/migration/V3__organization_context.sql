-- Organization bounded context: membership owns the user <-> organization link,
-- so the identity context (users) no longer references organizations.
-- user_id has no FK on purpose: it points to an aggregate of another bounded context.

CREATE TABLE organization_members (
    id UUID NOT NULL PRIMARY KEY,
    organization_id UUID NOT NULL,
    user_id UUID NOT NULL,
    role_id UUID,
    status VARCHAR(20) NOT NULL,
    joined_at TIMESTAMP NOT NULL,
    CONSTRAINT uq_organization_members_org_user UNIQUE (organization_id, user_id),
    CONSTRAINT fk_organization_members_organization FOREIGN KEY (organization_id) REFERENCES organizations (id)
);

CREATE INDEX idx_organization_members_user_id ON organization_members (user_id);

-- Existing users keep their organization as an ACTIVE membership (member id reuses the user id).
INSERT INTO organization_members (id, organization_id, user_id, role_id, status, joined_at)
SELECT u.id, u.organization_id, u.id, u.role_id, 'ACTIVE', u.created_at
FROM users u;

DROP INDEX idx_users_organization_id;
ALTER TABLE users DROP CONSTRAINT fk_users_organization;
ALTER TABLE users DROP COLUMN organization_id;
