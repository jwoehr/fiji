# Maven Build Instructions for FIJI

This document describes how to build FIJI using Apache Maven.

## Prerequisites

- **Java Development Kit (JDK):** Version 1.8 or higher
- **Apache Maven:** Version 3.6 or higher

## Building the Project

### Clean and Compile

```bash
mvn clean compile
```

### Run Tests

```bash
mvn test
```

### Package (Create JAR files)

```bash
mvn package
```

This creates several JAR files in the `target/` directory:

- `fiji-1.3.jar` - Main application JAR (requires dependencies in classpath)
- `fiji-1.3-jar-with-dependencies.jar` - Standalone JAR with all dependencies included
- `fiji-1.3-sources.jar` - Source code JAR
- `fiji-1.3-javadoc.jar` - Javadoc JAR

Dependencies are also copied to `target/lib/`:

- `jt400-21.0.6.jar` - JTOpen library for IBM i integration

### Skip Tests During Build

```bash
mvn package -DskipTests
```

### Install to Local Maven Repository

```bash
mvn install
```

## Running FIJI

### Using the Standalone JAR (Recommended)

The standalone JAR includes all dependencies:

```bash
java -jar target/fiji-1.3-jar-with-dependencies.jar
```

### Using the Main JAR with Dependencies

```bash
java -jar target/fiji-1.3.jar
```

This requires the dependencies in `target/lib/` to be in the classpath (handled automatically by the manifest).

### GUI Mode

```bash
java -jar target/fiji-1.3-jar-with-dependencies.jar --gui
```

Or:

```bash
java -jar target/fiji-1.3-jar-with-dependencies.jar -g
```

## Dependencies

The project uses the following dependencies, automatically downloaded from Maven Central:

- **jt400 (JTOpen) 21.0.6** - IBM Toolbox for Java (for IBM i integration)
- **JUnit 4.13.2** - Testing framework (test scope)
- **Hamcrest 2.2** - Matcher library for tests (test scope)

## Maven Project Structure

```text
fiji/
├── pom.xml                    # Maven project configuration
├── src/                       # Source code
├── test/                      # Test code
└── target/                    # Build output (created by Maven)
    ├── classes/               # Compiled classes
    ├── test-classes/          # Compiled test classes
    ├── lib/                   # Runtime dependencies
    ├── fiji-1.3.jar         # Main JAR
    ├── fiji-1.3-jar-with-dependencies.jar  # Standalone JAR
    ├── fiji-1.3-sources.jar # Source JAR
    └── fiji-1.3-javadoc.jar # Javadoc JAR
```

## Common Maven Commands

| Command                                   | Description                           |
|-------------------------------------------|---------------------------------------|
| `mvn clean`                               | Remove all build artifacts            |
| `mvn compile`                             | Compile source code                   |
| `mvn test`                                | Run unit tests                        |
| `mvn package`                             | Create JAR files                      |
| `mvn install`                             | Install to local Maven repository     |
| `mvn clean package`                       | Clean and build from scratch          |
| `mvn dependency:tree`                     | Show dependency tree                  |
| `mvn versions:display-dependency-updates` | Check for dependency updates          |

## IDE Integration

### IntelliJ IDEA

1. Open the project directory
2. IntelliJ will automatically detect the `pom.xml` and import the project

### Eclipse

1. File → Import → Maven → Existing Maven Projects
2. Select the project directory
3. Click Finish

### NetBeans

1. File → Open Project
2. Select the project directory (NetBeans recognizes Maven projects automatically)

## Troubleshooting

### Build Fails with "Tests Failed"

Skip tests during build:

```bash
mvn package -DskipTests
```

### Dependency Download Issues

Clear local Maven cache and retry:

```bash
rm -rf ~/.m2/repository/net/sf/jt400
mvn clean package
```

### Java Version Issues

Ensure you're using Java 8 or higher:

```bash
java -version
mvn -version
```

## Migration from Ant

The Maven build is equivalent to the Ant build but with these advantages:

1. **Automatic Dependency Management**: jt400 is downloaded from Maven Central
2. **Standard Project Structure**: Follows Maven conventions
3. **Better IDE Integration**: Most IDEs have excellent Maven support
4. **Reproducible Builds**: Dependencies are versioned and cached
5. **Multiple Output Formats**: Creates regular JAR, standalone JAR, sources, and javadoc

The Ant build files (`build.xml`, `nbproject/`) are still present and functional for backward compatibility.
