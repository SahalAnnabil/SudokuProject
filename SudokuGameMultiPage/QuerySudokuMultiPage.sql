CREATE DATABASE IF NOT EXISTS sudoku_game_db_multi_page CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE sudoku_game_db_multi_page;

CREATE TABLE IF NOT EXISTS saved_games (
    level_difficulty VARCHAR(10) NOT NULL PRIMARY KEY, -- 'beginner', 'easy', 'medium', 'hard', 'extreme'
    initial_board VARCHAR(100) NOT NULL,
    current_board VARCHAR(100) NOT NULL,
    time_elapsed_seconds INT DEFAULT 0,
    last_played_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS game_history (
    history_id INT AUTO_INCREMENT PRIMARY KEY,
    game_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    duration_seconds INT NOT NULL,
    solved_by_user BOOLEAN NOT NULL,
    difficulty VARCHAR(10) NOT NULL, -- Pastikan NOT NULL jika ini krusial untuk filter
    initial_board VARCHAR(100) NOT NULL
);

SELECT * FROM saved_games;
SELECT * FROM game_history;