#!/bin/bash
set -e
echo "Building and testing ShareIt project..."
mvn clean package
echo "Build completed successfully!"
