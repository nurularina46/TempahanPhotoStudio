-- Add Videography Packages to TempahanPhotoStudio Database
-- Run this script in your SQL Server Management Studio

USE TempahanPhotoStudio;
GO

-- Insert Videography Packages
INSERT INTO packages (package_name, event, duration, category, description, image_url) 
VALUES 
-- Wedding Videography
('Wedding Videography - Full Day', 'Wedding', '8-10 hours', 'Videography', 'Complete wedding day videography coverage from preparation to reception', ''),
('Wedding Videography - Half Day', 'Wedding', '4-5 hours', 'Videography', 'Wedding ceremony and reception videography', ''),
('Wedding Videography - Ceremony Only', 'Wedding', '2-3 hours', 'Videography', 'Wedding ceremony videography only', ''),

-- Event Videography
('Corporate Event Videography', 'Corporate Event', '6-8 hours', 'Videography', 'Professional corporate event documentation', ''),
('Birthday Party Videography', 'Birthday Party', '4-6 hours', 'Videography', 'Birthday celebration videography', ''),
('Anniversary Videography', 'Anniversary', '3-4 hours', 'Videography', 'Anniversary celebration videography', ''),
('Graduation Videography', 'Graduation', '2-3 hours', 'Videography', 'Graduation ceremony videography', ''),

-- Commercial Videography
('Product Commercial Video', 'Commercial', '4-6 hours', 'Videography', 'Product advertisement video production', ''),
('Company Profile Video', 'Commercial', '6-8 hours', 'Videography', 'Corporate profile video production', ''),
('Social Media Content', 'Social Media', '2-4 hours', 'Videography', 'Social media video content creation', ''),

-- Specialized Videography
('Drone Videography', 'Aerial', '2-3 hours', 'Videography', 'Aerial videography using professional drone', ''),
('Documentary Videography', 'Documentary', '4-8 hours', 'Videography', 'Documentary style videography', ''),
('Music Video Production', 'Music Video', '6-10 hours', 'Videography', 'Professional music video production', ''),
('Live Streaming', 'Live Event', '2-6 hours', 'Videography', 'Live event streaming service', ''),
('Interview Videography', 'Interview', '1-2 hours', 'Videography', 'Professional interview recording', ''),
('Real Estate Videography', 'Real Estate', '2-4 hours', 'Videography', 'Property showcase videography', '');
GO

-- Get the package IDs for videography packages
DECLARE @wedding_full_id INT = (SELECT id FROM packages WHERE package_name = 'Wedding Videography - Full Day');
DECLARE @wedding_half_id INT = (SELECT id FROM packages WHERE package_name = 'Wedding Videography - Half Day');
DECLARE @wedding_ceremony_id INT = (SELECT id FROM packages WHERE package_name = 'Wedding Videography - Ceremony Only');
DECLARE @corporate_id INT = (SELECT id FROM packages WHERE package_name = 'Corporate Event Videography');
DECLARE @birthday_id INT = (SELECT id FROM packages WHERE package_name = 'Birthday Party Videography');
DECLARE @anniversary_id INT = (SELECT id FROM packages WHERE package_name = 'Anniversary Videography');
DECLARE @graduation_id INT = (SELECT id FROM packages WHERE package_name = 'Graduation Videography');
DECLARE @product_commercial_id INT = (SELECT id FROM packages WHERE package_name = 'Product Commercial Video');
DECLARE @company_profile_id INT = (SELECT id FROM packages WHERE package_name = 'Company Profile Video');
DECLARE @social_media_id INT = (SELECT id FROM packages WHERE package_name = 'Social Media Content');
DECLARE @drone_id INT = (SELECT id FROM packages WHERE package_name = 'Drone Videography');
DECLARE @documentary_id INT = (SELECT id FROM packages WHERE package_name = 'Documentary Videography');
DECLARE @music_video_id INT = (SELECT id FROM packages WHERE package_name = 'Music Video Production');
DECLARE @live_streaming_id INT = (SELECT id FROM packages WHERE package_name = 'Live Streaming');
DECLARE @interview_id INT = (SELECT id FROM packages WHERE package_name = 'Interview Videography');
DECLARE @real_estate_id INT = (SELECT id FROM packages WHERE package_name = 'Real Estate Videography');

-- Insert Sub-Packages for Videography
INSERT INTO sub_packages (package_id, package_class, details, price, description) 
VALUES 
-- Wedding Videography - Full Day
(@wedding_full_id, 'Regular', 'Basic wedding video package', 1200.00, 'Includes 30-minute edited video + raw footage'),
(@wedding_full_id, 'Advance', 'Advanced wedding video package', 1800.00, 'Includes 60-minute edited video + highlights + raw footage'),
(@wedding_full_id, 'Premium', 'Premium wedding video package', 2500.00, 'Includes 90-minute edited video + highlights + drone shots + raw footage'),

-- Wedding Videography - Half Day
(@wedding_half_id, 'Regular', 'Basic half-day wedding video', 800.00, 'Includes 20-minute edited video + raw footage'),
(@wedding_half_id, 'Advance', 'Advanced half-day wedding video', 1200.00, 'Includes 40-minute edited video + highlights + raw footage'),
(@wedding_half_id, 'Premium', 'Premium half-day wedding video', 1600.00, 'Includes 60-minute edited video + highlights + drone shots + raw footage'),

-- Wedding Videography - Ceremony Only
(@wedding_ceremony_id, 'Regular', 'Basic ceremony video', 500.00, 'Includes 15-minute edited video + raw footage'),
(@wedding_ceremony_id, 'Advance', 'Advanced ceremony video', 750.00, 'Includes 30-minute edited video + highlights + raw footage'),
(@wedding_ceremony_id, 'Premium', 'Premium ceremony video', 1000.00, 'Includes 45-minute edited video + highlights + drone shots + raw footage'),

-- Corporate Event Videography
(@corporate_id, 'Regular', 'Basic corporate event video', 1000.00, 'Includes 20-minute edited video + raw footage'),
(@corporate_id, 'Advance', 'Advanced corporate event video', 1500.00, 'Includes 40-minute edited video + highlights + raw footage'),
(@corporate_id, 'Premium', 'Premium corporate event video', 2000.00, 'Includes 60-minute edited video + highlights + drone shots + raw footage'),

-- Birthday Party Videography
(@birthday_id, 'Regular', 'Basic birthday video', 400.00, 'Includes 10-minute edited video + raw footage'),
(@birthday_id, 'Advance', 'Advanced birthday video', 600.00, 'Includes 20-minute edited video + highlights + raw footage'),
(@birthday_id, 'Premium', 'Premium birthday video', 800.00, 'Includes 30-minute edited video + highlights + drone shots + raw footage'),

-- Anniversary Videography
(@anniversary_id, 'Regular', 'Basic anniversary video', 350.00, 'Includes 10-minute edited video + raw footage'),
(@anniversary_id, 'Advance', 'Advanced anniversary video', 500.00, 'Includes 20-minute edited video + highlights + raw footage'),
(@anniversary_id, 'Premium', 'Premium anniversary video', 700.00, 'Includes 30-minute edited video + highlights + drone shots + raw footage'),

-- Graduation Videography
(@graduation_id, 'Regular', 'Basic graduation video', 300.00, 'Includes 10-minute edited video + raw footage'),
(@graduation_id, 'Advance', 'Advanced graduation video', 450.00, 'Includes 20-minute edited video + highlights + raw footage'),
(@graduation_id, 'Premium', 'Premium graduation video', 600.00, 'Includes 30-minute edited video + highlights + drone shots + raw footage'),

-- Product Commercial Video
(@product_commercial_id, 'Regular', 'Basic product commercial', 800.00, 'Includes 30-second commercial + raw footage'),
(@product_commercial_id, 'Advance', 'Advanced product commercial', 1200.00, 'Includes 60-second commercial + multiple angles + raw footage'),
(@product_commercial_id, 'Premium', 'Premium product commercial', 1800.00, 'Includes 90-second commercial + drone shots + professional editing + raw footage'),

-- Company Profile Video
(@company_profile_id, 'Regular', 'Basic company profile', 1500.00, 'Includes 3-minute company video + raw footage'),
(@company_profile_id, 'Advance', 'Advanced company profile', 2200.00, 'Includes 5-minute company video + interviews + raw footage'),
(@company_profile_id, 'Premium', 'Premium company profile', 3000.00, 'Includes 8-minute company video + interviews + drone shots + raw footage'),

-- Social Media Content
(@social_media_id, 'Regular', 'Basic social media content', 200.00, 'Includes 5 short videos for social media'),
(@social_media_id, 'Advance', 'Advanced social media content', 350.00, 'Includes 10 short videos + basic editing'),
(@social_media_id, 'Premium', 'Premium social media content', 500.00, 'Includes 15 short videos + professional editing + graphics'),

-- Drone Videography
(@drone_id, 'Regular', 'Basic drone video', 600.00, 'Includes 5-minute drone video + raw footage'),
(@drone_id, 'Advance', 'Advanced drone video', 900.00, 'Includes 10-minute drone video + multiple locations + raw footage'),
(@drone_id, 'Premium', 'Premium drone video', 1300.00, 'Includes 15-minute drone video + multiple locations + professional editing + raw footage'),

-- Documentary Videography
(@documentary_id, 'Regular', 'Basic documentary', 2000.00, 'Includes 20-minute documentary + raw footage'),
(@documentary_id, 'Advance', 'Advanced documentary', 3000.00, 'Includes 40-minute documentary + interviews + raw footage'),
(@documentary_id, 'Premium', 'Premium documentary', 4500.00, 'Includes 60-minute documentary + interviews + drone shots + raw footage'),

-- Music Video Production
(@music_video_id, 'Regular', 'Basic music video', 1500.00, 'Includes 3-4 minute music video + raw footage'),
(@music_video_id, 'Advance', 'Advanced music video', 2500.00, 'Includes 4-5 minute music video + multiple locations + raw footage'),
(@music_video_id, 'Premium', 'Premium music video', 4000.00, 'Includes 5-6 minute music video + multiple locations + drone shots + professional editing + raw footage'),

-- Live Streaming
(@live_streaming_id, 'Regular', 'Basic live streaming', 300.00, '2-hour live streaming service'),
(@live_streaming_id, 'Advance', 'Advanced live streaming', 500.00, '4-hour live streaming service with professional setup'),
(@live_streaming_id, 'Premium', 'Premium live streaming', 800.00, '6-hour live streaming service with professional setup + recording'),

-- Interview Videography
(@interview_id, 'Regular', 'Basic interview video', 250.00, '1-hour interview recording + basic editing'),
(@interview_id, 'Advance', 'Advanced interview video', 400.00, '2-hour interview recording + professional editing'),
(@interview_id, 'Premium', 'Premium interview video', 600.00, '3-hour interview recording + professional editing + graphics'),

-- Real Estate Videography
(@real_estate_id, 'Regular', 'Basic property video', 400.00, '3-minute property showcase video + raw footage'),
(@real_estate_id, 'Advance', 'Advanced property video', 600.00, '5-minute property showcase video + drone shots + raw footage'),
(@real_estate_id, 'Premium', 'Premium property video', 900.00, '8-minute property showcase video + drone shots + professional editing + raw footage');
GO

-- Verify the data
SELECT 
    p.package_name,
    p.event,
    p.duration,
    COUNT(sp.id) as sub_package_count,
    MIN(sp.price) as min_price,
    MAX(sp.price) as max_price
FROM packages p
LEFT JOIN sub_packages sp ON p.id = sp.package_id
WHERE p.category = 'Videography'
GROUP BY p.id, p.package_name, p.event, p.duration
ORDER BY p.package_name;
GO

PRINT 'Videography packages added successfully!';
PRINT 'Total videography packages: ' + CAST((SELECT COUNT(*) FROM packages WHERE category = 'Videography') AS NVARCHAR(10));
PRINT 'Total videography sub-packages: ' + CAST((SELECT COUNT(*) FROM sub_packages sp JOIN packages p ON sp.package_id = p.id WHERE p.category = 'Videography') AS NVARCHAR(10));
GO
