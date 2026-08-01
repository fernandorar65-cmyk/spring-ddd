-- Remove gameplay bounded context tables (flow deferred until requirements are clear).
-- Safe for databases that already applied V1 with gameplay DDL.

DROP TABLE IF EXISTS player_answers CASCADE;
DROP TABLE IF EXISTS session_leaderboard CASCADE;
DROP TABLE IF EXISTS session_answer_options CASCADE;
DROP TABLE IF EXISTS session_questions CASCADE;
DROP TABLE IF EXISTS session_players CASCADE;
DROP TABLE IF EXISTS game_sessions CASCADE;
