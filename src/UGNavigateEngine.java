// UG Navigate: Main Routing Engine with Advanced Optimizations

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * Main routing engine that coordinates all algorithms and optimizations
 * Implements Divide and Conquer, Greedy, and Dynamic Programming strategies
 */
public class UGNavigateEngine {

    private CampusGraph campusGraph;
    private LandmarkSearch.LandmarkSearchEngine searchEngine;
    private FloydWarshallPathfinder.AllPairsResult precomputedPaths;
    private ExecutorService threadPool;
    private Map<String, List<Route>> routeCache; // Dynamic Programming cache
    private TrafficManager trafficManager;

    public UGNavigateEngine() {
        this.campusGraph = new CampusGraph();
        this.threadPool = Executors.newFixedThreadPool(4); // For parallel processing
        this.routeCache = new ConcurrentHashMap<>();
        this.trafficManager = new TrafficManager();
        initializeCampusData();
        this.searchEngine = new LandmarkSearch.LandmarkSearchEngine(campusGraph);
    }

    public CampusGraph getCampusGraph() {
        return campusGraph;
    }

    /**
     * Main route finding method with multiple optimization strategies
     */
    public RouteResult findOptimalRoutes(String sourceId, String destinationId,
                                         RoutePreferences preferences) {
        long startedAt = System.currentTimeMillis();
        Location source = campusGraph.getLocationById(sourceId);
        Location destination = campusGraph.getLocationById(destinationId);

        if (source == null || destination == null) {
            return new RouteResult(new ArrayList<>(), "Invalid source or destination");
        }

        // Check cache first (Dynamic Programming optimization)
        String cacheKey = generateCacheKey(sourceId, destinationId, preferences);
        if (routeCache.containsKey(cacheKey)) {
            return new RouteResult(routeCache.get(cacheKey), "Routes found (cached)");
        }

        // Update traffic conditions in real-time
        trafficManager.updateTrafficConditions(campusGraph);

        List<Route> allRoutes = new ArrayList<>();

        try {
            // Divide and Conquer: Use parallel processing for different algorithms
            List<Future<List<Route>>> futures = new ArrayList<>();

            // Dijkstra's algorithm (multiple paths)
            futures.add(threadPool.submit(() ->
                    DijkstraPathfinder.findMultiplePaths(campusGraph, source, destination,
                            preferences.transportMode, 3)
            ));

            // A* algorithm (optimal single path)
            futures.add(threadPool.submit(() -> {
                Route aStarRoute = AStarPathfinder.findOptimalPath(campusGraph, source,
                        destination, preferences.transportMode);
                return aStarRoute != null ? Arrays.asList(aStarRoute) : new ArrayList<Route>();
            }));

            // Landmark-based routes if landmarks specified
            if (preferences.landmarks != null && preferences.landmarks.length > 0) {
                futures.add(threadPool.submit(() ->
                        searchEngine.findRoutesWithLandmarks(source, destination,
                                preferences.transportMode, preferences.landmarks)
                ));
            }

            // Collect results from parallel execution
            for (Future<List<Route>> future : futures) {
                try {
                    List<Route> routes = future.get(5, TimeUnit.SECONDS); // 5-second timeout
                    allRoutes.addAll(routes);
                } catch (TimeoutException e) {
                    future.cancel(true);
                    System.err.println("Route calculation timed out for one algorithm");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new RouteResult(new ArrayList<>(), "Error calculating routes: " + e.getMessage());
        }

        // Remove duplicates and apply filtering
        allRoutes = removeDuplicates(allRoutes);
        allRoutes = applyGreedyOptimization(allRoutes, preferences);

        // Apply user filters
        if (preferences.filter != null) {
            allRoutes = searchEngine.filterRoutes(allRoutes, preferences.filter);
        }

        // Sort routes based on preferences
        sortRoutes(allRoutes, preferences.sortCriteria);

        // Limit to top routes
        allRoutes = allRoutes.stream().limit(preferences.maxRoutes).collect(Collectors.toList());

        // Cache results (Dynamic Programming)
        routeCache.put(cacheKey, allRoutes);

        long duration = System.currentTimeMillis() - startedAt;
        return new RouteResult(allRoutes, "Routes found successfully", duration, startedAt);
    }

    /**
     * Greedy optimization to select best routes based on multiple criteria
     */
    private List<Route> applyGreedyOptimization(List<Route> routes, RoutePreferences preferences) {
        if (routes.size() <= preferences.maxRoutes) return routes;

        // Greedy selection based on composite score
        routes.sort((r1, r2) -> {
            double score1 = calculateRouteScore(r1, preferences);
            double score2 = calculateRouteScore(r2, preferences);
            return Double.compare(score2, score1); // Higher score is better
        });

        // Select diverse routes using greedy approach
        List<Route> selectedRoutes = new ArrayList<>();
        selectedRoutes.add(routes.get(0)); // Always include the best route

        for (int i = 1; i < routes.size() && selectedRoutes.size() < preferences.maxRoutes; i++) {
            Route candidate = routes.get(i);
            if (isRouteDiverse(candidate, selectedRoutes, preferences)) {
                selectedRoutes.add(candidate);
            }
        }

        return selectedRoutes;
    }

    /**
     * Calculate composite score for route ranking (Greedy optimization)
     */
    private double calculateRouteScore(Route route, RoutePreferences preferences) {
        double score = 0;

        // Normalize and weight different factors
        double maxTime = 60.0; // 60 minutes max reasonable time
        double maxDistance = 5000.0; // 5km max reasonable distance

        // Time factor (lower is better)
        double timeFactor = Math.max(0, (maxTime - route.getTotalTime()) / maxTime);
        score += timeFactor * preferences.timeWeight;

        // Distance factor (lower is better)
        double distanceFactor = Math.max(0, (maxDistance - route.getTotalDistance()) / maxDistance);
        score += distanceFactor * preferences.distanceWeight;

        // Landmark factor (more landmarks can be better for sightseeing)
        double landmarkFactor = Math.min(1.0, route.getLandmarksPassedThrough().size() / 5.0);
        score += landmarkFactor * preferences.landmarkWeight;

        return score;
    }

    /**
     * Check if a route provides diversity compared to already selected routes
     */
    private boolean isRouteDiverse(Route candidate, List<Route> selected, RoutePreferences preferences) {
        for (Route existing : selected) {
            double pathSimilarity = calculatePathSimilarity(candidate, existing);
            if (pathSimilarity > 0.7) { // 70% similarity threshold
                return false;
            }
        }
        return true;
    }

    /**
     * Calculate similarity between two route paths
     */
    private double calculatePathSimilarity(Route route1, Route route2) {
        Set<String> path1 = route1.getPath().stream().map(Location::getId).collect(Collectors.toSet());
        Set<String> path2 = route2.getPath().stream().map(Location::getId).collect(Collectors.toSet());

        Set<String> intersection = new HashSet<>(path1);
        intersection.retainAll(path2);

        Set<String> union = new HashSet<>(path1);
        union.addAll(path2);

        return union.isEmpty() ? 0 : (double) intersection.size() / union.size();
    }

    /**
     * Advanced route sorting with multiple algorithms
     */
    private void sortRoutes(List<Route> routes, String criteria) {
        switch (criteria.toLowerCase()) {
            case "distance":
                RouteSorting.QuickSort.sortByDistance(routes);
                break;
            case "time":
                RouteSorting.MergeSort.sortByTime(routes);
                break;
            case "landmarks":
                RouteSorting.QuickSort.sortByLandmarks(routes);
                break;
            case "adaptive":
                RouteSorting.AdaptiveSorting.sortOptimal(routes, "composite");
                break;
            default:
                RouteSorting.MergeSort.sortByComposite(routes);
        }
    }

    /**
     * Remove duplicate routes using advanced comparison
     */
    private List<Route> removeDuplicates(List<Route> routes) {
        Map<String, Route> uniqueRoutes = new LinkedHashMap<>();

        for (Route route : routes) {
            String signature = generateRouteSignature(route);
            if (!uniqueRoutes.containsKey(signature) ||
                    route.getTotalTime() < uniqueRoutes.get(signature).getTotalTime()) {
                uniqueRoutes.put(signature, route);
            }
        }

        return new ArrayList<>(uniqueRoutes.values());
    }

    /**
     * Generate unique signature for route comparison
     */
    private String generateRouteSignature(Route route) {
        // Use full ordered path to avoid considering looped routes as unique
        return route.getTransportMode()+":"+route.getPath().stream().map(Location::getId).collect(Collectors.joining("->"));
    }

    /**
     * Generate cache key for Dynamic Programming optimization
     */
    private String generateCacheKey(String source, String destination, RoutePreferences preferences) {
        StringBuilder key = new StringBuilder();
        key.append(source).append("|").append(destination).append("|");
        key.append(preferences.transportMode).append("|");
        key.append(preferences.sortCriteria).append("|");
        key.append(preferences.maxRoutes);

        if (preferences.landmarks != null) {
            key.append("|").append(String.join(",", preferences.landmarks));
        }

        return key.toString();
    }

    /**
     * Get real-time route updates
     */
    public RouteUpdate getRouteUpdate(String routeId) {
        // Implementation for real-time updates (traffic, closures, etc.)
        return new RouteUpdate(routeId, trafficManager.getCurrentConditions());
    }

    /**
     * Initialize campus data with UG-specific locations and connections
     */
    private void initializeCampusData() {
        // This would typically load from a database or configuration file
        // For demo purposes, adding some sample UG campus locations

        // Academic buildings
        Location mainLibrary = new Location("LIB001", "Balme Library", 5.6509, -0.1892, "academic");
        mainLibrary.addKeyword("library");
        mainLibrary.addKeyword("books");
        mainLibrary.addKeyword("study");

        Location greatHall = new Location("GH001", "Great Hall", 5.6518, -0.1885, "academic");
        greatHall.addKeyword("hall");
        greatHall.addKeyword("ceremony");
        greatHall.addKeyword("graduation");

        Location cbas = new Location("CBAS001", "CBAS Building", 5.6515, -0.1890, "academic");
        cbas.addKeyword("computer science");
        cbas.addKeyword("biology");
        cbas.addKeyword("cbas");

        // Service locations
        Location gcb = new Location("BANK001", "GCB Bank", 5.6512, -0.1888, "service");
        gcb.addKeyword("bank");
        gcb.addKeyword("atm");
        gcb.addKeyword("money");

        Location clinic = new Location("CLINIC001", "University Clinic", 5.6520, -0.1895, "service");
        clinic.addKeyword("hospital");
        clinic.addKeyword("clinic");
        clinic.addKeyword("medical");
        clinic.addKeyword("health");

        // Recreational
        Location sportsComplex = new Location("SPORT001", "Sports Complex", 5.6505, -0.1880, "recreational");
        sportsComplex.addKeyword("sports");
        sportsComplex.addKeyword("gym");
        sportsComplex.addKeyword("football");
        sportsComplex.addKeyword("basketball");

        // Add locations to graph
        campusGraph.addLocation(mainLibrary);
        campusGraph.addLocation(greatHall);
        campusGraph.addLocation(cbas);
        campusGraph.addLocation(gcb);
        campusGraph.addLocation(clinic);
        campusGraph.addLocation(sportsComplex);

        // Add edges (roads/paths) between locations
        campusGraph.addEdge(new Edge(mainLibrary, greatHall, 200, 2.5, 1.0, "Main Campus Road"));
        campusGraph.addEdge(new Edge(greatHall, cbas, 150, 2.0, 0.8, "Academic Avenue"));
        campusGraph.addEdge(new Edge(cbas, gcb, 100, 1.5, 0.6, "Service Lane"));
        campusGraph.addEdge(new Edge(gcb, clinic, 180, 2.2, 1.0, "Health Center Road"));
        campusGraph.addEdge(new Edge(clinic, sportsComplex, 300, 3.5, 1.5, "Sports Road"));
        campusGraph.addEdge(new Edge(mainLibrary, cbas, 250, 3.0, 1.2, "Library Road"));
        campusGraph.addEdge(new Edge(greatHall, sportsComplex, 400, 5.0, 2.0, "Campus Boulevard"));

        // Precompute all-pairs shortest paths for performance optimization
        try {
            precomputedPaths = FloydWarshallPathfinder.computeAllPairsShortestPaths(campusGraph, "walking");
        } catch (Exception e) {
            System.err.println("Failed to precompute paths: " + e.getMessage());
        }
    }

    /**
     * Get available locations for user selection
     */
    public List<Location> getAvailableLocations() {
        return new ArrayList<>(campusGraph.getAllLocations());
    }

    /**
     * Search locations by keyword
     */
    public List<Location> searchLocations(String keyword) {
        return new ArrayList<>(searchEngine.findLocationsByKeyword(keyword));
    }

    /**
     * Get campus statistics
     */
    public CampusStats getCampusStats() {
        return new CampusStats(
                campusGraph.getLocationCount(),
                campusGraph.getEdgeCount(),
                routeCache.size()
        );
    }

    /**
     * Clear route cache (useful for memory management)
     */
    public void clearCache() {
        routeCache.clear();
    }

    /**
     * Shutdown the engine and cleanup resources
     */
    public void shutdown() {
        threadPool.shutdown();
        try {
            if (!threadPool.awaitTermination(5, TimeUnit.SECONDS)) {
                threadPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            threadPool.shutdownNow();
        }
    }
}

/**
 * Traffic management system for dynamic route optimization
 */
