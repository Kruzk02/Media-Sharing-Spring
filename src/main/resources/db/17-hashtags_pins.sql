CREATE TABLE IF NOT EXISTS hashtags_pins(
    hashtag_id INT,
    pin_id INT,

    INDEX idx_hashtag_pin_created_id(hashtag_id, pin_id),

    FOREIGN KEY (hashtag_id) REFERENCES hashtags(id) ON DELETE CASCADE,
    FOREIGN KEY (pin_id) REFERENCES pins(id) ON DELETE CASCADE
);