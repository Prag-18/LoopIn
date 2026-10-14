-- Create schema for LoopIn Campus Borrowing Platform

CREATE TABLE IF NOT EXISTS item_requests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    item_name VARCHAR(255) NOT NULL,
    category VARCHAR(100),
    requester_name VARCHAR(255),
    requester_email VARCHAR(255) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    trust_radius INT NOT NULL,
    status VARCHAR(50) NOT NULL,
    note TEXT
);

CREATE TABLE IF NOT EXISTS item_listings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    category VARCHAR(100),
    owner_name VARCHAR(255),
    owner_email VARCHAR(255) NOT NULL,
    min_trust_degree INT NOT NULL,
    available BOOLEAN NOT NULL DEFAULT TRUE,
    condition_state VARCHAR(50)
);
