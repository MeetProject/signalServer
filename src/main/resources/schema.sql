CREATE TABLE member (
    id       VARCHAR(36)  PRIMARY KEY,
    name     VARCHAR(20)  NOT NULL,
    profile_color VARCHAR(20),
    created_at    TIMESTAMP    NOT NULL
);

CREATE TABLE room (
    id     VARCHAR(10) PRIMARY KEY,
    created_at  TIMESTAMP   NOT NULL
);