-- Identity: move avatars to user_images; drop fields that belong elsewhere or are obsolete.

CREATE TABLE user_images (
    id UUID NOT NULL PRIMARY KEY,
    user_id UUID NOT NULL,
    url VARCHAR(500) NOT NULL,
    type VARCHAR(100) NOT NULL,
    alt VARCHAR(100) NOT NULL,
    slug VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_user_images_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE INDEX idx_user_images_user_id ON user_images (user_id);
CREATE INDEX idx_user_images_user_type ON user_images (user_id, type);

-- Preserve existing avatar URLs as profile images (prefer profile_avatar_url over avatar).
INSERT INTO user_images (id, user_id, url, type, alt, slug, created_at, updated_at)
SELECT
    gen_random_uuid(),
    u.id,
    LEFT(COALESCE(NULLIF(TRIM(u.profile_avatar_url), ''), NULLIF(TRIM(u.avatar), '')), 500),
    'profile',
    'Profile avatar',
    'profile',
    u.created_at,
    u.updated_at
FROM users u
WHERE COALESCE(NULLIF(TRIM(u.profile_avatar_url), ''), NULLIF(TRIM(u.avatar), '')) IS NOT NULL;

ALTER TABLE users DROP COLUMN avatar;
ALTER TABLE users DROP COLUMN profile_avatar_url;
ALTER TABLE users DROP COLUMN department;
ALTER TABLE users DROP COLUMN job_title;
