-- V2: Create a simple dummy table to verify Flyway runs

CREATE TABLE IF NOT EXISTS flyway_test_dummy (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    PRIMARY KEY (id)
) ENGINE=InnoDB;
