#!/bin/bash

# Configuration
BASE_URL="http://localhost:8085/api/users"
EMAIL="testuser_$(date +%s)@example.com"
PASSWORD="password123"
FIRST_NAME="Test"
LAST_NAME="User"

echo "=== Testing Authentication ==="
echo "Target URL: $BASE_URL"
echo "Test Email: $EMAIL"

# 1. Register
echo ""
echo "--- 1. Registering new user ---"
curl -s -X POST "$BASE_URL/auth/register" \
     -H "Content-Type: application/json" \
     -d "{
           \"email\": \"$EMAIL\",
           \"password\": \"$PASSWORD\",
           \"firstName\": \"$FIRST_NAME\",
           \"lastName\": \"$LAST_NAME\",
           \"roles\": [\"AGRICULTEUR\"]
         }" | python3 -m json.tool

# 2. Login
echo ""
echo "--- 2. Logging in ---"
curl -s -X POST "$BASE_URL/auth/login" \
     -H "Content-Type: application/json" \
     -d "{
           \"email\": \"$EMAIL\",
           \"password\": \"$PASSWORD\"
         }" | python3 -m json.tool

echo ""
echo "=== Test Complete ==="
