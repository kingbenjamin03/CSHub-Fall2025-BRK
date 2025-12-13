-- ============================================================================
-- SQL statement to create a new student and apply to RA job #6
-- ============================================================================
-- Creates: Jane Smith (new student user)
-- Applies to: RA job ID = 6
-- ============================================================================

-- STEP 1: Create a brand-new STUDENT user (user_type = 4 means STUDENT)
INSERT INTO user (
    user_name,
    first_name,
    last_name,
    email,
    user_type,
    rating,
    rating_count,
    recommend_rating,
    recommend_rating_count,
    service_provider,
    service_user,
    service_execution_counts
)
VALUES (
    'jane_smith_2025_ra6',
    'Jane',
    'Smith',
    'jane.smith.2025.ra6@university.edu',
    4,  -- STUDENT user type
    0, 0, 0, 0,  -- rating fields
    0, 0, 0      -- service fields
);

-- STEP 2: Get the newly created user ID
SET @new_student_id := LAST_INSERT_ID();

-- STEP 3: Add student profile information
INSERT INTO student_info (
    user_id,
    id_number,
    student_year,
    student_type,
    major,
    first_enroll_date
)
VALUES (
    @new_student_id,
    'SMU12345678',
    '2025',
    'MS',
    'Computer Science',
    '2025-08-20'
);

-- STEP 4: Create the RA job application to job #6
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
VALUES (
    6,  -- RA job ID = 6
    @new_student_id,
    'Application for RA Position (Job #6)',
    'Hello Professor, I am very interested in this RA position and would love to contribute to your research project.',
    0, 0, 0, 0,  -- rating fields
    'pending'    -- application status
);

-- ============================================================================
-- VERIFICATION QUERIES (optional - run these to verify the data was inserted)
-- ============================================================================

-- Verify the user was created
SELECT 
    id, 
    user_name, 
    first_name, 
    last_name, 
    email, 
    user_type
FROM user 
WHERE id = @new_student_id;

-- Verify the student_info was created
SELECT 
    user_id,
    id_number,
    student_year,
    student_type,
    major,
    first_enroll_date
FROM student_info 
WHERE user_id = @new_student_id;

-- Verify the RA job application was created
SELECT 
    id,
    rajob_id,
    applicant_id,
    apply_headline,
    status
FROM rajob_application 
WHERE applicant_id = @new_student_id 
  AND rajob_id = 6;

-- ============================================================================
-- ALTERNATIVE: If you prefer to use subqueries instead of variables
-- ============================================================================
/*
-- Create user and get ID in one step
INSERT INTO user (
    user_name, first_name, last_name, email, user_type,
    rating, rating_count, recommend_rating, recommend_rating_count,
    service_provider, service_user, service_execution_counts
)
VALUES (
    'jane_smith_2025_ra6', 'Jane', 'Smith', 'jane.smith.2025.ra6@university.edu', 4,
    0, 0, 0, 0, 0, 0, 0
);

-- Insert student_info using subquery
INSERT INTO student_info (user_id, id_number, student_year, student_type, major, first_enroll_date)
SELECT 
    id,
    'SMU12345678',
    '2025',
    'MS',
    'Computer Science',
    '2025-08-20'
FROM user
WHERE email = 'jane.smith.2025.ra6@university.edu'
LIMIT 1;

-- Insert rajob_application using subquery
INSERT INTO rajob_application (
    rajob_id, applicant_id, apply_headline, apply_cover_letter,
    rating, rating_count, recommend_rating, recommend_rating_count, status
)
SELECT 
    6,
    id,
    'Application for RA Position (Job #6)',
    'Hello Professor, I am very interested in this RA position and would love to contribute to your research project.',
    0, 0, 0, 0,
    'pending'
FROM user
WHERE email = 'jane.smith.2025.ra6@university.edu'
LIMIT 1;
*/

