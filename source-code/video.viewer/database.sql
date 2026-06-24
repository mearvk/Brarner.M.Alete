-- Video Viewer — Database setup script
-- Creates the video_viewer schema and history table

CREATE DATABASE IF NOT EXISTS video_viewer;
USE video_viewer;

CREATE TABLE IF NOT EXISTS history (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    url        VARCHAR(2048) NOT NULL,
    title      VARCHAR(512),
    played_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS config (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    param_key  VARCHAR(128) NOT NULL UNIQUE,
    param_val  VARCHAR(512) NOT NULL
);

INSERT IGNORE INTO config (param_key, param_val) VALUES
    ('width', '1280'),
    ('height', '720'),
    ('title', 'Video Viewer');
