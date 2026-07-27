-- The Quiz and Gameplay aggregates navigate their child collections through
-- the existing foreign-key columns created in V4. These indexes keep those
-- aggregate reads efficient without changing an already-applied migration.

CREATE INDEX idx_quizzes_organization_id ON quizzes (organization_id);
CREATE INDEX idx_quizzes_created_by ON quizzes (created_by);

CREATE INDEX idx_game_sessions_organization_id ON game_sessions (organization_id);
CREATE INDEX idx_game_sessions_quiz_id ON game_sessions (quiz_id);
CREATE INDEX idx_game_sessions_host_user_id ON game_sessions (host_user_id);

CREATE INDEX idx_session_players_user_id ON session_players (user_id);
CREATE INDEX idx_player_answers_session_player_id ON player_answers (session_player_id);
CREATE INDEX idx_session_leaderboard_player_id ON session_leaderboard (session_player_id);
