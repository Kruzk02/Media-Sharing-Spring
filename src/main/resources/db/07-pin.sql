CREATE TABLE IF NOT EXISTS pins (
	id int auto_increment primary key,
    user_id int NOT NULL,
    description text,
    media_id int NULL DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    INDEX idx_pins_created_id (created_at, id),

    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (media_id) REFERENCES media(id) ON DELETE CASCADE
);