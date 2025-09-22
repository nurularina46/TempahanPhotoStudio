-- SQL Server Database Setup for TempahanPhotoStudio
-- Run this script in your SQL Server Management Studio

USE TempahanPhotoStudio;
GO

-- Create Users table (matching your existing structure)
CREATE TABLE users (
    id INT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(50) NOT NULL,
    email NVARCHAR(100) UNIQUE NOT NULL,
    password NVARCHAR(255) NOT NULL
);
GO

-- Create Packages table
CREATE TABLE packages (
    id INT IDENTITY(1,1) PRIMARY KEY,
    package_name NVARCHAR(100) NOT NULL,
    event NVARCHAR(50) NOT NULL,
    duration NVARCHAR(50) NOT NULL,
    category NVARCHAR(50) NOT NULL,
    description NVARCHAR(500),
    image_url NVARCHAR(255)
);
GO

-- Create Sub Packages table
CREATE TABLE sub_packages (
    id INT IDENTITY(1,1) PRIMARY KEY,
    package_id INT NOT NULL,
    package_class NVARCHAR(20) NOT NULL,
    details NVARCHAR(500) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    description NVARCHAR(500),
    FOREIGN KEY (package_id) REFERENCES packages(id) ON DELETE CASCADE
);
GO

-- Create Bookings table
CREATE TABLE bookings (
    id INT IDENTITY(1,1) PRIMARY KEY,
    user_id INT NOT NULL,
    package_id INT NOT NULL,
    sub_package_id INT NOT NULL,
    booking_date NVARCHAR(20) NOT NULL,
    event_date NVARCHAR(20) NOT NULL,
    status NVARCHAR(20) NOT NULL DEFAULT 'Pending',
    total_amount DECIMAL(10,2) NOT NULL,
    payment_method NVARCHAR(50) NOT NULL,
    payment_status NVARCHAR(20) NOT NULL DEFAULT 'Pending',
    notes NVARCHAR(500),
    created_at NVARCHAR(50) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (package_id) REFERENCES packages(id),
    FOREIGN KEY (sub_package_id) REFERENCES sub_packages(id)
);
GO

-- Insert sample data (matching your existing data structure)
-- Insert Admin user
INSERT INTO users (name, email, password) 
VALUES ('admin', 'admin@photostudio.com', 'admin123');
GO

-- Insert Regular user
INSERT INTO users (name, email, password) 
VALUES ('user', 'user@example.com', 'user123');
GO

-- Insert sample packages
INSERT INTO packages (package_name, event, duration, category, description, image_url) 
VALUES 
('Wedding Photography', 'Wedding', '8 hours', 'Photography', 'Professional wedding photography service', ''),
('Event Videography', 'Corporate Event', '6 hours', 'Videography', 'Professional event videography service', ''),
('Portrait Photography', 'Portrait', '2 hours', 'Photography', 'Professional portrait photography', ''),
('Product Photography', 'Commercial', '4 hours', 'Photography', 'Product and commercial photography', '');
GO

-- Insert sample sub-packages
INSERT INTO sub_packages (package_id, package_class, details, price, description) 
VALUES 
(1, 'Regular', 'Basic wedding package', 500.00, 'Includes 100 edited photos'),
(1, 'Advance', 'Advanced wedding package', 800.00, 'Includes 200 edited photos + album'),
(1, 'Premium', 'Premium wedding package', 1200.00, 'Includes 300 edited photos + album + video'),
(2, 'Regular', 'Basic videography package', 600.00, 'Includes 2-hour video'),
(2, 'Advance', 'Advanced videography package', 900.00, 'Includes 4-hour video + editing'),
(2, 'Premium', 'Premium videography package', 1400.00, 'Includes 6-hour video + editing + drone shots'),
(3, 'Regular', 'Basic portrait session', 150.00, '30-minute session, 10 edited photos'),
(3, 'Advance', 'Advanced portrait session', 250.00, '1-hour session, 20 edited photos'),
(3, 'Premium', 'Premium portrait session', 350.00, '2-hour session, 30 edited photos + prints'),
(4, 'Regular', 'Basic product photography', 200.00, 'Up to 10 products, basic editing'),
(4, 'Advance', 'Advanced product photography', 350.00, 'Up to 20 products, advanced editing'),
(4, 'Premium', 'Premium product photography', 500.00, 'Up to 30 products, premium editing + retouching');
GO

-- Verify data
SELECT 'Users' as TableName, COUNT(*) as RecordCount FROM users
UNION ALL
SELECT 'Packages', COUNT(*) FROM packages
UNION ALL
SELECT 'Sub Packages', COUNT(*) FROM sub_packages
UNION ALL
SELECT 'Bookings', COUNT(*) FROM bookings;
GO

PRINT 'Database setup completed successfully!';
PRINT 'Admin login: admin@photostudio.com / admin123';
PRINT 'User login: user@example.com / user123';
GO
