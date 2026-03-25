#!/bin/bash

# Load environment variables from .env file (optional now)
if [ -f .env ]; then
    export $(cat .env | grep -v '^#' | xargs)
fi

echo "🚀 Starting Persons Finder API..."
echo "📍 Server will be available at http://localhost:8080"
echo ""

# Check if Ollama is running
echo "Checking Ollama service..."
if ! curl -s http://localhost:11434/api/tags > /dev/null 2>&1; then
    echo "⚠️  Ollama not running. Starting it now..."
    brew services start ollama
    sleep 3
fi

echo "✅ Ollama ready"
echo ""

./gradlew bootRun
