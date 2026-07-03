CREATE TABLE IF NOT EXISTS member (
    id       VARCHAR(36)  PRIMARY KEY,
    name     VARCHAR(20)  NOT NULL,
    profile_color VARCHAR(20),
    created_at    TIMESTAMP    NOT NULL
);

CREATE TABLE IF NOT EXISTS room (
    id     VARCHAR(10) PRIMARY KEY,
    created_at  TIMESTAMP   NOT NULL
);