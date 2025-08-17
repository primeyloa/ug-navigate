import java.util.*;
import java.util.concurrent.*;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * Enhanced UG Navigate Engine with Real University of Ghana Campus Data
 * Implements advanced algorithms with authentic UG locations, buildings, and streets
 */
public class UGNavigateEngineEnhanced {

    CampusGraph campusGraph;
    private LandmarkSearch.LandmarkSearchEngine searchEngine;
    private FloydWarshallPathfinder.AllPairsResult precomputedPaths;
    private ExecutorService threadPool;
    private Map<String, List<Route>> routeCache; // Dynamic Programming cache
    private TrafficManager trafficManager;
    private Map<String, double[]> campusCoordinates;
    private List<String> campusStreets;

    public UGNavigateEngineEnhanced() {
        this.campusGraph = new CampusGraph();
        this.threadPool = Executors.newFixedThreadPool(6); // Increased for larger dataset
        this.routeCache = new ConcurrentHashMap<>();
        this.trafficManager = new TrafficManager();

        // Initialize with real UG campus data
        initializeEnhancedUGCampusData();

        this.searchEngine = new LandmarkSearch.LandmarkSearchEngine(campusGraph);
        this.campusCoordinates = UGCampusDataEnhanced.getLocationCoordinates();
        this.campusStreets = UGCampusDataEnhanced.getCampusStreets();

        System.out.println("🎓 UG Navigate Engine Enhanced - Real Campus Data Loaded");
        printCampusStats();
    }

    /**
     * Enhanced route finding with real campus optimizations
     */
    public RouteResult findOptimalRoutes(String sourceId, String destinationId,
                                         RoutePreferences preferences) {
        Location source = campusGraph.getLocationById(sourceId);
        Location destination = campusGraph.getLocationById(destinationId);

        if (source == null || destination == null) {
            return new RouteResult(new ArrayList<>(), "Invalid source or destination location");
        }

        // Enhanced cache key with campus-specific factors
        String cacheKey = generateEnhancedCacheKey(sourceId, destinationId, preferences);
        if (routeCache.containsKey(cacheKey)) {
            List<Route> cachedRoutes = routeCache.get(cacheKey);
            return new RouteResult(cachedRoutes,
                    String.format("Routes found (cached) - %d options available", cachedRoutes.size()));
        }

        // Real-time campus conditions update
        updateCampusConditions();

        List<Route> allRoutes = new ArrayList<>();

        try {
            // Parallel algorithm execution for enhanced performance
            List<Future<List<Route>>> futures = new ArrayList<>();

            // Primary algorithms
            futures.add(threadPool.submit(() ->
                    DijkstraPathfinder.findMultiplePaths(campusGraph, source, destination,
                            preferences.transportMode, 4))); // Increased paths for larger campus

            futures.add(threadPool.submit(() -> {
                Route aStarRoute = AStarPathfinder.findOptimalPath(campusGraph, source,
                        destination, preferences.transportMode);
                return aStarRoute != null ? Arrays.asList(aStarRoute) : new ArrayList<Route>();
            }));

            // Campus-specific route finding
            if (preferences.landmarks != null && preferences.landmarks.length > 0) {
                futures.add(threadPool.submit(() ->
                        searchEngine.findRoutesWithLandmarks(source, destination,
                                preferences.transportMode, preferences.landmarks)));
            }

            // Hall-specific routing (for residential areas)
            if (isResidentialArea(source) || isResidentialArea(destination)) {
                futures.add(threadPool.submit(() ->
                        findResidentialRoutes(source, destination, preferences.transportMode)));
            }

            // Academic building optimized routes
            if (isAcademicBuilding(source) || isAcademicBuilding(destination)) {
                futures.add(threadPool.submit(() ->
                        findAcademicRoutes(source, destination, preferences.transportMode)));
            }

            // Service facility routes (bank, hospital, etc.)
            if (isServiceFacility(source) || isServiceFacility(destination)) {
                futures.add(threadPool.submit(() ->
                        findServiceRoutes(source, destination, preferences.transportMode)));
            }

            // Collect results with timeout
            for (Future<List<Route>> future : futures) {
                try {
                    List<Route> routes = future.get(8, TimeUnit.SECONDS); // Increased timeout
                    allRoutes.addAll(routes);
                } catch (TimeoutException e) {
                    future.cancel(true);
                    System.err.println("⚠️ Route calculation timed out for one algorithm");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new RouteResult(new ArrayList<>(),
                    "Error calculating routes: " + e.getMessage());
        }

        // Enhanced route processing
        allRoutes = removeDuplicateRoutes(allRoutes);
        allRoutes = applyUGCampusOptimizations(allRoutes, preferences);

        // Apply filters
        if (preferences.filter != null) {
            allRoutes = searchEngine.filterRoutes(allRoutes, preferences.filter);
        }

        // Campus-aware sorting
        sortRoutesWithCampusContext(allRoutes, preferences.sortCriteria, source, destination);

        // Limit results
        allRoutes = allRoutes.stream().limit(preferences.maxRoutes).collect(Collectors.toList());

        // Enhanced route information
        enhanceRoutesWithCampusInfo(allRoutes);

        // Cache with TTL for campus conditions
        routeCache.put(cacheKey, allRoutes);

        String message = String.format("Routes found successfully from %s to %s",
                source.getName(), destination.getName());

        return new RouteResult(allRoutes, message);
    }

    /**
     * Campus-specific route optimizations
     */
    private List<Route> applyUGCampusOptimizations(List<Route> routes, RoutePreferences preferences) {
        if (routes.size() <= preferences.maxRoutes) return routes;

        // UG Campus specific scoring
        routes.sort((r1, r2) -> {
            double score1 = calculateUGCampusScore(r1, preferences);
            double score2 = calculateUGCampusScore(r2, preferences);
            return Double.compare(score2, score1);
        });

        // Ensure diversity in route types (hall routes, academic routes, service routes)
        List<Route> optimizedRoutes = new ArrayList<>();
        Set<String> routeTypes = new HashSet<>();

        for (Route route : routes) {
            String routeType = determineRouteType(route);
            if (optimizedRoutes.size() < preferences.maxRoutes &&
                    (routeTypes.size() < 3 || routeTypes.contains(routeType))) {
                optimizedRoutes.add(route);
                routeTypes.add(routeType);
            }
        }

        return optimizedRoutes;
    }

    /**
     * UG Campus-specific scoring system
     */
    private double calculateUGCampusScore(Route route, RoutePreferences preferences) {
        double score = 0;

        // Base scoring from parent class
        double maxTime = 15.0; // 15 minutes max for campus traversal
        double maxDistance = 2000.0; // 2km max reasonable campus distance

        double timeFactor = Math.max(0, (maxTime - route.getTotalTime()) / maxTime);
        score += timeFactor * preferences.timeWeight;

        double distanceFactor = Math.max(0, (maxDistance - route.getTotalDistance()) / maxDistance);
        score += distanceFactor * preferences.distanceWeight;

        // Campus-specific bonuses

        // Main roads bonus (University Avenue, Academic Road, etc.)
        score += calculateMainRoadBonus(route) * 0.1;

        // Landmark proximity bonus
        score += calculateLandmarkProximityBonus(route) * 0.15;

        // Safety route bonus (well-lit, populated areas)
        score += calculateSafetyBonus(route) * 0.1;

        // Accessibility bonus (wheelchair friendly routes)
        score += calculateAccessibilityBonus(route) * 0.05;

        return score;
    }

    /**
     * Determine route type for diversity
     */
    private String determineRouteType(Route route) {
        List<Location> path = route.getPath();

        // Count different location types in route
        int residential = 0, academic = 0, service = 0, recreational = 0;

        for (Location loc : path) {
            switch (loc.getType()) {
                case "residential": residential++; break;
                case "academic": academic++; break;
                case "service": service++; break;
                case "recreational": recreational++; break;
            }
        }

        // Determine primary route type
        if (residential > academic && residential > service) return "residential";
        if (academic > service && academic > recreational) return "academic";
        if (service > recreational) return "service";
        return "recreational";
    }

    /**
     * Calculate bonus scores for UG campus features
     */
    private double calculateMainRoadBonus(Route route) {
        List<String> mainRoads = Arrays.asList(
                "University Avenue", "Academic Road", "Hall Road", "Faculty Road"
        );

        double bonus = 0;
        for (Edge edge : route.getEdges()) {
            if (mainRoads.contains(edge.getRoadName())) {
                bonus += 0.2;
            }
        }
        return Math.min(bonus, 1.0);
    }

    private double calculateLandmarkProximityBonus(Route route) {
        List<String> majorLandmarks = Arrays.asList(
                "balme", "great hall", "commonwealth", "university square"
        );

        double bonus = 0;
        for (Location loc : route.getPath()) {
            for (String landmark : majorLandmarks) {
                if (loc.getKeywords().stream().anyMatch(k -> k.contains(landmark))) {
                    bonus += 0.1;
                    break;
                }
            }
        }
        return Math.min(bonus, 1.0);
    }

    private double calculateSafetyBonus(Route route) {
        // Routes through well-populated areas (halls, academic buildings, main roads)
        double bonus = 0;
        for (Location loc : route.getPath()) {
            if (loc.getType().equals("academic") || loc.getType().equals("residential")) {
                bonus += 0.05;
            }
        }
        return Math.min(bonus, 1.0);
    }

    private double calculateAccessibilityBonus(Route route) {
        // Bonus for routes with fewer elevation changes and paved roads
        double bonus = 0.8; // Base accessibility score

        // Check for main roads which are typically more accessible
        for (Edge edge : route.getEdges()) {
            if (edge.getRoadName().contains("Avenue") || edge.getRoadName().contains("Road")) {
                bonus += 0.05;
            }
        }

        return Math.min(bonus, 1.0);
    }

    /**
     * Specialized route finding methods
     */
    private List<Route> findResidentialRoutes(Location source, Location destination, String transportMode) {
        // Find routes that prioritize hall connections and night market access
        List<Route> residentialRoutes = new ArrayList<>();

        // Try route through University Square (central hub)
        Location universitySquare = campusGraph.getLocationById("SQUARE001");
        if (universitySquare != null) {
            Route viaSquare = findRouteViaIntermediate(source, universitySquare, destination, transportMode);
            if (viaSquare != null) {
                viaSquare.addLandmark("central hub");
                residentialRoutes.add(viaSquare);
            }
        }

        // Try route through food court for dining access
        Location foodCourt = campusGraph.getLocationById("FOOD001");
        if (foodCourt != null) {
            Route viaFoodCourt = findRouteViaIntermediate(source, foodCourt, destination, transportMode);
            if (viaFoodCourt != null) {
                viaFoodCourt.addLandmark("food court access");
                residentialRoutes.add(viaFoodCourt);
            }
        }

        return residentialRoutes;
    }

    private List<Route> findAcademicRoutes(Location source, Location destination, String transportMode) {
        List<Route> academicRoutes = new ArrayList<>();

        // Route via Balme Library (academic hub)
        Location balmeLibrary = campusGraph.getLocationById("LIB001");
        if (balmeLibrary != null) {
            Route viaLibrary = findRouteViaIntermediate(source, balmeLibrary, destination, transportMode);
            if (viaLibrary != null) {
                viaLibrary.addLandmark("library access");
                academicRoutes.add(viaLibrary);
            }
        }

        return academicRoutes;
    }

    private List<Route> findServiceRoutes(Location source, Location destination, String transportMode) {
        List<Route> serviceRoutes = new ArrayList<>();

        // Route via GCB Bank for financial services
        Location bank = campusGraph.getLocationById("BANK001");
        if (bank != null) {
            Route viaBank = findRouteViaIntermediate(source, bank, destination, transportMode);
            if (viaBank != null) {
                viaBank.addLandmark("banking access");
                serviceRoutes.add(viaBank);
            }
        }

        return serviceRoutes;
    }

    /**
     * Helper method to find route via intermediate location
     */
    private Route findRouteViaIntermediate(Location source, Location intermediate,
                                           Location destination, String transportMode) {
        Route toIntermediate = AStarPathfinder.findOptimalPath(campusGraph, source, intermediate, transportMode);
        Route fromIntermediate = AStarPathfinder.findOptimalPath(campusGraph, intermediate, destination, transportMode);

        if (toIntermediate != null && fromIntermediate != null) {
            return combineRoutes(toIntermediate, fromIntermediate, transportMode);
        }
        return null;
    }

    /**
     * Combine two routes into one
     */
    private Route combineRoutes(Route route1, Route route2, String transportMode) {
        Route combined = new Route(transportMode);

        // Add all locations from first route
        for (Location loc : route1.getPath()) {
            combined.addLocation(loc);
        }

        // Add locations from second route (excluding the connecting point)
        List<Location> secondPath = route2.getPath();
        for (int i = 1; i < secondPath.size(); i++) {
            combined.addLocation(secondPath.get(i));
        }

        // Add all edges
        for (Edge edge : route1.getEdges()) {
            combined.addEdge(edge);
        }
        for (Edge edge : route2.getEdges()) {
            combined.addEdge(edge);
        }

        return combined;
    }

    /**
     * Location type checking methods
     */
    private boolean isResidentialArea(Location location) {
        return location.getType().equals("residential") ||
                location.getKeywords().contains("hall") ||
                location.getKeywords().contains("hostel");
    }

    private boolean isAcademicBuilding(Location location) {
        return location.getType().equals("academic") ||
                location.getKeywords().contains("department") ||
                location.getKeywords().contains("faculty");
    }

    private boolean isServiceFacility(Location location) {
        return location.getType().equals("service") ||
                location.getKeywords().contains("bank") ||
                location.getKeywords().contains("hospital") ||
                location.getKeywords().contains("clinic");
    }

    /**
     * Campus-aware sorting with context
     */
    private void sortRoutesWithCampusContext(List<Route> routes, String criteria,
                                             Location source, Location destination) {
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
                // UG-specific adaptive sorting
                sortByUGCampusContext(routes, source, destination);
                break;
            default:
                RouteSorting.MergeSort.sortByComposite(routes);
        }
    }

    private void sortByUGCampusContext(List<Route> routes, Location source, Location destination) {
        LocalDateTime now = LocalDateTime.now();
        int hour = now.getHour();

        // Different priorities based on time of day
        Comparator<Route> contextComparator;

        if (hour >= 8 && hour <= 17) {
            // Daytime: prioritize academic routes
            contextComparator = (r1, r2) -> {
                double score1 = calculateDaytimeScore(r1);
                double score2 = calculateDaytimeScore(r2);
                return Double.compare(score2, score1);
            };
        } else {
            // Evening/Night: prioritize safety and hall access
            contextComparator = (r1, r2) -> {
                double score1 = calculateNighttimeScore(r1);
                double score2 = calculateNighttimeScore(r2);
                return Double.compare(score2, score1);
            };
        }

        routes.sort(contextComparator);
    }

    private double calculateDaytimeScore(Route route) {
        double score = 1.0 / (1.0 + route.getTotalTime()); // Prefer shorter time

        // Bonus for academic buildings
        for (Location loc : route.getPath()) {
            if (loc.getType().equals("academic")) {
                score += 0.1;
            }
        }

        return score;
    }

    private double calculateNighttimeScore(Route route) {
        double score = calculateSafetyBonus(route); // Prioritize safety

        // Bonus for well-lit main roads
        score += calculateMainRoadBonus(route) * 0.3;

        // Bonus for residential area access
        for (Location loc : route.getPath()) {
            if (loc.getType().equals("residential")) {
                score += 0.15;
            }
        }

        return score;
    }

    /**
     * Enhance routes with campus-specific information
     */
    private void enhanceRoutesWithCampusInfo(List<Route> routes) {
        for (Route route : routes) {
            // Add accessibility information
            boolean isAccessible = checkRouteAccessibility(route);
            if (isAccessible) {
                route.addLandmark("wheelchair accessible");
            }

            // Add estimated crowd level
            String crowdLevel = estimateCrowdLevel(route);
            route.addLandmark("crowd level: " + crowdLevel);

            // Add recommended time to travel
            String timeRecommendation = getTimeRecommendation(route);
            route.addLandmark(timeRecommendation);
        }
    }

    private boolean checkRouteAccessibility(Route route) {
        // Check if route uses main roads and avoids steep paths
        for (Edge edge : route.getEdges()) {
            String roadName = edge.getRoadName().toLowerCase();
            if (roadName.contains("avenue") || roadName.contains("road") || roadName.contains("lane")) {
                continue; // Main roads are generally accessible
            }
            if (roadName.contains("path") || roadName.contains("shortcut")) {
                return false; // Paths may not be accessible
            }
        }
        return true;
    }

    private String estimateCrowdLevel(Route route) {
        LocalDateTime now = LocalDateTime.now();
        int hour = now.getHour();

        // Check for high-traffic locations
        boolean hasHighTrafficLocation = false;
        for (Location loc : route.getPath()) {
            if (loc.getKeywords().contains("library") ||
                    loc.getKeywords().contains("food court") ||
                    loc.getId().equals("SQUARE001")) {
                hasHighTrafficLocation = true;
                break;
            }
        }

        if (hour >= 12 && hour <= 14 && hasHighTrafficLocation) {
            return "high"; // Lunch time at popular spots
        } else if (hour >= 8 && hour <= 17 && hasHighTrafficLocation) {
            return "medium"; // Regular academic hours
        } else {
            return "low";
        }
    }

    private String getTimeRecommendation(Route route) {
        LocalDateTime now = LocalDateTime.now();
        int hour = now.getHour();

        if (hour >= 6 && hour <= 8) {
            return "best for: morning exercise";
        } else if (hour >= 12 && hour <= 14) {
            return "best for: lunch break";
        } else if (hour >= 17 && hour <= 19) {
            return "best for: evening activities";
        } else if (hour >= 20 || hour <= 6) {
            return "best for: night owls (use well-lit routes)";
        } else {
            return "best for: regular activities";
        }
    }

    /**
     * Update campus conditions (traffic, events, closures)
     */
    private void updateCampusConditions() {
        trafficManager.updateTrafficConditions(campusGraph);

        // Simulate campus-specific conditions
        LocalDateTime now = LocalDateTime.now();
        int hour = now.getHour();
        int dayOfWeek = now.getDayOfWeek().getValue(); // 1 = Monday, 7 = Sunday

        // Weekend conditions
        if (dayOfWeek >= 6) {
            // Lighter traffic on weekends
            applyWeekendConditions();
        }

        // Exam period simulation (increased library traffic)
        if (isExamPeriod(now)) {
            increaseLibraryTraffic();
        }

        // Event-based closures (graduation, etc.)
        if (isEventDay(now)) {
            applyEventRestrictions();
        }
    }

    private void applyWeekendConditions() {
        // Reduce traffic multipliers on weekends
        for (Location location : campusGraph.getAllLocations()) {
            for (Edge edge : campusGraph.getNeighbors(location)) {
                double currentMultiplier = edge.getTrafficMultiplier();
                edge.setTrafficMultiplier(currentMultiplier * 0.7); // 30% less traffic
            }
        }
    }

    private void increaseLibraryTraffic() {
        Location library = campusGraph.getLocationById("LIB001");
        if (library != null) {
            for (Edge edge : campusGraph.getNeighbors(library)) {
                edge.setTrafficMultiplier(edge.getTrafficMultiplier() * 1.5);
            }
        }
    }

    private void applyEventRestrictions() {
        // Close some roads near Great Hall during events
        Location greatHall = campusGraph.getLocationById("GH001");
        if (greatHall != null) {
            for (Edge edge : campusGraph.getNeighbors(greatHall)) {
                if (new Random().nextDouble() < 0.3) { // 30% chance of closure
                    edge.setClosed(true);
                }
            }
        }
    }

    private boolean isExamPeriod(LocalDateTime now) {
        int month = now.getMonthValue();
        // Simulate exam periods: April-May and November-December
        return (month >= 4 && month <= 5) || (month >= 11 && month <= 12);
    }

    private boolean isEventDay(LocalDateTime now) {
        int dayOfMonth = now.getDayOfMonth();
        // Simulate occasional events
        return dayOfMonth % 15 == 0; // Every 15th day
    }

    /**
     * Enhanced cache key generation
     */
    private String generateEnhancedCacheKey(String source, String destination, RoutePreferences preferences) {
        StringBuilder key = new StringBuilder();
        key.append(source).append("|").append(destination).append("|");
        key.append(preferences.transportMode).append("|");
        key.append(preferences.sortCriteria).append("|");
        key.append(preferences.maxRoutes);

        // Add time-based cache key components
        LocalDateTime now = LocalDateTime.now();
        key.append("|").append(now.getHour()); // Hour-based caching
        key.append("|").append(now.getDayOfWeek().getValue()); // Day-based caching

        if (preferences.landmarks != null) {
            key.append("|").append(String.join(",", preferences.landmarks));
        }

        return key.toString();
    }

    /**
     * Initialize with enhanced UG campus data
     */
    private void initializeEnhancedUGCampusData() {
        UGCampusDataEnhanced.initializeRealUGCampusData(campusGraph);

        // Precompute paths for performance
        try {
            precomputedPaths = FloydWarshallPathfinder.computeAllPairsShortestPaths(campusGraph, "walking");
        } catch (Exception e) {
            System.err.println("⚠️ Failed to precompute paths: " + e.getMessage());
        }
    }

    /**
     * Print enhanced campus statistics
     */
    private void printCampusStats() {
        CampusStats stats = getCampusStats();
        System.out.println("📊 " + stats.toString());
        System.out.println("🗺️ Campus streets: " + campusStreets.size());
        System.out.println("📍 Coordinate bounds: NE(5.6540, -0.1860), SW(5.6485, -0.1920)");
    }

    // Existing methods with same signatures for compatibility
    public List<Location> getAvailableLocations() {
        return new ArrayList<>(campusGraph.getAllLocations());
    }

    public List<Location> searchLocations(String keyword) {
        return new ArrayList<>(searchEngine.findLocationsByKeyword(keyword));
    }

    public CampusStats getCampusStats() {
        return new CampusStats(
                campusGraph.getLocationCount(),
                campusGraph.getEdgeCount(),
                routeCache.size()
        );
    }

    public void clearCache() {
        routeCache.clear();
    }

    public RouteUpdate getRouteUpdate(String routeId) {
        return new RouteUpdate(routeId, trafficManager.getCurrentConditions());
    }

    public void shutdown() {
        threadPool.shutdown();
        try {
            if (!threadPool.awaitTermination(10, TimeUnit.SECONDS)) {
                threadPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            threadPool.shutdownNow();
        }
    }
}