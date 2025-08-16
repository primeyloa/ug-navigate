# UG Campus Route Finder

A Java application to find the optimal route between locations on the UG campus map, considering real-time traffic. This project was built to demonstrate core software engineering principles, including graph algorithms, data abstraction, and building multiple user interfaces on a shared logic core.

## Features

- **Shortest Path Finding**: Uses Dijkstra's algorithm to find the most efficient route based on distance and simulated traffic.
- **Dynamic Traffic Simulation**: Includes a framework for providing traffic data that can be extended to use live APIs. The default implementation simulates peak and off-peak campus hours.
- **Dual Interfaces**: The application can be run in two modes:
  1. A fast and lightweight **Console App** for scripting and quick lookups.
  2. A user-friendly **GUI App** (built with Java Swing) for interactive use.
- **Advanced Algorithm Stubs**: Contains conceptual placeholders to show how other algorithms like Vogel's Approximation Method could be used for related logistics problems (e.g., a shuttle bus optimizer).
- **Unit Tested**: The core pathfinding logic is verified by a suite of JUnit 5 tests.

## How to Run the Application

### Prerequisites
- Java Development Kit (JDK) 11 or later.
- The compiled classes in the `bin/` directory.

### From the Command Line

First, compile the entire project:
```bash
# Create a directory for compiled files
mkdir -p bin

# Compile the source code
javac -d bin -cp src/main/java src/main/java/com/myuser/ugcampusroutefinder/core/*.java src/main/java/com/myuser/ugcampusroutefinder/model/*.java src/main/java/com/myuser/ugcampusroutefinder/ui/*.java
```

#### To Run the Console App:
```bash
java -cp bin:src/main/resources com.myuser.ugcampusroutefinder.ui.ConsoleApp
```
Follow the on-screen prompts to enter a start and end location. Type `exit` to quit.

#### To Run the GUI App:
```bash
java -cp bin:src/main/resources com.myuser.ugcampusroutefinder.ui.GuiApp
```
This will launch the graphical user interface.

### How to Run the Tests
To run the verification tests for the core logic, you will need the JUnit Platform Console Standalone JAR.

1.  **Download the JUnit JAR**:
    ```bash
    curl -L -o junit-platform-console-standalone.jar https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.10.2/junit-platform-console-standalone-1.10.2.jar
    ```

2.  **Compile the Test Files**:
    ```bash
    javac -d bin -cp "bin:junit-platform-console-standalone.jar" src/test/java/com/myuser/ugcampusroutefinder/core/PathfinderTest.java
    ```

3.  **Run the Tests**:
    ```bash
    java -jar junit-platform-console-standalone.jar --classpath "bin:src/test/resources" --scan-classpath
    ```

## Project Structure
- `src/main/java/com/myuser/ugcampusroutefinder/`: Main application source code.
  - `core/`: Contains the main logic (CampusGraph, Pathfinder, etc.).
  - `model/`: Contains the data classes (Location, Route, Path).
  - `ui/`: Contains the Console and GUI application entry points.
- `src/main/resources/`: Non-code resources.
  - `data/campus_routes.csv`: The primary map data file.
- `src/test/java/`: Source code for unit tests.
- `src/test/resources/`: Resource files used exclusively for testing.
- `README.md`: This file.
