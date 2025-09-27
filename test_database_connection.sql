-- =====================================================
-- TEST DATABASE CONNECTION AND DATA
-- =====================================================
-- Jalankan script ini untuk menguji database

USE TempahanPhotoStudio;
GO

-- =====================================================
-- 1. CHECK ALL TABLES EXIST
-- =====================================================
SELECT 
    TABLE_NAME,
    CASE 
        WHEN TABLE_NAME IN ('users', 'packages', 'videography_sub_packages', 'bookings') 
        THEN 'EXISTS' 
        ELSE 'MISSING' 
    END as Status
FROM INFORMATION_SCHEMA.TABLES 
WHERE TABLE_NAME IN ('users', 'packages', 'videography_sub_packages', 'bookings')
ORDER BY TABLE_NAME;
GO

-- =====================================================
-- 2. CHECK FOREIGN KEY CONSTRAINTS
-- =====================================================
SELECT 
    tc.CONSTRAINT_NAME,
    tc.TABLE_NAME,
    kcu.COLUMN_NAME,
    ccu.TABLE_NAME AS FOREIGN_TABLE_NAME,
    ccu.COLUMN_NAME AS FOREIGN_COLUMN_NAME
FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS AS tc
JOIN INFORMATION_SCHEMA.KEY_COLUMN_USAGE AS kcu
    ON tc.CONSTRAINT_NAME = kcu.CONSTRAINT_NAME
JOIN INFORMATION_SCHEMA.CONSTRAINT_COLUMN_USAGE AS ccu
    ON ccu.CONSTRAINT_NAME = tc.CONSTRAINT_NAME
WHERE tc.CONSTRAINT_TYPE = 'FOREIGN KEY'
    AND tc.TABLE_NAME = 'videography_sub_packages';
GO

-- =====================================================
-- 3. TEST INSERT (WITHOUT COMMITTING)
-- =====================================================
BEGIN TRANSACTION;

-- Test insert into videography_sub_packages
DECLARE @test_package_id INT = (SELECT TOP 1 id FROM packages WHERE category = 'Videography');

IF @test_package_id IS NOT NULL
BEGIN
    INSERT INTO videography_sub_packages (package_id, package_class, price, details) 
    VALUES (@test_package_id, 'Test', 100.00, 'Test sub-package for validation');
    
    PRINT 'SUCCESS: Test insert completed successfully!';
    PRINT 'Test Package ID: ' + CAST(@test_package_id AS NVARCHAR(10));
    
    -- Delete test record
    DELETE FROM videography_sub_packages WHERE package_class = 'Test';
    PRINT 'Test record deleted successfully!';
END
ELSE
BEGIN
    PRINT 'ERROR: No videography packages found!';
END

-- Rollback transaction (don't commit test data)
ROLLBACK TRANSACTION;
GO

-- =====================================================
-- 4. SHOW SAMPLE DATA
-- =====================================================
-- Show videography packages
SELECT 
    p.id as Package_ID,
    p.package_name,
    p.event,
    p.duration,
    COUNT(vsp.id) as Sub_Package_Count
FROM packages p
LEFT JOIN videography_sub_packages vsp ON p.id = vsp.package_id
WHERE p.category = 'Videography'
GROUP BY p.id, p.package_name, p.event, p.duration
ORDER BY p.id;
GO

-- Show videography sub-packages
SELECT 
    vsp.id as Sub_Package_ID,
    p.package_name,
    vsp.package_class,
    vsp.price,
    LEFT(vsp.details, 50) + '...' as Details_Preview
FROM videography_sub_packages vsp
JOIN packages p ON vsp.package_id = p.id
WHERE p.category = 'Videography'
ORDER BY vsp.package_id, vsp.package_class;
GO

-- =====================================================
-- 5. FINAL STATUS
-- =====================================================
PRINT '=====================================================';
PRINT 'DATABASE TEST COMPLETED!';
PRINT '=====================================================';
PRINT 'Videography Packages: ' + CAST((SELECT COUNT(*) FROM packages WHERE category = 'Videography') AS NVARCHAR(10));
PRINT 'Videography Sub-Packages: ' + CAST((SELECT COUNT(*) FROM videography_sub_packages) AS NVARCHAR(10));
PRINT 'Users: ' + CAST((SELECT COUNT(*) FROM users) AS NVARCHAR(10));
PRINT '=====================================================';
PRINT 'Database is ready for Android app!';
PRINT '=====================================================';
GO
