-- Foundation baseline.
-- Domain tables will be added in later Flyway migrations (see docs/especificacion-tablas.md).

CREATE TABLE foundation_marker (
    id INTEGER PRIMARY KEY,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
