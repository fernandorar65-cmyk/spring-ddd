-- Align the existing identity and organization schema with its JPA mappings.
ALTER TABLE organizations RENAME COLUMN logo TO logo_url;

ALTER TABLE permissions
    ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE roles
    ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE organization_members
    ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;

-- Quiz bounded context.
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

-- Gameplay bounded context.
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
    opened_at TIMESTAMP,
    closed_at TIMESTAMP,
    CONSTRAINT uq_session_questions_session_order UNIQUE (session_id, order_index),
    CONSTRAINT fk_session_questions_session FOREIGN KEY (session_id) REFERENCES game_sessions (id)
);

CREATE TABLE player_answers (
    id UUID NOT NULL PRIMARY KEY,
    session_question_id UUID NOT NULL,
    session_player_id UUID NOT NULL,
    answer_option_id UUID,
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
        FOREIGN KEY (answer_option_id) REFERENCES answer_options (id)
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
