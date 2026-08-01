-- Baseline schema (DDL only). Reflects the current application model.
-- Reference data is loaded by Java seeders (app.seed), not by Flyway.

-- ---------------------------------------------------------------------------
-- Identity
-- ---------------------------------------------------------------------------

CREATE TABLE permissions (
    id UUID NOT NULL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    module VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT uq_permissions_name_module UNIQUE (name, module)
);

CREATE TABLE roles (
    id UUID NOT NULL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(30) NOT NULL,
    description VARCHAR(255),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
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
    role_id UUID,
    email VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(80) NOT NULL,
    last_name VARCHAR(80) NOT NULL,
    status VARCHAR(20) NOT NULL,
    phone_number VARCHAR(30),
    birth_date DATE,
    bio TEXT,
    location VARCHAR(150),
    last_login TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT uq_users_email UNIQUE (email),
    CONSTRAINT fk_users_role FOREIGN KEY (role_id) REFERENCES roles (id)
);

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

-- ---------------------------------------------------------------------------
-- Organization
-- ---------------------------------------------------------------------------

CREATE TABLE organizations (
    id UUID NOT NULL PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    slug VARCHAR(100) NOT NULL,
    logo_url VARCHAR(500),
    description TEXT,
    timezone VARCHAR(64) NOT NULL,
    language VARCHAR(10) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT uq_organizations_slug UNIQUE (slug)
);

CREATE TABLE organization_members (
    id UUID NOT NULL PRIMARY KEY,
    organization_id UUID NOT NULL,
    user_id UUID NOT NULL,
    role_id UUID,
    status VARCHAR(20) NOT NULL,
    joined_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT uq_organization_members_org_user UNIQUE (organization_id, user_id),
    CONSTRAINT fk_organization_members_organization FOREIGN KEY (organization_id) REFERENCES organizations (id)
);

CREATE INDEX idx_organization_members_user_id ON organization_members (user_id);

-- ---------------------------------------------------------------------------
-- Quiz
-- ---------------------------------------------------------------------------

CREATE TABLE quizzes (
    id UUID NOT NULL PRIMARY KEY,
    organization_id UUID NOT NULL,
    created_by UUID NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    thumbnail_url VARCHAR(500),
    visibility VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    difficulty VARCHAR(20) NOT NULL,
    estimated_time_minutes INTEGER,
    play_count INTEGER NOT NULL,
    average_rating NUMERIC(3, 2) NOT NULL,
    is_template BOOLEAN NOT NULL,
    random_questions BOOLEAN NOT NULL,
    random_answers BOOLEAN NOT NULL,
    show_correct_answer BOOLEAN NOT NULL,
    show_ranking BOOLEAN NOT NULL,
    allow_retry BOOLEAN NOT NULL,
    show_timer BOOLEAN NOT NULL,
    music_enabled BOOLEAN NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE categories (
    id UUID NOT NULL PRIMARY KEY,
    organization_id UUID NOT NULL,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    color VARCHAR(20),
    icon VARCHAR(50),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT uq_categories_organization_name UNIQUE (organization_id, name)
);

CREATE TABLE quiz_categories (
    quiz_id UUID NOT NULL,
    category_id UUID NOT NULL,
    PRIMARY KEY (quiz_id, category_id),
    CONSTRAINT fk_quiz_categories_quiz FOREIGN KEY (quiz_id) REFERENCES quizzes (id),
    CONSTRAINT fk_quiz_categories_category FOREIGN KEY (category_id) REFERENCES categories (id)
);

CREATE TABLE questions (
    id UUID NOT NULL PRIMARY KEY,
    quiz_id UUID NOT NULL,
    title VARCHAR(500) NOT NULL,
    description TEXT,
    type VARCHAR(20) NOT NULL,
    difficulty VARCHAR(20) NOT NULL,
    explanation TEXT,
    order_index INTEGER NOT NULL,
    time_limit_seconds INTEGER NOT NULL,
    points INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT uq_questions_quiz_order UNIQUE (quiz_id, order_index),
    CONSTRAINT fk_questions_quiz FOREIGN KEY (quiz_id) REFERENCES quizzes (id)
);

CREATE TABLE answer_options (
    id UUID NOT NULL PRIMARY KEY,
    question_id UUID NOT NULL,
    text VARCHAR(500) NOT NULL,
    is_correct BOOLEAN NOT NULL,
    explanation TEXT,
    order_index INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT uq_answer_options_question_order UNIQUE (question_id, order_index),
    CONSTRAINT fk_answer_options_question FOREIGN KEY (question_id) REFERENCES questions (id)
);

CREATE TABLE question_assets (
    id UUID NOT NULL PRIMARY KEY,
    question_id UUID NOT NULL UNIQUE,
    type VARCHAR(20) NOT NULL,
    url VARCHAR(1000) NOT NULL,
    thumbnail_url VARCHAR(1000),
    alt_text VARCHAR(255),
    duration_seconds INTEGER,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_question_assets_question FOREIGN KEY (question_id) REFERENCES questions (id)
);

CREATE INDEX idx_quizzes_organization_id ON quizzes (organization_id);
CREATE INDEX idx_quizzes_created_by ON quizzes (created_by);

-- ---------------------------------------------------------------------------
-- Gameplay (removed by V3__drop_gameplay.sql — kept here because V1 was already applied)
-- ---------------------------------------------------------------------------

CREATE TABLE game_sessions (
    id UUID NOT NULL PRIMARY KEY,
    organization_id UUID NOT NULL,
    quiz_id UUID NOT NULL,
    host_user_id UUID NOT NULL,
    game_pin VARCHAR(6) UNIQUE,
    status VARCHAR(20) NOT NULL,
    current_question_index INTEGER NOT NULL,
    started_at TIMESTAMP,
    finished_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE session_players (
    id UUID NOT NULL PRIMARY KEY,
    session_id UUID NOT NULL,
    user_id UUID,
    nickname VARCHAR(30) NOT NULL,
    score INTEGER NOT NULL,
    connected BOOLEAN NOT NULL,
    joined_at TIMESTAMP NOT NULL,
    left_at TIMESTAMP,
    CONSTRAINT uq_session_players_session_nickname UNIQUE (session_id, nickname),
    CONSTRAINT fk_session_players_session FOREIGN KEY (session_id) REFERENCES game_sessions (id)
);

CREATE TABLE session_questions (
    id UUID NOT NULL PRIMARY KEY,
    session_id UUID NOT NULL,
    quiz_question_id UUID NOT NULL,
    order_index INTEGER NOT NULL,
    points INTEGER NOT NULL,
    time_limit_seconds INTEGER NOT NULL,
    title VARCHAR(500),
    description TEXT,
    question_type VARCHAR(20),
    opened_at TIMESTAMP,
    closed_at TIMESTAMP,
    CONSTRAINT uq_session_questions_session_order UNIQUE (session_id, order_index),
    CONSTRAINT fk_session_questions_session FOREIGN KEY (session_id) REFERENCES game_sessions (id)
);

CREATE TABLE session_answer_options (
    id UUID NOT NULL PRIMARY KEY,
    session_question_id UUID NOT NULL,
    original_answer_option_id UUID NOT NULL,
    text VARCHAR(500) NOT NULL,
    is_correct BOOLEAN NOT NULL,
    order_index INTEGER NOT NULL,
    CONSTRAINT uq_session_answer_options_question_order
        UNIQUE (session_question_id, order_index),
    CONSTRAINT fk_session_answer_options_session_question
        FOREIGN KEY (session_question_id) REFERENCES session_questions (id)
);

CREATE TABLE player_answers (
    id UUID NOT NULL PRIMARY KEY,
    session_question_id UUID NOT NULL,
    session_player_id UUID NOT NULL,
    answer_option_id UUID,
    session_answer_option_id UUID,
    is_correct BOOLEAN NOT NULL,
    response_time_ms BIGINT NOT NULL,
    awarded_points INTEGER NOT NULL,
    answered_at TIMESTAMP NOT NULL,
    CONSTRAINT uq_player_answers_question_player UNIQUE (session_question_id, session_player_id),
    CONSTRAINT fk_player_answers_session_question
        FOREIGN KEY (session_question_id) REFERENCES session_questions (id),
    CONSTRAINT fk_player_answers_session_player
        FOREIGN KEY (session_player_id) REFERENCES session_players (id),
    CONSTRAINT fk_player_answers_answer_option
        FOREIGN KEY (answer_option_id) REFERENCES answer_options (id),
    CONSTRAINT fk_player_answers_session_answer_option
        FOREIGN KEY (session_answer_option_id) REFERENCES session_answer_options (id)
);

CREATE TABLE session_leaderboard (
    id UUID NOT NULL PRIMARY KEY,
    session_id UUID NOT NULL,
    session_player_id UUID NOT NULL,
    position INTEGER NOT NULL,
    nickname VARCHAR(30) NOT NULL,
    score INTEGER NOT NULL,
    correct_answers INTEGER NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT uq_session_leaderboard_position UNIQUE (session_id, position),
    CONSTRAINT uq_session_leaderboard_player UNIQUE (session_id, session_player_id),
    CONSTRAINT fk_session_leaderboard_session FOREIGN KEY (session_id) REFERENCES game_sessions (id),
    CONSTRAINT fk_session_leaderboard_player FOREIGN KEY (session_player_id) REFERENCES session_players (id)
);

CREATE INDEX idx_game_sessions_organization_id ON game_sessions (organization_id);
CREATE INDEX idx_game_sessions_quiz_id ON game_sessions (quiz_id);
CREATE INDEX idx_game_sessions_host_user_id ON game_sessions (host_user_id);
CREATE INDEX idx_session_players_user_id ON session_players (user_id);
CREATE INDEX idx_session_answer_options_session_question_id ON session_answer_options (session_question_id);
CREATE INDEX idx_player_answers_session_player_id ON player_answers (session_player_id);
CREATE INDEX idx_session_leaderboard_player_id ON session_leaderboard (session_player_id);
