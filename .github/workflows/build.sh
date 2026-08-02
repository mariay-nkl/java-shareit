
#!/bin/bash
set -e
echo "Building ShareIt project..."
mvn clean package -DskipTests
echo "Build completed successfully!"
