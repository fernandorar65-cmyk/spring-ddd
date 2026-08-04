-- Visibility was unused: access is already scoped by organization_id.
ALTER TABLE quizzes DROP COLUMN IF EXISTS visibility;
