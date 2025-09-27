-- Add videography packages first before adding sub-packages
-- Run this script in SQL Server Management Studio

USE TempahanPhotoStudio;
GO

-- Check if packages table exists
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'packages')
BEGIN
    CREATE TABLE packages (
        id INT IDENTITY(1,1) PRIMARY KEY,
        package_name NVARCHAR(100) NOT NULL,
        event NVARCHAR(100) NOT NULL,
        duration NVARCHAR(50) NOT NULL,
        category NVARCHAR(50) NOT NULL,
        description NVARCHAR(500),
        image_url NVARCHAR(200)
    );
    PRINT 'packages table created successfully!';
END
ELSE
BEGIN
    PRINT 'packages table already exists.';
END
GO

-- Insert sample videography packages
INSERT INTO packages (package_name, event, duration, category, description, image_url) 
VALUES 
('Wedding Videography - Full Day', 'Wedding', '8-10 hours', 'Videography', 'Complete wedding day videography coverage from preparation to reception', ''),
('Wedding Videography - Half Day', 'Wedding', '4-5 hours', 'Videography', 'Wedding ceremony and reception videography', ''),
('Corporate Event Videography', 'Corporate Event', '6-8 hours', 'Videography', 'Professional corporate event documentation', ''),
('Birthday Party Videography', 'Birthday Party', '4-6 hours', 'Videography', 'Birthday celebration videography', ''),
('Music Video Production', 'Music Video', '6-10 hours', 'Videography', 'Professional music video production', '');
GO

-- Verify packages were inserted
SELECT 
    id,
    package_name,
    event,
    duration,
    category
FROM packages 
WHERE category = 'Videography'
ORDER BY id;
GO

PRINT 'Videography packages added successfully!';
PRINT 'Total videography packages: ' + CAST((SELECT COUNT(*) FROM packages WHERE category = 'Videography') AS NVARCHAR(10));
GO
