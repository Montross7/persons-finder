#!/bin/bash
# Run this script to start the server with environment variables

# Load environment variables from .env file
if [ -f .env ]; then
    export $(cat .env | grep -v '^#' | xargs)
    echo "✓ Loaded environment variables from .env"
else
    echo "⚠ Warning: .env file not found. Copy .env.example to .env and add your API key."
    exit 1
fi

# Check if GEMINI_API_KEY is set
if [ -z "$GEMINI_API_KEY" ]; then
    echo "❌ Error: GEMINI_API_KEY is not set in .env file"
    exit 1
fi

echo "✓ GEMINI_API_KEY is configured"
echo "Starting Spring Boot application..."
./gradlew bootRun
