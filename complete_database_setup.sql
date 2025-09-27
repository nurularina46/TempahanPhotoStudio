-- =====================================================
-- COMPLETE DATABASE SETUP FOR TEMPAHAN PHOTO STUDIO
-- =====================================================
-- Jalankan script ini dalam SQL Server Management Studio

USE TempahanPhotoStudio;
GO

-- =====================================================
-- 1. CREATE USERS TABLE
-- =====================================================
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'users')
BEGIN
    CREATE TABLE users (
        id INT IDENTITY(1,1) PRIMARY KEY,
        name NVARCHAR(100) NOT NULL,
        email NVARCHAR(100) UNIQUE NOT NULL,
        password NVARCHAR(255) NOT NULL,
        role NVARCHAR(20) DEFAULT 'User',
        phone_number NVARCHAR(20),
        created_at DATETIME DEFAULT GETDATE()
    );
    PRINT 'users table created successfully!';
END
ELSE
BEGIN
    PRINT 'users table already exists.';
END
GO

-- =====================================================
-- 2. CREATE PACKAGES TABLE
-- =====================================================
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'packages')
BEGIN
    CREATE TABLE packages (
        id INT IDENTITY(1,1) PRIMARY KEY,
        package_name NVARCHAR(100) NOT NULL,
        event NVARCHAR(100) NOT NULL,
        duration NVARCHAR(50) NOT NULL,
        category NVARCHAR(50) NOT NULL,
        description NVARCHAR(500),
        image_url NVARCHAR(200),
        created_at DATETIME DEFAULT GETDATE()
    );
    PRINT 'packages table created successfully!';
END
ELSE
BEGIN
    PRINT 'packages table already exists.';
END
GO

-- =====================================================
-- 3. CREATE VIDEography_SUB_PACKAGES TABLE
-- =====================================================
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'videography_sub_packages')
BEGIN
    CREATE TABLE videography_sub_packages (
        id INT IDENTITY(1,1) PRIMARY KEY,
        package_id INT NOT NULL,
        package_class NVARCHAR(50) NOT NULL,
        price DECIMAL(10,2) NOT NULL,
        details NVARCHAR(500) NOT NULL,
        created_at DATETIME DEFAULT GETDATE(),
        updated_at DATETIME DEFAULT GETDATE(),
        FOREIGN KEY (package_id) REFERENCES packages(id) ON DELETE CASCADE
    );
    PRINT 'videography_sub_packages table created successfully!';
END
ELSE
BEGIN
    PRINT 'videography_sub_packages table already exists.';
END
GO

-- =====================================================
-- 4. CREATE BOOKINGS TABLE
-- =====================================================
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'bookings')
BEGIN
    CREATE TABLE bookings (
        id INT IDENTITY(1,1) PRIMARY KEY,
        user_id INT NOT NULL,
        package_id INT NOT NULL,
        sub_package_id INT,
        booking_date DATETIME NOT NULL,
        event_date DATETIME NOT NULL,
        status NVARCHAR(20) DEFAULT 'Pending',
        total_amount DECIMAL(10,2) NOT NULL,
        payment_method NVARCHAR(50),
        payment_status NVARCHAR(20) DEFAULT 'Pending',
        notes NVARCHAR(500),
        created_at DATETIME DEFAULT GETDATE(),
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
        FOREIGN KEY (package_id) REFERENCES packages(id) ON DELETE CASCADE
    );
    PRINT 'bookings table created successfully!';
END
ELSE
BEGIN
    PRINT 'bookings table already exists.';
END
GO

-- =====================================================
-- 5. INSERT SAMPLE USERS
-- =====================================================
-- Insert Admin User
IF NOT EXISTS (SELECT * FROM users WHERE email = 'admin@photostudio.com')
BEGIN
    INSERT INTO users (name, email, password, role) 
    VALUES ('Admin PhotoStudio', 'admin@photostudio.com', 'admin123', 'Admin');
    PRINT 'Admin user created successfully!';
END

-- Insert Test User
IF NOT EXISTS (SELECT * FROM users WHERE email = 'user@test.com')
BEGIN
    INSERT INTO users (name, email, password, role) 
    VALUES ('Test User', 'user@test.com', 'user123', 'User');
    PRINT 'Test user created successfully!';
END
GO

-- =====================================================
-- 6. INSERT SAMPLE PACKAGES
-- =====================================================
-- Photography Packages
INSERT INTO packages (package_name, event, duration, category, description) 
VALUES 
('Wedding Photography - Full Day', 'Wedding', '8-10 hours', 'Photography', 'Complete wedding day photography coverage from preparation to reception'),
('Wedding Photography - Half Day', 'Wedding', '4-5 hours', 'Photography', 'Wedding ceremony and reception photography'),
('Portrait Photography', 'Portrait', '2-3 hours', 'Photography', 'Professional portrait photography session'),
('Event Photography', 'Corporate Event', '4-6 hours', 'Photography', 'Corporate event photography documentation'),
('Graduation Photography', 'Graduation', '2-3 hours', 'Photography', 'Graduation ceremony photography');
GO

-- Videography Packages
INSERT INTO packages (package_name, event, duration, category, description) 
VALUES 
('Wedding Videography - Full Day', 'Wedding', '8-10 hours', 'Videography', 'Complete wedding day videography coverage from preparation to reception'),
('Wedding Videography - Half Day', 'Wedding', '4-5 hours', 'Videography', 'Wedding ceremony and reception videography'),
('Corporate Event Videography', 'Corporate Event', '6-8 hours', 'Videography', 'Professional corporate event documentation'),
('Birthday Party Videography', 'Birthday Party', '4-6 hours', 'Videography', 'Birthday celebration videography'),
('Music Video Production', 'Music Video', '6-10 hours', 'Videography', 'Professional music video production');
GO

-- =====================================================
-- 7. INSERT SAMPLE VIDEography_SUB_PACKAGES
-- =====================================================
-- Get package IDs for videography packages
DECLARE @wedding_vid_full_id INT = (SELECT id FROM packages WHERE package_name = 'Wedding Videography - Full Day');
DECLARE @wedding_vid_half_id INT = (SELECT id FROM packages WHERE package_name = 'Wedding Videography - Half Day');
DECLARE @corporate_vid_id INT = (SELECT id FROM packages WHERE package_name = 'Corporate Event Videography');
DECLARE @birthday_vid_id INT = (SELECT id FROM packages WHERE package_name = 'Birthday Party Videography');
DECLARE @music_vid_id INT = (SELECT id FROM packages WHERE package_name = 'Music Video Production');

-- Insert sub-packages for each videography package
INSERT INTO videography_sub_packages (package_id, package_class, price, details) 
VALUES 
-- Wedding Videography - Full Day
(@wedding_vid_full_id, 'Regular', 1200.00, '1 Event 1 Video | Akad Nikah / Sanding (1 Day 1 Event) | 6-7 Hours Per Day | 1 x Full Showreel | 4-6 Minute Highlight Video | 1 x Exclusive Pendrive 16 GB & Case'),
(@wedding_vid_full_id, 'Advance', 1800.00, '1 Event 1 Video | Akad Nikah / Sanding (1 Day 1 Event) | 6-7 Hours Per Day | 1 x Full Showreel | 4-6 Minute Highlight Video | 1 x Exclusive Pendrive 16 GB & Case | Drone 1/Per Day RM500'),
(@wedding_vid_full_id, 'Premium', 2500.00, '1 Event 1 Video | Akad Nikah / Sanding (1 Day 1 Event) | 6-7 Hours Per Day | 1 x Full Showreel | 4-6 Minute Highlight Video | 1 x Exclusive Pendrive 16 GB & Case | Drone 1/Per Day RM500 | +1 Videographer Per Day RM500'),

-- Wedding Videography - Half Day
(@wedding_vid_half_id, 'Regular', 800.00, '1 Event 1 Video | Akad Nikah / Sanding (Half Day) | 4-5 Hours Per Day | 1 x Full Showreel | 4-6 Minute Highlight Video | 1 x Exclusive Pendrive 16 GB & Case'),
(@wedding_vid_half_id, 'Advance', 1200.00, '1 Event 1 Video | Akad Nikah / Sanding (Half Day) | 4-5 Hours Per Day | 1 x Full Showreel | 4-6 Minute Highlight Video | 1 x Exclusive Pendrive 16 GB & Case | Drone 1/Per Day RM500'),
(@wedding_vid_half_id, 'Premium', 1600.00, '1 Event 1 Video | Akad Nikah / Sanding (Half Day) | 4-5 Hours Per Day | 1 x Full Showreel | 4-6 Minute Highlight Video | 1 x Exclusive Pendrive 16 GB & Case | Drone 1/Per Day RM500 | +1 Videographer Per Day RM500'),

-- Corporate Event Videography
(@corporate_vid_id, 'Regular', 1000.00, 'Corporate Event Documentation | 6-8 Hours | Professional Setup | 1 x Full Event Video | Highlight Reel | Raw Footage'),
(@corporate_vid_id, 'Advance', 1500.00, 'Corporate Event Documentation | 6-8 Hours | Professional Setup | 1 x Full Event Video | Highlight Reel | Raw Footage | Drone Coverage'),
(@corporate_vid_id, 'Premium', 2000.00, 'Corporate Event Documentation | 6-8 Hours | Professional Setup | 1 x Full Event Video | Highlight Reel | Raw Footage | Drone Coverage | Multiple Camera Angles'),

-- Birthday Party Videography
(@birthday_vid_id, 'Regular', 400.00, 'Birthday Party Coverage | 4-6 Hours | Fun & Energetic Style | 1 x Party Video | Highlight Reel | Raw Footage'),
(@birthday_vid_id, 'Advance', 600.00, 'Birthday Party Coverage | 4-6 Hours | Fun & Energetic Style | 1 x Party Video | Highlight Reel | Raw Footage | Drone Shots'),
(@birthday_vid_id, 'Premium', 800.00, 'Birthday Party Coverage | 4-6 Hours | Fun & Energetic Style | 1 x Party Video | Highlight Reel | Raw Footage | Drone Shots | Multiple Cameras'),

-- Music Video Production
(@music_vid_id, 'Regular', 1500.00, 'Music Video Production | 6-10 Hours | Creative Direction | 1 x Music Video | Behind The Scenes | Raw Footage'),
(@music_vid_id, 'Advance', 2500.00, 'Music Video Production | 6-10 Hours | Creative Direction | 1 x Music Video | Behind The Scenes | Raw Footage | Drone Shots'),
(@music_vid_id, 'Premium', 4000.00, 'Music Video Production | 6-10 Hours | Creative Direction | 1 x Music Video | Behind The Scenes | Raw Footage | Drone Shots | Multiple Locations');
GO

-- =====================================================
-- 8. VERIFY DATA
-- =====================================================
-- Check users
SELECT 'USERS' as Table_Name, COUNT(*) as Record_Count FROM users
UNION ALL
-- Check packages
SELECT 'PACKAGES' as Table_Name, COUNT(*) as Record_Count FROM packages
UNION ALL
-- Check videography sub-packages
SELECT 'VIDEOGRAPHY_SUB_PACKAGES' as Table_Name, COUNT(*) as Record_Count FROM videography_sub_packages
UNION ALL
-- Check bookings
SELECT 'BOOKINGS' as Table_Name, COUNT(*) as Record_Count FROM bookings;
GO

-- Show sample data
SELECT 'PACKAGES' as Table_Name, id, package_name, category FROM packages ORDER BY category, id;
GO

SELECT 'VIDEOGRAPHY_SUB_PACKAGES' as Table_Name, id, package_id, package_class, price FROM videography_sub_packages ORDER BY package_id, package_class;
GO

PRINT '=====================================================';
PRINT 'DATABASE SETUP COMPLETED SUCCESSFULLY!';
PRINT '=====================================================';
PRINT 'Total Users: ' + CAST((SELECT COUNT(*) FROM users) AS NVARCHAR(10));
PRINT 'Total Packages: ' + CAST((SELECT COUNT(*) FROM packages) AS NVARCHAR(10));
PRINT 'Total Videography Sub-Packages: ' + CAST((SELECT COUNT(*) FROM videography_sub_packages) AS NVARCHAR(10));
PRINT '=====================================================';
GO
