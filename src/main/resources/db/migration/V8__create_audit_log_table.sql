CREATE TABLE audit_log (

    id BIGSERIAL PRIMARY KEY,

    username VARCHAR(255),

    action VARCHAR(100) NOT NULL,

    entity_name VARCHAR(100),

    entity_id BIGINT,

    created_at TIMESTAMP NOT NULL
);