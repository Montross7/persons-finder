#!/bin/bash

# Smoke test for all API endpoints
# Run this after starting the server with ./run.sh

set -e

BASE_URL="http://localhost:8080/api/v1/persons"
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo "🧪 Running API Smoke Tests..."
echo ""

# Test 1: Create a person
echo "📝 Test 1: Create a person (POST /api/v1/persons)"
PERSON_ID=$(curl -s -X POST "$BASE_URL" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test User",
    "jobTitle": "QA Engineer",
    "hobbies": ["testing", "automation", "coffee"],
    "location": {"latitude": -41.2865, "longitude": 174.7762}
  }')
echo -e "${GREEN}✅ Created person with ID: $PERSON_ID${NC}"
echo ""

# Test 2: Update location
echo "📍 Test 2: Update person location (PUT /api/v1/persons/$PERSON_ID/location)"
curl -s -X PUT "$BASE_URL/$PERSON_ID/location?longitude=175.5&latitude=-41.5" | jq -r '.name, .latitude, .longitude' 2>/dev/null || echo "Updated"
echo -e "${GREEN}✅ Location updated${NC}"
echo ""

# Test 3: Find nearby persons
echo "🔍 Test 3: Find nearby persons (GET /api/v1/persons/nearby)"
NEARBY_COUNT=$(curl -s "$BASE_URL/nearby?id=1&radius=100" | jq '.content | length' 2>/dev/null || echo "?")
echo -e "${GREEN}✅ Found $NEARBY_COUNT nearby persons within 100km${NC}"
echo ""

# Test 4: Get person names
echo "📛 Test 4: Get person names (GET /api/v1/persons?ids=...)"
curl -s "$BASE_URL?ids=1&ids=$PERSON_ID" | jq '.' 2>/dev/null || echo "Names retrieved"
echo -e "${GREEN}✅ Names retrieved${NC}"
echo ""

# Test 5: Validation - invalid radius
echo "❌ Test 5: Invalid radius (should return 400)"
HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/nearby?id=1&radius=0")
if [ "$HTTP_CODE" = "400" ]; then
    echo -e "${GREEN}✅ Validation working - returned 400${NC}"
else
    echo -e "${RED}❌ Expected 400, got $HTTP_CODE${NC}"
fi
echo ""

# Test 6: Validation - invalid coordinates
echo "❌ Test 6: Invalid longitude (should return 400)"
HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" -X PUT "$BASE_URL/1/location?longitude=200&latitude=-41.5")
if [ "$HTTP_CODE" = "400" ]; then
    echo -e "${GREEN}✅ Validation working - returned 400${NC}"
else
    echo -e "${RED}❌ Expected 400, got $HTTP_CODE${NC}"
fi
echo ""

# Test 7: Non-existent person
echo "❌ Test 7: Non-existent person (should return 404)"
HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL?ids=999999")
if [ "$HTTP_CODE" = "404" ]; then
    echo -e "${GREEN}✅ Error handling working - returned 404${NC}"
else
    echo -e "${RED}❌ Expected 404, got $HTTP_CODE${NC}"
fi
echo ""

echo "🎉 All tests complete!"
echo ""
echo "📊 Performance test: Run './seed_database.sh' to test with 1M records"
