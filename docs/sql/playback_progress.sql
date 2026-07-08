-- MySQL DDL for Playback Progress (replacement for DynamoDB table in dev)
CREATE TABLE IF NOT EXISTS playback_progress (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  media_id VARCHAR(200) NOT NULL,
  position_ms BIGINT DEFAULT 0,
  duration_ms BIGINT DEFAULT 0,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  version BIGINT DEFAULT 0,
  UNIQUE KEY uq_user_media (user_id, media_id),
  INDEX idx_user_updated (user_id, updated_at)
);

-- Example insert
-- INSERT INTO playback_progress (user_id, media_id, position_ms, duration_ms) VALUES (1, 'media-abc', 120000, 360000);

-- Idempotency keys table: store Idempotency-Key per user to prevent duplicate processing
CREATE TABLE IF NOT EXISTS playback_idempotency (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  idempotency_key VARCHAR(255) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  ttl_seconds INT NOT NULL DEFAULT 60,
  UNIQUE KEY uq_user_id_key (user_id, idempotency_key),
  INDEX idx_created_at (created_at)
);

-- Dead-letter events table: store events that failed to persist after retries
CREATE TABLE IF NOT EXISTS playback_events_dlq (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT,
  media_id VARCHAR(200),
  device VARCHAR(50),
  payload JSON,
  error_reason TEXT,
  retry_count INT DEFAULT 0,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  processed TINYINT(1) DEFAULT 0,
  INDEX idx_processed (processed),
  INDEX idx_user_created (user_id, created_at)
);

-- Audit log: append-only records for debugging and replay
CREATE TABLE IF NOT EXISTS playback_audit_log (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT,
  media_id VARCHAR(200),
  device VARCHAR(50),
  action VARCHAR(50) NOT NULL,
  position_ms BIGINT,
  duration_ms BIGINT,
  payload JSON,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_user_created (user_id, created_at)
);

-- Optional: Recent playback materialized table to serve paged queries efficiently
CREATE TABLE IF NOT EXISTS playback_recent (
  user_id BIGINT NOT NULL,
  media_id VARCHAR(200) NOT NULL,
  device VARCHAR(50),
  position_ms BIGINT,
  duration_ms BIGINT,
  updated_at TIMESTAMP NOT NULL,
  PRIMARY KEY (user_id, media_id, device),
  INDEX idx_user_updated (user_id, updated_at)
);

-- Example: insert audit and dlq samples
-- INSERT INTO playback_audit_log (user_id, media_id, action, position_ms, duration_ms, payload) VALUES (1, 'media-abc', 'SAVE', 120000, 360000, JSON_OBJECT('source','api'));
-- INSERT INTO playback_events_dlq (user_id, media_id, device, payload, error_reason) VALUES (1, 'media-abc', 'MOBILE', JSON_OBJECT('positionMs',120000), 'DB_TIMEOUT');
