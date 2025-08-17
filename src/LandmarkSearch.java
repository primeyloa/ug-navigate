/**
 * Advanced search algorithms for landmark-based routing
 */
import java.util.*;
import java.util.stream.Collectors;

class LandmarkSearch {

    /**
     * Binary search for finding routes with specific characteristics
     * Requires pre-sorted routes
     */
    public static class BinarySearch {

        public static List<Route> findRoutesByMaxDistance(List<Route> sortedRoutes, double maxDistance) {
            int index = binarySearchDistance(sortedRoutes, maxDistance);
            return sortedRoutes.subList(0, index + 1);
        }

        public static List<Route> findRoutesByMaxTime(List<Route> sortedRoutes, double maxTime) {
            int index = binarySearchTime(sortedRoutes, maxTime);
            return sortedRoutes.subList(0, index + 1);
        }

        private static int binarySearchDistance(List<Route> routes, double target) {
            int left = 0, right = routes.size() - 1, result = -1;

            while (left <= right) {
                int mid = left + (right - left) / 2;

                if (routes.get(mid).getTotalDistance() <= target) {
                    result = mid;
                    left = mid + 1;
                } else {
                    right = mid - 1;
                }
            }

            return result;
        }

        private static int binarySearchTime(List<Route> routes, double target) {
            int left = 0, right = routes.size() - 1, result = -1;

            while (left <= right) {
                int mid = left + (right - left) / 2;

                if (routes.get(mid).getTotalTime() <= target) {
                    result = mid;
                    left = mid + 1;
                } else {
                    right = mid - 1;
                }
            }

            return result;
        }
    }

    /**
     * Advanced landmark-based search with fuzzy matching
     */
    public static class LandmarkSearchEngine {

        private CampusGraph graph;
        private Map<String, Set<Location>> landmarkCache;

        public LandmarkSearchEngine(CampusGraph graph) {
            this.graph = graph;
            this.landmarkCache = new HashMap<>();
            buildLandmarkIndex();
        }

        private void buildLandmarkIndex() {
            for (Location location : graph.getAllLocations()) {
                // Index by exact keywords
                for (String keyword : location.getKeywords()) {
                    landmarkCache.computeIfAbsent(keyword.toLowerCase(), k -> new HashSet<>())
                            .add(location);
                }

                // Index by location type
                landmarkCache.computeIfAbsent(location.getType().toLowerCase(), k -> new HashSet<>())
                        .add(location);

                // Index by partial name matches
                String[] nameWords = location.getName().toLowerCase().split("\\s+");
                for (String word : nameWords) {
                    if (word.length() >= 3) { // Avoid indexing very short words
                        landmarkCache.computeIfAbsent(word, k -> new HashSet<>())
                                .add(location);
                    }
                }
            }
        }

        /**
         * Find routes that pass through or near specified landmarks
         */
        public List<Route> findRoutesWithLandmarks(Location source, Location destination,
                                                   String transportMode, String... landmarks) {
            List<Route> candidateRoutes = new ArrayList<>();

            // Get all locations matching any landmark
            Set<Location> landmarkLocations = new HashSet<>();
            for (String landmark : landmarks) {
                landmarkLocations.addAll(findLocationsByKeyword(landmark));
            }

            if (landmarkLocations.isEmpty()) {
                // No landmarks found, return direct routes
                return DijkstraPathfinder.findMultiplePaths(graph, source, destination, transportMode, 3);
            }

            // Find routes through each landmark location
            for (Location landmarkLoc : landmarkLocations) {
                Route toLandmark = AStarPathfinder.findOptimalPath(graph, source, landmarkLoc, transportMode);
                Route fromLandmark = AStarPathfinder.findOptimalPath(graph, landmarkLoc, destination, transportMode);

                if (toLandmark != null && fromLandmark != null) {
                    Route combinedRoute = combineRoutes(toLandmark, fromLandmark, transportMode);
                    if (combinedRoute != null) {
                        // Add landmark information
                        for (String landmark : landmarks) {
                            if (landmarkLoc.getKeywords().contains(landmark.toLowerCase()) ||
                                    landmarkLoc.getName().toLowerCase().contains(landmark.toLowerCase()) ||
                                    landmarkLoc.getType().toLowerCase().contains(landmark.toLowerCase())) {
                                combinedRoute.addLandmark(landmark);
                            }
                        }
                        candidateRoutes.add(combinedRoute);
                    }
                }
            }

            // Remove duplicates and sort by total time
            candidateRoutes = removeDuplicateRoutes(candidateRoutes);
            RouteSorting.MergeSort.sortByTime(candidateRoutes);

            // Return top 3 routes
            return candidateRoutes.stream().limit(3).collect(Collectors.toList());
        }

        /**
         * Fuzzy search for landmarks with partial matching
         */
        public Set<Location> findLocationsByKeyword(String keyword) {
            Set<Location> results = new HashSet<>();
            String searchTerm = keyword.toLowerCase().trim();

            // Exact match
            if (landmarkCache.containsKey(searchTerm)) {
                results.addAll(landmarkCache.get(searchTerm));
            }

            // Partial matches
            for (String indexed : landmarkCache.keySet()) {
                if (indexed.contains(searchTerm) || searchTerm.contains(indexed)) {
                    results.addAll(landmarkCache.get(indexed));
                }
            }

            // Levenshtein distance for fuzzy matching
            for (String indexed : landmarkCache.keySet()) {
                if (levenshteinDistance(searchTerm, indexed) <= 2 && indexed.length() >= 4) {
                    results.addAll(landmarkCache.get(indexed));
                }
            }

            return results;
        }

        /**
         * Advanced route filtering with multiple criteria
         */
        public List<Route> filterRoutes(List<Route> routes, RouteFilter filter) {
            return routes.stream()
                    .filter(route -> applyFilter(route, filter))
                    .collect(Collectors.toList());
        }

        private boolean applyFilter(Route route, RouteFilter filter) {
            if (filter.maxDistance > 0 && route.getTotalDistance() > filter.maxDistance) return false;
            if (filter.maxTime > 0 && route.getTotalTime() > filter.maxTime) return false;
            if (filter.minLandmarks > 0 && route.getLandmarksPassedThrough().size() < filter.minLandmarks) return false;
            if (filter.requiredLandmarks != null) {
                for (String required : filter.requiredLandmarks) {
                    if (!route.getLandmarksPassedThrough().contains(required)) return false;
                }
            }
            if (filter.avoidLandmarks != null) {
                for (String avoid : filter.avoidLandmarks) {
                    if (route.getLandmarksPassedThrough().contains(avoid)) return false;
                }
            }
            return true;
        }

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

        private List<Route> removeDuplicateRoutes(List<Route> routes) {
            Set<String> seenPaths = new HashSet<>();
            List<Route> uniqueRoutes = new ArrayList<>();

            for (Route route : routes) {
                String pathSignature = route.getPath().stream()
                        .map(Location::getId)
                        .collect(Collectors.joining("->"));
                if (!seenPaths.contains(pathSignature)) {
                    seenPaths.add(pathSignature);
                    uniqueRoutes.add(route);
                }
            }

            return uniqueRoutes;
        }

        private int levenshteinDistance(String s1, String s2) {
            int[][] dp = new int[s1.length() + 1][s2.length() + 1];

            for (int i = 0; i <= s1.length(); i++) dp[i][0] = i;
            for (int j = 0; j <= s2.length(); j++) dp[0][j] = j;

            for (int i = 1; i <= s1.length(); i++) {
                for (int j = 1; j <= s2.length(); j++) {
                    if (s1.charAt(i-1) == s2.charAt(j-1)) {
                        dp[i][j] = dp[i-1][j-1];
                    } else {
                        dp[i][j] = 1 + Math.min(Math.min(dp[i-1][j], dp[i][j-1]), dp[i-1][j-1]);
                    }
                }
            }

            return dp[s1.length()][s2.length()];
        }
    }

    /**
     * Route filtering criteria
     */
    public static class RouteFilter {
        public double maxDistance = 0;
        public double maxTime = 0;
        public int minLandmarks = 0;
        public List<String> requiredLandmarks = null;
        public List<String> avoidLandmarks = null;

        public RouteFilter setMaxDistance(double maxDistance) {
            this.maxDistance = maxDistance;
            return this;
        }

        public RouteFilter setMaxTime(double maxTime) {
            this.maxTime = maxTime;
            return this;
        }

        public RouteFilter setMinLandmarks(int minLandmarks) {
            this.minLandmarks = minLandmarks;
            return this;
        }

        public RouteFilter requireLandmarks(String... landmarks) {
            this.requiredLandmarks = Arrays.asList(landmarks);
            return this;
        }

        public RouteFilter avoidLandmarks(String... landmarks) {
            this.avoidLandmarks = Arrays.asList(landmarks);
            return this;
        }
    }
}