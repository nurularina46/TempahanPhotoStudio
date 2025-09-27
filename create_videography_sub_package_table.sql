-- Create videography_sub_packages table for TempahanPhotoStudio Database
-- Run this script in SQL Server Management Studio

USE TempahanPhotoStudio;
GO

-- Create videography_sub_packages table if it doesn't exist
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'videography_sub_packages')
BEGIN
    CREATE TABLE videography_sub_packages (
        id INT IDENTITY(1,1) PRIMARY KEY,
        package_id INT NOT NULL,
        package_class NVARCHAR(50) NOT NULL,
        price DECIMAL(10,2) NOT NULL,
        details NVARCHAR(500) NOT NULL,
        created_at DATETIME DEFAULT GETDATE(),
        updated_at DATETIME DEFAULT GETDATE()
    );
    
    PRINT 'videography_sub_packages table created successfully!';
END
ELSE
BEGIN
    PRINT 'videography_sub_packages table already exists.';
END
GO

-- Add foreign key constraint if packages table exists
IF EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'packages')
BEGIN
    -- Check if foreign key constraint already exists
    IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.REFERENTIAL_CONSTRAINTS 
                   WHERE CONSTRAINT_NAME = 'FK_videography_sub_packages_package_id')
    BEGIN
        ALTER TABLE videography_sub_packages
        ADD CONSTRAINT FK_videography_sub_packages_package_id
        FOREIGN KEY (package_id) REFERENCES packages(id);
        
        PRINT 'Foreign key constraint added successfully!';
    END
    ELSE
    BEGIN
        PRINT 'Foreign key constraint already exists.';
    END
END
ELSE
BEGIN
    PRINT 'packages table does not exist. Foreign key constraint not added.';
END
GO

-- Insert sample data for testing
INSERT INTO videography_sub_packages (package_id, package_class, price, details) 
VALUES 
(1, 'Regular', 500.00, 'Basic videography package with 1 videographer'),
(1, 'Advance', 800.00, 'Advanced videography package with 2 videographers'),
(1, 'Premium', 1200.00, 'Premium videography package with drone and 2 videographers'),
(2, 'Regular', 300.00, 'Basic event videography'),
(2, 'Advance', 500.00, 'Advanced event videography with highlights'),
(2, 'Premium', 800.00, 'Premium event videography with drone shots');
GO

-- Verify the table structure
SELECT 
    COLUMN_NAME,
    DATA_TYPE,
    IS_NULLABLE,
    COLUMN_DEFAULT
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_NAME = 'videography_sub_packages'
ORDER BY ORDINAL_POSITION;
GO

-- Show sample data
SELECT 
    id,
    package_id,
    package_class,
    price,
    details,
    created_at
FROM videography_sub_packages
ORDER BY package_id, package_class;
GO

PRINT 'videography_sub_packages table setup completed successfully!';
PRINT 'Total records inserted: ' + CAST((SELECT COUNT(*) FROM videography_sub_packages) AS NVARCHAR(10));
GO
