-- ============================================================================
-- SQL statement to insert an interview into the database
-- ============================================================================
-- Position: "Test RA Job"
-- Job ID: 2
-- Applicant: John Doe, email: john.doe@university.edu
-- Faculty: benking, User ID = 1, email: kingb@smu.edu
-- Schedule: 2025-12-15 14:15:58 (within the specified range 2025-12-12 to 2026-12-12)
-- ============================================================================

-- STEP 1: Find the applicant ID for John Doe
-- (Note: If email is 'john.doe@universiry.edu' with typo, change it below)
SELECT id, first_name, last_name, email 
FROM user 
WHERE email = 'john.doe@university.edu' 
   OR email = 'john.doe@universiry.edu';  -- Including typo variant

-- STEP 2: Find the job application ID
-- Replace <applicant_id> with the ID from Step 1
SELECT id, rajob_id, applicant_id, status
FROM rajob_application 
WHERE applicant_id = <applicant_id>  -- Replace with actual applicant ID
  AND rajob_id = 2;

-- STEP 3: Verify the RA Job exists
SELECT id, title, rajob_publisher_id
FROM rajob
WHERE id = 2;

-- STEP 4: Insert the interview (using subqueries to find IDs automatically)
INSERT INTO ra_interview (
    rajob_application_id,
    faculty_id,
    applicant_id,
    rajob_id,
    interview_date,
    interview_time,
    status,
    created_time,
    updated_time
)
SELECT 
    raja.id AS rajob_application_id,
    1 AS faculty_id,  -- benking (User ID = 1)
    applicant.id AS applicant_id,
    2 AS rajob_id,  -- "Test RA Job"
    '2025-12-15 14:15:58' AS interview_date,
    '2025-12-15 14:15:58' AS interview_time,
    'pending' AS status,
    NOW() AS created_time,
    NOW() AS updated_time
FROM 
    user AS applicant
    INNER JOIN rajob_application AS raja 
        ON raja.applicant_id = applicant.id 
        AND raja.rajob_id = 2
WHERE 
    (applicant.email = 'john.doe@university.edu' OR applicant.email = 'john.doe@universiry.edu')
    AND applicant.first_name = 'John'
    AND applicant.last_name = 'Doe'
LIMIT 1;

-- ============================================================================
-- ALTERNATIVE: Direct INSERT if you already know the IDs
-- ============================================================================
-- Use this if you've already run the queries above and know the IDs
-- Replace <rajob_application_id> and <applicant_id> with actual values
/*
INSERT INTO ra_interview (
    rajob_application_id,
    faculty_id,
    applicant_id,
    rajob_id,
    interview_date,
    interview_time,
    status,
    created_time,
    updated_time
)
VALUES (
    <rajob_application_id>,  -- Replace with actual application ID
    1,                       -- benking (faculty_id)
    <applicant_id>,          -- Replace with John Doe's user ID
    2,                       -- "Test RA Job" (rajob_id)
    '2025-12-15 14:15:58',   -- interview_date
    '2025-12-15 14:15:58',   -- interview_time
    'pending',               -- status
    NOW(),                   -- created_time
    NOW()                    -- updated_time
);
*/

-- ============================================================================
-- HELPER QUERIES: If applicant or application doesn't exist
-- ============================================================================

-- If John Doe doesn't exist, create the user first:
/*
INSERT INTO user (
    user_name, 
    first_name, 
    last_name, 
    email, 
    rating, 
    rating_count, 
    recommend_rating, 
    recommend_rating_count, 
    service_provider, 
    service_user, 
    service_execution_counts
)
VALUES (
    'johndoe', 
    'John', 
    'Doe', 
    'john.doe@university.edu', 
    0, 0, 0, 0, 0, 0, 0
);
*/

-- If the application doesn't exist, create it first:
/*
INSERT INTO rajob_application (
    rajob_id, 
    applicant_id, 
    rating, 
    rating_count, 
    recommend_rating, 
    recommend_rating_count, 
    status
)
SELECT 
    2, 
    id, 
    0, 0, 0, 0, 
    'pending' 
FROM user 
WHERE email = 'john.doe@university.edu' 
   OR email = 'john.doe@universiry.edu';
*/

