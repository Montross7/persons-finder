#!/bin/bash

# Seed script to populate database with 1 million persons
# This bypasses the Gemini API and uses mock bios for speed

set -e

DB_NAME="persons_finder"
DB_USER="stuartburmeister"
RECORDS=1000000

echo "🌱 Seeding database with $RECORDS persons..."
echo "⚠️  This will take 2-5 minutes depending on your machine"

# Array of sample data for variety
NAMES=("Alex" "Jordan" "Taylor" "Morgan" "Casey" "Jamie" "Riley" "Avery" "Quinn" "Skyler")
JOB_TITLES=("Software Engineer" "Data Scientist" "Product Manager" "Designer" "DevOps Engineer" "Marketing Manager" "Teacher" "Nurse" "Chef" "Architect")
HOBBIES=('["reading", "gaming"]' '["hiking", "photography"]' '["cooking", "music"]' '["sports", "travel"]' '["art", "yoga"]')
BIOS=("A creative professional who loves solving problems." "An adventurous spirit with a passion for learning." "A detail-oriented individual who enjoys collaboration." "A tech enthusiast always exploring new ideas." "A people person who thrives in dynamic environments.")

# New Zealand latitude/longitude ranges
# Latitude: -47 (south) to -34 (north)
# Longitude: 166 (west) to 179 (east)

psql -d "$DB_NAME" -U "$DB_USER" <<SQL
-- Use a temporary staging approach for faster inserts
CREATE TEMP TABLE temp_persons AS SELECT * FROM person WHERE false;

-- Generate 1 million records using generate_series
INSERT INTO temp_persons (name, bio, job_title, hobbies, latitude, longitude)
SELECT
    (ARRAY['Alex', 'Jordan', 'Taylor', 'Morgan', 'Casey', 'Jamie', 'Riley', 'Avery', 'Quinn', 'Skyler'])[1 + mod(i, 10)] || '_' || i AS name,
    (ARRAY[
        'A creative professional who loves solving problems.',
        'An adventurous spirit with a passion for learning.',
        'A detail-oriented individual who enjoys collaboration.',
        'A tech enthusiast always exploring new ideas.',
        'A people person who thrives in dynamic environments.'
    ])[1 + mod(i, 5)] AS bio,
    (ARRAY['Software Engineer', 'Data Scientist', 'Product Manager', 'Designer', 'DevOps Engineer', 'Marketing Manager', 'Teacher', 'Nurse', 'Chef', 'Architect'])[1 + mod(i, 10)] AS job_title,
    (ARRAY['[reading, gaming]', '[hiking, photography]', '[cooking, music]', '[sports, travel]', '[art, yoga]'])[1 + mod(i, 5)] AS hobbies,
    -47.0 + (random() * 13.0) AS latitude,  -- Range: -47 to -34 (NZ)
    166.0 + (random() * 13.0) AS longitude  -- Range: 166 to 179 (NZ)
FROM generate_series(1, $RECORDS) AS i;

-- Copy from temp table to actual table (much faster than direct insert)
INSERT INTO person (name, bio, job_title, hobbies, latitude, longitude)
SELECT name, bio, job_title, hobbies, latitude, longitude FROM temp_persons;

-- Create index for faster nearby queries
CREATE INDEX IF NOT EXISTS idx_person_location ON person (latitude, longitude);

-- Show stats
SELECT COUNT(*) AS total_persons FROM person;
SQL

echo "✅ Database seeded with $RECORDS persons!"
echo "📊 Testing query performance..."

# Test query performance
psql -d "$DB_NAME" -U "$DB_USER" <<SQL
\timing on
-- Test nearby query for person ID 1 within 50km
SELECT COUNT(*) FROM person
WHERE 
    id != 1 
    AND (
        6371 * acos(
            cos(radians(-41.2865)) * cos(radians(latitude)) * 
            cos(radians(longitude) - radians(174.7762)) + 
            sin(radians(-41.2865)) * sin(radians(latitude))
        )
    ) <= 50
LIMIT 10;
SQL

echo ""
echo "🎯 Seed complete! Your database now has 1M+ records for performance testing."
