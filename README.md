# UG Navigate - University of Ghana Campus Navigation System

## Overview

UG Navigate is a comprehensive Java Swing desktop application that provides intelligent navigation across the University of Ghana campus. The system combines multiple graph algorithms, real-time traffic simulation, and an intuitive user interface to help students, staff, and visitors find optimal routes between campus locations.

## 🎯 Main Goal

The primary objective is to provide an intelligent, user-friendly navigation system that:
- Finds optimal routes using multiple algorithmic approaches
- Adapts to real-time traffic conditions
- Offers multiple route alternatives based on different criteria
- Provides step-by-step directions with embedded mapping
- Supports both shortest distance and shortest time routing

## 🏗️ Architecture Overview

The application follows a modular architecture with clear separation of concerns:

```
UG Navigate Application
├── User Interface Layer (Swing GUI)
├── Routing Engine Layer (Pathfinding Algorithms)
├── Data Layer (Campus Graph & Location Data)
├── Utility Layer (Calculations & Monitoring)
└── External Integration (OpenStreetMap APIs)
```

## 📁 File Structure & Components

### 1. Core Application Files

#### `UGNavigateApplication.java` - Main GUI Controller
**Purpose**: The central user interface that orchestrates all application functionality.

**Key Responsibilities**:
- **UI Management**: Creates and manages all Swing components
- **Theme System**: Implements dark/light mode with `Ctrl+T` toggle
- **User Input Handling**: Processes source/destination inputs with autocomplete
- **Route Display**: Shows results with embedded map and step-by-step directions
- **Algorithm Integration**: Coordinates between different routing engines

**Key Features**:
```java
// Theme management
private AppTheme currentTheme = AppTheme.LIGHT;
private void applyTheme(AppTheme theme) // Dynamic UI recoloring

// Autocomplete system
private AutoCompleteOverlay sourceAutoComplete;
private AutoCompleteOverlay destinationAutoComplete;

// Embedded mapping
private OSMMapPanel mapPanel; // Custom OpenStreetMap renderer
```

#### `UGNavigateEngine.java` - Base Routing Engine
**Purpose**: Provides fundamental pathfinding capabilities using Dijkstra's algorithm.

**Core Methods**:
- `findOptimalRoutes()`: Main entry point for route calculation
- `findShortestPath()`: Single shortest path using Dijkstra
- `calculateRouteMetrics()`: Computes distance, time, and traffic impact

#### `UGNavigateEngineEnhanced.java` - Advanced Routing Engine
**Purpose**: Extends the base engine with parallel algorithm execution and campus-specific optimizations.

**Enhanced Features**:
- **Parallel Processing**: Executes multiple algorithms simultaneously
- **Multi-Strategy Routing**: Combines Dijkstra, A*, and Floyd-Warshall results
- **Route Diversity**: Generates multiple alternative routes
- **Performance Optimization**: Caches routes and pre-computes shortest paths

```java
// Parallel algorithm execution
ExecutorService executor = Executors.newFixedThreadPool(3);
Future<List<Route>> dijkstraFuture = executor.submit(() -> 
    dijkstraPathfinder.findShortestPath(source, destination, graph));
```

### 2. Graph & Data Management

#### `CampusGraph.java` - Graph Data Structure
**Purpose**: Represents the campus as a directed graph with locations and connections.

**Key Components**:
- **Adjacency List**: `Map<Location, List<Edge>>` for efficient traversal
- **Location Management**: Methods to add, find, and query locations
- **Edge Management**: Handles road connections with traffic data

```java
public class CampusGraph {
    private Map<Location, List<Edge>> adjacencyList;
    private List<Location> locations;
    
    public List<Edge> getEdges(Location location) // Get outgoing edges
    public void addLocation(Location location) // Add new location
    public Location findLocationByName(String name) // Fuzzy search
}
```

#### `UGCampusDataEnhanced.java` - Campus Data Repository
**Purpose**: Contains real University of Ghana campus locations with accurate coordinates.

**Data Sources**:
- **OpenStreetMap APIs**: Nominatim and Photon for coordinate accuracy
- **Campus Infrastructure**: Buildings, halls, libraries, service points
- **Road Network**: Internal roads and pathways with traffic data

**Key Locations**:
- Academic buildings (Balme Library, Great Hall, Faculty of Arts)
- Student halls (Kwapong Hall, Hilla Limann Hall, Jean Nelson Aka Hall)
- Service points (Health Center, Security Post, Sports Complex)

#### `Location.java` - Location Entity
**Purpose**: Represents a physical location on campus.

**Properties**:
```java
public class Location {
    private String name;           // Human-readable name
    private double latitude;       // GPS coordinates
    private double longitude;
    private LocationType type;     // BUILDING, HALL, SERVICE, etc.
    private String description;    // Additional details
}
```

#### `Edge.java` - Road Connection
**Purpose**: Represents a road or pathway between two locations.

**Properties**:
```java
public class Edge {
    private Location source;       // Starting location
    private Location destination;  // Ending location
    private double distance;       // Physical distance in meters
    private double time;           // Travel time in minutes
    private String roadName;       // Road identifier
    private TrafficCondition trafficCondition; // Current traffic state
}
```

### 3. Pathfinding Algorithms

#### `DijkstraPathfinder.java` - Dijkstra's Algorithm Implementation
**Purpose**: Finds the shortest path between two locations using Dijkstra's algorithm.

**Algorithm Details**:
- **Time Complexity**: O((V + E) log V) with priority queue
- **Space Complexity**: O(V)
- **Use Case**: Optimal for single shortest path queries

```java
public List<Location> findShortestPath(Location source, Location destination, CampusGraph graph) {
    PriorityQueue<PathNode> pq = new PriorityQueue<>();
    Map<Location, Double> distances = new HashMap<>();
    Map<Location, Location> previous = new HashMap<>();
    
    // Standard Dijkstra implementation with priority queue
}
```

#### `AStarPathfinder.java` - A* Search Algorithm
**Purpose**: Finds optimal paths using heuristic-based search.

**Key Features**:
- **Heuristic Function**: Uses straight-line distance as admissible heuristic
- **Optimality**: Guarantees shortest path when heuristic is admissible
- **Performance**: Often faster than Dijkstra for large graphs

```java
private double heuristic(Location current, Location goal) {
    return DistanceCalculator.calculateDistance(current, goal);
}
```

#### `FloydWarshallPathfinder.java` - All-Pairs Shortest Paths
**Purpose**: Pre-computes shortest paths between all location pairs.

**Optimizations**:
- **Location Indexing**: Pre-computes location-to-index mapping
- **Memory Efficiency**: Uses adjacency matrix for dense graphs
- **Caching**: Stores results for repeated queries

```java
// Pre-compute location indices for O(1) lookup
Map<Location, Integer> locationIndex = new HashMap<>();
for (int i = 0; i < locations.size(); i++) {
    locationIndex.put(locations.get(i), i);
}
```

### 4. Route Management & Analysis

#### `Route.java` - Route Representation
**Purpose**: Encapsulates a complete route with all relevant metrics.

**Properties**:
```java
public class Route {
    private List<Location> path;           // Sequence of locations
    private List<Edge> edges;              // Road segments used
    private double totalDistance;          // Total distance in meters
    private double totalTime;              // Total time in minutes
    private double trafficImpact;          // Traffic delay factor
    private RouteCriteria criteria;        // Optimization criteria used
}
```

#### `RouteResult.java` - Route Calculation Results
**Purpose**: Contains the outcome of a route finding operation.

**Features**:
- **Multiple Routes**: Stores up to 5 alternative routes
- **Performance Metrics**: Includes calculation time and algorithm used
- **Error Handling**: Contains error messages if route finding fails

#### `RouteSorting.java` - Route Analysis Algorithms
**Purpose**: Provides various sorting and analysis algorithms for routes.

**Implemented Algorithms**:
- **Adaptive Sorting**: Chooses best sorting method based on criteria
- **Multi-Criteria Analysis**: Combines distance, time, and traffic factors
- **Route Diversity**: Ensures alternative routes are genuinely different

### 5. Traffic & Real-Time Features

#### `TrafficManager.java` - Dynamic Traffic Simulation
**Purpose**: Simulates real-time traffic conditions affecting route planning.

**Features**:
- **Time-Based Traffic**: Different conditions for morning, afternoon, evening
- **Random Events**: Road closures and traffic incidents
- **Dynamic Updates**: Adjusts edge weights based on current conditions

```java
public class TrafficManager {
    private Map<String, Double> currentTrafficConditions;
    private Random random = new Random();
    
    public double getTrafficMultiplier(String roadName) {
        // Returns traffic multiplier based on time and random events
    }
}
```

#### `RouteUpdate.java` - Real-Time Updates
**Purpose**: Represents dynamic route updates due to changing conditions.

### 6. User Interface Components

#### `OSMMapPanel.java` - Embedded Map Component
**Purpose**: Custom Swing component for rendering OpenStreetMap tiles and routes.

**Features**:
- **Tile Rendering**: Downloads and displays OpenStreetMap tiles
- **Route Visualization**: Draws polylines for calculated routes
- **Interactive Controls**: Mouse wheel zoom, click-drag panning
- **Multiple Providers**: Fallback tile providers for reliability

```java
public class OSMMapPanel extends JPanel {
    private BufferedImage mapImage;
    private List<Point> routePoints;
    private double zoom = 16.0;
    private Point center;
    
    public void setPath(List<Location> path) // Draw route on map
    public void setMarkers(Location start, Location end) // Add markers
}
```

#### `AutoCompleteOverlay.java` - Smart Input Suggestions
**Purpose**: Provides non-intrusive autocomplete for location input.

**Key Features**:
- **Non-Focusable**: Doesn't steal focus from text fields
- **Fuzzy Matching**: Finds locations even with partial/incorrect input
- **Keyboard Navigation**: Arrow keys for suggestion selection
- **Theme Integration**: Adapts to current dark/light mode

### 7. Utility & Support Classes

#### `DistanceCalculator.java` - Geographic Calculations
**Purpose**: Provides accurate distance calculations using Haversine formula.

```java
public static double calculateDistance(Location loc1, Location loc2) {
    // Haversine formula for accurate geographic distance
    double lat1 = Math.toRadians(loc1.getLatitude());
    double lat2 = Math.toRadians(loc2.getLatitude());
    // ... calculation details
}
```

#### `PerformanceMonitor.java` - System Monitoring
**Purpose**: Tracks algorithm performance and system metrics.

#### `CampusStats.java` - Campus Analytics
**Purpose**: Provides statistics about the campus graph and routing data.

#### `AlgorithmDemo.java` - Algorithm Demonstrations
**Purpose**: Shows additional algorithms (NCM, VAM, CPM) as requested in project brief.

### 8. Configuration & Preferences

#### `RoutePreferences.java` - User Preferences
**Purpose**: Defines user preferences for route calculation.

```java
public class RoutePreferences {
    private RouteCriteria criteria;        // DISTANCE, TIME, TRAFFIC
    private int maxRoutes;                 // Number of alternatives
    private boolean avoidTraffic;          // Traffic avoidance flag
    private List<Location> landmarks;      // Preferred landmarks
}
```

## 🔄 How It All Works Together

### 1. Application Startup
1. **Initialization**: `UGNavigateApplication` creates the GUI
2. **Data Loading**: `UGCampusDataEnhanced` populates the campus graph
3. **Theme Application**: Loads saved theme preference or defaults to light mode
4. **Map Initialization**: `OSMMapPanel` loads initial map tiles

### 2. User Input Processing
1. **Text Input**: User types source/destination in text fields
2. **Autocomplete**: `AutoCompleteOverlay` shows matching locations
3. **Input Resolution**: `resolveUserInputToLocation()` converts text to Location objects
4. **Validation**: Ensures source and destination are different locations

### 3. Route Calculation Process
1. **Engine Selection**: Chooses between base and enhanced engines based on criteria
2. **Parallel Execution**: Enhanced engine runs multiple algorithms simultaneously
3. **Algorithm Execution**:
   - **Dijkstra**: Finds shortest path
   - **A***: Finds optimal path with heuristics
   - **Floyd-Warshall**: Pre-computed all-pairs paths
4. **Route Combination**: Merges results from different algorithms
5. **Traffic Integration**: `TrafficManager` adjusts route times based on current conditions
6. **Route Sorting**: `RouteSorting` orders routes by user preferences

### 4. Results Display
1. **Route Visualization**: `OSMMapPanel` draws routes as polylines
2. **Directions Generation**: Creates step-by-step instructions from route edges
3. **Metrics Display**: Shows distance, time, and traffic impact
4. **Alternative Routes**: Presents multiple options with different characteristics

## 🚀 Key Features

### Intelligent Routing
- **Multi-Algorithm Approach**: Combines Dijkstra, A*, and Floyd-Warshall
- **Real-Time Traffic**: Adapts to current traffic conditions
- **Multiple Criteria**: Distance, time, traffic, or balanced optimization
- **Route Alternatives**: Provides up to 5 different route options

### User Experience
- **Dark/Light Mode**: Complete theme system with `Ctrl+T` toggle
- **Embedded Mapping**: No external browser required
- **Smart Autocomplete**: Fuzzy matching with keyboard navigation
- **Step-by-Step Directions**: Clear, actionable navigation instructions

### Performance & Reliability
- **Parallel Processing**: Multi-threaded algorithm execution
- **Route Caching**: Stores computed routes for faster subsequent queries
- **Fallback Systems**: Multiple tile providers and error handling
- **Memory Optimization**: Efficient data structures and cleanup

## 🛠️ Technical Implementation Details

### Algorithm Complexity
- **Dijkstra**: O((V + E) log V) with priority queue
- **A***: O((V + E) log V) with admissible heuristic
- **Floyd-Warshall**: O(V³) for all-pairs shortest paths
- **Route Combination**: O(R²) where R is number of routes

### Memory Management
- **Graph Representation**: Adjacency list for sparse campus graph
- **Route Caching**: ConcurrentHashMap for thread-safe caching
- **Image Management**: BufferedImage for map tiles with cleanup
- **UI Components**: Proper disposal of Swing components

### Error Handling
- **Network Failures**: Graceful degradation for map tile loading
- **Invalid Input**: Comprehensive input validation and user feedback
- **Algorithm Failures**: Fallback to simpler algorithms
- **Memory Issues**: Automatic cleanup and resource management

## 📊 Performance Characteristics

### Campus Graph Statistics
- **Locations**: 35+ campus locations with accurate coordinates
- **Edges**: 50+ road connections with traffic data
- **Graph Density**: Sparse graph optimized for campus layout
- **Update Frequency**: Real-time traffic updates every minute

### Algorithm Performance
- **Route Calculation**: < 100ms for typical campus routes
- **Map Rendering**: < 500ms for initial tile loading
- **Autocomplete**: < 50ms for location matching
- **Theme Switching**: < 100ms for complete UI recoloring

## 📝 Conclusion

UG Navigate represents a comprehensive solution for campus navigation that combines theoretical computer science concepts (graph algorithms, optimization techniques) with practical software engineering (user interface design, real-time systems, external API integration). The modular architecture ensures maintainability and extensibility, while the multi-algorithm approach provides robust and intelligent routing capabilities.
