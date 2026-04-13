# FIJI - ForthIsh Java Interpreter

FIJI is a Forth-like interpreter implemented in Java, providing an interactive programming environment with stack-based operations and extensibility through Java integration.

**Project Status:** Migrated from [SourceForge](https://sourceforge.net/projects/fiji/) on 2023-02-22

## Table of Contents

- [Overview](#overview)
- [System Requirements](#system-requirements)
- [Building FIJI](#building-fiji)
- [Usage](#usage)
- [Project Structure](#project-structure)
- [Examples](#examples)
- [Contributing](#contributing)
- [License](#license)

## Overview

FIJI (ForthIsh Java Interpreter) is a stack-based programming language interpreter that combines Forth-like syntax with Java's object-oriented capabilities. It provides:

- Interactive command-line interpreter
- GUI-based development environment
- Integration with Java classes and libraries
- Extensible word definitions
- Desktop application framework

## System Requirements

- **Java Development Kit (JDK):** Version 1.8 or higher
- **Build Tool:** Apache Ant (for building from source)
- **Optional:** NetBeans IDE (project includes NetBeans configuration)

### Dependencies

- JTOpen library (jt400) - for IBM i integration (optional)

## Building FIJI

### Using Apache Ant

1. Clone or download the repository:
   ```bash
   git clone <repository-url>
   cd fiji
   ```

2. Build the project:
   ```bash
   ant clean
   ant jar
   ```

3. The compiled JAR file will be created at `dist/FIJI.jar`

### Using NetBeans

1. Open the project in NetBeans IDE
2. Select **Run → Clean and Build Project**
3. The JAR file will be generated in the `dist/` directory

## Usage

### Command-Line Mode

Run FIJI in interactive command-line mode:

```bash
java -jar dist/FIJI.jar
```

You can also run directly from the compiled classes:

```bash
java -cp build/classes com.SoftWoehr.FIJI.FIJI
```

### GUI Mode

Launch FIJI with the graphical user interface:

```bash
java -jar dist/FIJI.jar --gui
```

Or use the short form:

```bash
java -jar dist/FIJI.jar -g
```

### Basic FIJI Commands

Once in the FIJI interpreter, you can use Forth-like stack operations:

```forth
\ Push numbers onto the stack
5 10 +        \ Adds 5 and 10, result: 15
.             \ Prints the top of stack

\ Define a word
: square dup * ;
5 square .    \ Prints 25

\ Java integration
" java.util.Date " new .  \ Creates a new Date object
```

## Project Structure

```
fiji/
├── src/                          # Source code
│   └── com/SoftWoehr/
│       ├── FIJI/                 # Core FIJI interpreter
│       │   ├── base/             # Base classes and utilities
│       │   │   ├── desktop/      # Desktop application framework
│       │   │   └── shell/        # Interpreter engine
│       │   └── examples/         # Example FIJI programs
│       ├── JaXWT/                # GUI components
│       └── util/                 # Utility classes
├── html/                         # Documentation
├── nbproject/                    # NetBeans project files
├── build.xml                     # Ant build script
└── README.md                     # This file
```

## Examples

Example FIJI programs can be found in:

- `src/com/SoftWoehr/FIJI/examples/fiji/` - Basic FIJI examples
- `src/com/SoftWoehr/FIJI/examples/bmbcon/` - Advanced examples with Java integration

### Running Examples

```bash
# From the FIJI interpreter
include src/com/SoftWoehr/FIJI/examples/fiji/testfiji.fiji
```

## Contributing

Contributions to FIJI are welcome! Here's how you can help:

### Reporting Issues

- Use the issue tracker to report bugs or suggest features
- Provide detailed information about your environment and steps to reproduce

### Submitting Changes

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/your-feature`)
3. Make your changes following the existing code style
4. Test your changes thoroughly
5. Commit with clear, descriptive messages
6. Push to your fork and submit a pull request

### Code Style

- Follow Java naming conventions
- Document public APIs with Javadoc comments
- Keep methods focused and concise
- Add examples for new features

## License

FIJI is free software distributed under the GNU Lesser General Public License (LGPL).

See the following files for license details:
- `LICENSE.TXT` - Main license file
- `COPYING.LESSER` - LGPL license text
- `About_License.txt` - Additional license information

**NO WARRANTY - NO GUARANTEE**

This software is provided as-is without any warranty or guarantee of fitness for any particular purpose.

---

**Author:** Jack J. Woehr  
**Website:** http://www.softwoehr.com  
**Copyright:** © 2001-2015 Jack J. Woehr. All Rights Reserved.
