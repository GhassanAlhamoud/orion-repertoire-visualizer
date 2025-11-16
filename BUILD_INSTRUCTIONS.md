# Build and Run Instructions

## Prerequisites

- **Java 11 or higher** (Java 17+ recommended)
- **Apache Maven 3.6+**
- **OrionDB Chess Engine** (built separately)

## Step-by-Step Build Process

### 1. Build OrionDB Chess Engine

First, you need to build the OrionDB chess engine dependency:

```bash
# Clone OrionDB
git clone https://github.com/GhassanAlhamoud/oriondb-chess-engine.git
cd oriondb-chess-engine

# Build and install to local Maven repository
mvn clean install -DskipTests

# This creates: target/oriondb-0.1.0-SNAPSHOT.jar
```

### 2. Build Orion Repertoire Visualizer

```bash
# Navigate to the visualizer project
cd ../orion-repertoire-visualizer

# Clean and compile
mvn clean compile

# Package the application
mvn package

# Run the application
mvn javafx:run
```

## Alternative: Run with Java directly

If `mvn javafx:run` doesn't work due to module path issues, you can run with explicit JavaFX module path:

```bash
# Set JAVAFX_HOME to your JavaFX SDK location
export JAVAFX_HOME=/path/to/javafx-sdk-21

# Run with explicit module path
java --module-path $JAVAFX_HOME/lib \
     --add-modules javafx.controls,javafx.fxml \
     -cp target/orion-repertoire-visualizer-1.0.0.jar \
     com.orion.visualizer.OrionVisualizerApp
```

## Troubleshooting Compilation Issues

### Issue: JavaFX classes not found during compilation

**Solution 1**: Ensure JavaFX dependencies are correctly downloaded:
```bash
mvn dependency:resolve
ls ~/.m2/repository/org/openjfx/javafx-controls/21.0.1/
```

**Solution 2**: Use a simpler JavaFX version (17 instead of 21):
Edit `pom.xml` and change:
```xml
<javafx.version>17.0.8</javafx.version>
```

**Solution 3**: Build without JavaFX UI (core library only):
```bash
# Comment out UI classes temporarily and build core services
mvn clean compile -DskipTests
```

### Issue: OrionDB dependency not found

**Solution**: Ensure OrionDB is installed in local Maven repository:
```bash
ls ~/.m2/repository/com/oriondb/oriondb/0.1.0-SNAPSHOT/
```

If not found, rebuild OrionDB:
```bash
cd ../oriondb-chess-engine
mvn clean install
```

### Issue: Java version mismatch

**Solution**: Check Java version and update if needed:
```bash
java -version  # Should be 11 or higher
javac -version # Should match java version

# Update JAVA_HOME if needed
export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64
```

## Running the Application

### Option 1: Maven JavaFX Plugin (Recommended)

```bash
mvn javafx:run
```

### Option 2: Executable JAR

```bash
java -jar target/orion-repertoire-visualizer-1.0.0.jar
```

**Note**: This requires JavaFX to be in the classpath or module path.

### Option 3: IDE (IntelliJ IDEA / Eclipse)

1. Import project as Maven project
2. Ensure JavaFX SDK is configured in IDE
3. Run `OrionVisualizerApp.java` as Java Application
4. Add VM options if needed:
   ```
   --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml
   ```

## Development Mode

For active development with auto-reload:

```bash
# Terminal 1: Watch for changes and recompile
mvn compile -Dmaven.compiler.useIncrementalCompilation=false

# Terminal 2: Run application
mvn javafx:run
```

## Testing

Run unit tests:
```bash
mvn test
```

Run with test coverage:
```bash
mvn clean test jacoco:report
```

## Creating Distribution Package

Create a standalone distribution:

```bash
# Package with dependencies
mvn clean package

# The JAR will be in: target/orion-repertoire-visualizer-1.0.0.jar
```

For platform-specific installers (requires jpackage):

```bash
# Windows
jpackage --input target --main-jar orion-repertoire-visualizer-1.0.0.jar \
         --name "Orion Repertoire Visualizer" --type msi

# macOS
jpackage --input target --main-jar orion-repertoire-visualizer-1.0.0.jar \
         --name "Orion Repertoire Visualizer" --type dmg

# Linux
jpackage --input target --main-jar orion-repertoire-visualizer-1.0.0.jar \
         --name "Orion Repertoire Visualizer" --type deb
```

## Common Build Errors and Solutions

| Error | Cause | Solution |
|-------|-------|----------|
| `module not found: javafx.controls` | JavaFX modules not in module path | Use classpath instead of module path |
| `cannot find symbol: class GridPane` | JavaFX not on classpath | Check Maven dependencies |
| `invalid target release: 17` | Java 17 not installed | Install Java 17 or change to Java 11 |
| `Failed to execute goal...oriondb` | OrionDB not built | Build OrionDB first |

## Performance Tuning

For better performance with large databases:

```bash
# Increase heap size
export MAVEN_OPTS="-Xmx2g -Xms512m"
mvn javafx:run

# Or when running JAR directly
java -Xmx2g -jar target/orion-repertoire-visualizer-1.0.0.jar
```

## Clean Build

To start fresh:

```bash
# Clean all build artifacts
mvn clean

# Clean and rebuild everything
mvn clean install

# Clean Maven cache for this project
rm -rf ~/.m2/repository/com/orion/orion-repertoire-visualizer
```

## Next Steps

After successful build:
1. Import a PGN file (File → Import PGN)
2. Apply filters to analyze specific players/time periods
3. Explore the opening tree visualization
4. Review the User Guide in README.md

## Support

If you encounter issues not covered here:
1. Check the main README.md
2. Review error logs in `target/` directory
3. Open an issue on GitHub with full error output
