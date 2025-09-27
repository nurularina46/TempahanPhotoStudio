-- =====================================================
-- SETUP DATABASE UNTUK VIDEOGRAPHY SUB-PACKAGES
-- =====================================================
-- Berdasarkan struktur database yang ditunjukkan dalam gambar

USE TempahanPhotoStudio;
GO

-- =====================================================
-- 1. CREATE PACKAGES TABLE (JIKA BELUM WUJUD)
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
    PRINT 'Created packages table';
END
ELSE
BEGIN
    PRINT 'packages table already exists';
END
GO

-- =====================================================
-- 2. CREATE VIDEOGRAPHY_SUB_PACKAGES TABLE
-- =====================================================
IF EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'videography_sub_packages')
BEGIN
    DROP TABLE videography_sub_packages;
    PRINT 'Dropped existing videography_sub_packages table';
END

CREATE TABLE videography_sub_packages (
    id INT IDENTITY(1,1) PRIMARY KEY,
    package_id INT NOT NULL,
    package_class NVARCHAR(50) NOT NULL,
    details NVARCHAR(500) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    description NVARCHAR(500),
    is_active BIT DEFAULT 1,
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (package_id) REFERENCES packages(id) ON DELETE CASCADE
);
PRINT 'Created videography_sub_packages table with correct structure';
GO

-- =====================================================
-- 3. CREATE USERS TABLE (JIKA BELUM WUJUD)
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
    PRINT 'Created users table';
END
ELSE
BEGIN
    PRINT 'users table already exists';
END
GO

-- =====================================================
-- 4. INSERT SAMPLE DATA
-- =====================================================

-- Insert Admin User
IF NOT EXISTS (SELECT * FROM users WHERE email = 'admin@photostudio.com')
BEGIN
    INSERT INTO users (name, email, password, role) 
    VALUES ('Admin PhotoStudio', 'admin@photostudio.com', 'admin123', 'Admin');
    PRINT 'Admin user created';
END

-- Insert Test User
IF NOT EXISTS (SELECT * FROM users WHERE email = 'user@test.com')
BEGIN
    INSERT INTO users (name, email, password, role) 
    VALUES ('Test User', 'user@test.com', 'user123', 'User');
    PRINT 'Test user created';
END

-- Insert Videography Packages
INSERT INTO packages (package_name, event, duration, category, description) 
VALUES 
('Wedding Videography - Full Day', 'Wedding', '8-10 hours', 'Videography', 'Complete wedding day videography coverage from preparation to reception'),
('Wedding Videography - Half Day', 'Wedding', '4-5 hours', 'Videography', 'Wedding ceremony and reception videography'),
('Corporate Event Videography', 'Corporate Event', '6-8 hours', 'Videography', 'Professional corporate event documentation'),
('Birthday Party Videography', 'Birthday Party', '4-6 hours', 'Videography', 'Birthday celebration videography'),
('Music Video Production', 'Music Video', '6-10 hours', 'Videography', 'Professional music video production');
PRINT 'Videography packages inserted';
GO

-- =====================================================
-- 5. INSERT SAMPLE SUB-PACKAGES
-- =====================================================
-- Get package IDs
DECLARE @pkg1 INT = (SELECT id FROM packages WHERE package_name = 'Wedding Videography - Full Day');
DECLARE @pkg2 INT = (SELECT id FROM packages WHERE package_name = 'Wedding Videography - Half Day');
DECLARE @pkg3 INT = (SELECT id FROM packages WHERE package_name = 'Corporate Event Videography');
DECLARE @pkg4 INT = (SELECT id FROM packages WHERE package_name = 'Birthday Party Videography');
DECLARE @pkg5 INT = (SELECT id FROM packages WHERE package_name = 'Music Video Production');

-- Insert sub-packages
INSERT INTO videography_sub_packages (package_id, package_class, details, price, description, is_active) 
VALUES 
-- Wedding Videography - Full Day
(@pkg1, 'Regular', '1 Event 1 Video | Akad Nikah / Sanding (1 Day 1 Event) | 6-7 Hours Per Day | 1 x Full Showreel | 4-6 Minute Highlight Video | 1 x Exclusive Pendrive 16 GB & Case', 1200.00, 'Basic wedding videography package with essential coverage', 1),
(@pkg1, 'Advance', '1 Event 1 Video | Akad Nikah / Sanding (1 Day 1 Event) | 6-7 Hours Per Day | 1 x Full Showreel | 4-6 Minute Highlight Video | 1 x Exclusive Pendrive 16 GB & Case | Drone 1/Per Day RM500', 1800.00, 'Advanced wedding videography with drone coverage', 1),
(@pkg1, 'Premium', '1 Event 1 Video | Akad Nikah / Sanding (1 Day 1 Event) | 6-7 Hours Per Day | 1 x Full Showreel | 4-6 Minute Highlight Video | 1 x Exclusive Pendrive 16 GB & Case | Drone 1/Per Day RM500 | +1 Videographer Per Day RM500', 2500.00, 'Premium wedding videography with full coverage and multiple videographers', 1),

-- Wedding Videography - Half Day
(@pkg2, 'Regular', '1 Event 1 Video | Akad Nikah / Sanding (Half Day) | 4-5 Hours Per Day | 1 x Full Showreel | 4-6 Minute Highlight Video | 1 x Exclusive Pendrive 16 GB & Case', 800.00, 'Basic half-day wedding videography', 1),
(@pkg2, 'Advance', '1 Event 1 Video | Akad Nikah / Sanding (Half Day) | 4-5 Hours Per Day | 1 x Full Showreel | 4-6 Minute Highlight Video | 1 x Exclusive Pendrive 16 GB & Case | Drone 1/Per Day RM500', 1200.00, 'Advanced half-day wedding videography with drone', 1),
(@pkg2, 'Premium', '1 Event 1 Video | Akad Nikah / Sanding (Half Day) | 4-5 Hours Per Day | 1 x Full Showreel | 4-6 Minute Highlight Video | 1 x Exclusive Pendrive 16 GB & Case | Drone 1/Per Day RM500 | +1 Videographer Per Day RM500', 1600.00, 'Premium half-day wedding videography with full features', 1),

-- Corporate Event Videography
(@pkg3, 'Regular', 'Corporate Event Documentation | 6-8 Hours | Professional Setup | 1 x Full Event Video | Highlight Reel | Raw Footage', 1000.00, 'Basic corporate event videography', 1),
(@pkg3, 'Advance', 'Corporate Event Documentation | 6-8 Hours | Professional Setup | 1 x Full Event Video | Highlight Reel | Raw Footage | Drone Coverage', 1500.00, 'Advanced corporate event videography with drone', 1),
(@pkg3, 'Premium', 'Corporate Event Documentation | 6-8 Hours | Professional Setup | 1 x Full Event Video | Highlight Reel | Raw Footage | Drone Coverage | Multiple Camera Angles', 2000.00, 'Premium corporate event videography with multiple cameras', 1),

-- Birthday Party Videography
(@pkg4, 'Regular', 'Birthday Party Coverage | 4-6 Hours | Fun & Energetic Style | 1 x Party Video | Highlight Reel | Raw Footage', 400.00, 'Basic birthday party videography', 1),
(@pkg4, 'Advance', 'Birthday Party Coverage | 4-6 Hours | Fun & Energetic Style | 1 x Party Video | Highlight Reel | Raw Footage | Drone Shots', 600.00, 'Advanced birthday party videography with drone', 1),
(@pkg4, 'Premium', 'Birthday Party Coverage | 4-6 Hours | Fun & Energetic Style | 1 x Party Video | Highlight Reel | Raw Footage | Drone Shots | Multiple Cameras', 800.00, 'Premium birthday party videography with multiple cameras', 1),

-- Music Video Production
(@pkg5, 'Regular', 'Music Video Production | 6-10 Hours | Creative Direction | 1 x Music Video | Behind The Scenes | Raw Footage', 1500.00, 'Basic music video production', 1),
(@pkg5, 'Advance', 'Music Video Production | 6-10 Hours | Creative Direction | 1 x Music Video | Behind The Scenes | Raw Footage | Drone Shots', 2500.00, 'Advanced music video production with drone', 1),
(@pkg5, 'Premium', 'Music Video Production | 6-10 Hours | Creative Direction | 1 x Music Video | Behind The Scenes | Raw Footage | Drone Shots | Multiple Locations', 4000.00, 'Premium music video production with multiple locations', 1);
PRINT 'Videography sub-packages inserted';
GO

-- =====================================================
-- 6. VERIFY DATA
-- =====================================================
-- Check table structure
SELECT 
    COLUMN_NAME,
    DATA_TYPE,
    IS_NULLABLE,
    COLUMN_DEFAULT
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_NAME = 'videography_sub_packages'
ORDER BY ORDINAL_POSITION;
GO

-- Check data counts
SELECT 'USERS' as Table_Name, COUNT(*) as Count FROM users
UNION ALL
SELECT 'PACKAGES' as Table_Name, COUNT(*) as Count FROM packages
UNION ALL
SELECT 'VIDEOGRAPHY_SUB_PACKAGES' as Table_Name, COUNT(*) as Count FROM videography_sub_packages;
GO

-- Show sample data
SELECT 
    vsp.id,
    p.package_name,
    vsp.package_class,
    vsp.price,
    vsp.details,
    vsp.description,
    vsp.is_active
FROM videography_sub_packages vsp
JOIN packages p ON vsp.package_id = p.id
WHERE p.category = 'Videography'
ORDER BY vsp.package_id, vsp.package_class;
GO

PRINT '=====================================================';
PRINT 'VIDEOGRAPHY SUB-PACKAGE DATABASE SETUP COMPLETED!';
PRINT '=====================================================';
PRINT 'Users: ' + CAST((SELECT COUNT(*) FROM users) AS NVARCHAR(10));
PRINT 'Videography Packages: ' + CAST((SELECT COUNT(*) FROM packages WHERE category = 'Videography') AS NVARCHAR(10));
PRINT 'Videography Sub-Packages: ' + CAST((SELECT COUNT(*) FROM videography_sub_packages) AS NVARCHAR(10));
PRINT '=====================================================';
PRINT 'Database is ready for Android app!';
PRINT '=====================================================';
GO
