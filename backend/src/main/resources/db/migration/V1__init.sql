-- ==========================================
-- V1__init.sql
-- Create vendors table
-- Java Developer 2 Task
-- ==========================================

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(150),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE vendors (

    -- Primary key
    id BIGSERIAL PRIMARY KEY,

    -- Vendor company name
    vendor_name VARCHAR(255) NOT NULL,

    -- Person we contact
    contact_person VARCHAR(255),

    -- Vendor email
    email VARCHAR(255) UNIQUE NOT NULL,

    -- Vendor phone number
    phone VARCHAR(20),

    -- Risk score between 0 to 100
    risk_score INTEGER DEFAULT 0,

    -- LOW / MEDIUM / HIGH / PENDING
    status VARCHAR(50) DEFAULT 'PENDING',

    -- Notes or AI summary
    description TEXT,

    -- Date for next review
    review_date DATE,

    -- Soft delete support
    deleted BOOLEAN DEFAULT FALSE,

    -- Auto timestamps
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ==========================================
-- Indexes for faster search
-- ==========================================

CREATE INDEX idx_vendor_name ON vendors(vendor_name);
CREATE INDEX idx_status ON vendors(status);
CREATE INDEX idx_review_date ON vendors(review_date);
CREATE INDEX idx_email ON vendors(email);