-- Preserve the question and answer options shown in a game session.
ALTER TABLE session_questions
    ADD COLUMN title VARCHAR(500);

ALTER TABLE session_questions
    ADD COLUMN description TEXT;

ALTER TABLE session_questions
    ADD COLUMN question_type VARCHAR(20);

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

ALTER TABLE player_answers
    ADD COLUMN session_answer_option_id UUID;

ALTER TABLE player_answers
    ADD CONSTRAINT fk_player_answers_session_answer_option
        FOREIGN KEY (session_answer_option_id) REFERENCES session_answer_options (id);

CREATE INDEX idx_session_answer_options_session_question_id
    ON session_answer_options (session_question_id);
