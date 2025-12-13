-- ============================================================================
-- Quick script: Jane Smith applies to RA job #5
-- ============================================================================
-- Assumes Jane already exists (created via insert_new_student_ra6.sql)
-- Email: jane.smith.2025.ra6@university.edu
-- ============================================================================

-- Insert RA job application for Jane to job #5
INSERT INTO rajob_application (
    rajob_id,
    applicant_id,
    apply_headline,
    apply_cover_letter,
    rating,
    rating_count,
    recommend_rating,
    recommend_rating_count,
    status
)
SELECT 
    5,  -- RA job ID = 5
    id AS applicant_id,
    'Application for RA Position (Job #5)',
    'Hello Professor, I am very interested in this RA position and would love to contribute to your research project.',
    0, 0, 0, 0,  -- rating fields
    'pending'    -- application status
FROM user
WHERE email = 'jane.smith.2025.ra6@university.edu'
LIMIT 1;

-- Verify the application was created
SELECT 
    id,
    rajob_id,
    applicant_id,
    apply_headline,
    status
FROM rajob_application 
WHERE applicant_id = (
    SELECT id FROM user WHERE email = 'jane.smith.2025.ra6@university.edu'
)
AND rajob_id = 5;

