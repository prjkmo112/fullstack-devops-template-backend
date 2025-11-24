CREATE TABLE users
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(255)                                       NOT NULL,
    email      VARCHAR(128)                                       NOT NULL,
    passwd     VARCHAR(255) CHARACTER SET ascii collate ascii_bin NOT NULL,
    role       VARCHAR(64)                                        NOT NULL,
    created_at DATETIME                                           NULL
);

