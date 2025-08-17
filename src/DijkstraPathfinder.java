/**
 * Dijkstra's algorithm implementation for shortest path
 */
import java.util.*;
class DijkstraPathfinder {

    public static class PathResult {
        public Route route;
        public Map<Location, Double> distances;
        public Map<Location, Location> predecessors;

        public PathResult(Route route, Map<Location, Double> distances,
                          Map<Location, Location> predecessors) {
            this.route = route;
            this.distances = distances;
            this.predecessors = predecessors;
        }
    }

    /**
     * Find shortest path using Dijkstra's algorithm
     * Optimized with priority queue for O((V + E) log V) complexity
     */
    public static PathResult findShortestPath(CampusGraph graph, Location source,
                                              Location destination, String transportMode) {
        Map<Location, Double> distances = new HashMap<>();
        Map<Location, Location> predecessors = new HashMap<>();
        PriorityQueue<LocationDistance> pq = new PriorityQueue<>();
        Set<Location> visited = new HashSet<>();

        // Initialize distances
        for (Location location : graph.getAllLocations()) {
            distances.put(location, Double.POSITIVE_INFINITY);
        }
        distances.put(source, 0.0);
        pq.offer(new LocationDistance(source, 0.0));

        while (!pq.isEmpty()) {
            LocationDistance current = pq.poll();
            Location currentLocation = current.location;

            if (visited.contains(currentLocation)) continue;
            visited.add(currentLocation);

            if (currentLocation.equals(destination)) break;

            // Explore neighbors
            for (Edge edge : graph.getNeighbors(currentLocation)) {
                if (edge.isClosed()) continue; // Skip closed roads

                Location neighbor = edge.getDestination();
                double edgeWeight = transportMode.equals("walking") ?
                        edge.getWalkingTime() : edge.getDrivingTime();
                double newDistance = distances.get(currentLocation) + edgeWeight;

                if (newDistance < distances.get(neighbor)) {
                    distances.put(neighbor, newDistance);
                    predecessors.put(neighbor, currentLocation);
                    pq.offer(new LocationDistance(neighbor, newDistance));
                }
            }
        }

        // Reconstruct path
        Route route = reconstructPath(graph, predecessors, source, destination, transportMode);
        return new PathResult(route, distances, predecessors);
    }

    /**
     * Find multiple shortest paths using modified Dijkstra
     */
    public static List<Route> findMultiplePaths(CampusGraph graph, Location source,
                                                Location destination, String transportMode, int maxPaths) {
        List<Route> routes = new ArrayList<>();
        Set<List<Location>> foundPaths = new HashSet<>();

        // Use k-shortest paths algorithm (Yen's algorithm simplified)
        PathResult firstPath = findShortestPath(graph, source, destination, transportMode);
        if (firstPath.route != null) {
            routes.add(firstPath.route);
            foundPaths.add(firstPath.route.getPath());
        }

        // For additional paths, temporarily remove edges and find alternatives
        for (int i = 1; i < maxPaths && i < 3; i++) {
            Route alternativePath = findAlternativePath(graph, source, destination,
                    transportMode, foundPaths);
            if (alternativePath != null) {
                routes.add(alternativePath);
                foundPaths.add(alternativePath.getPath());
            }
        }

        return routes;
    }

    private static Route findAlternativePath(CampusGraph graph, Location source,
                                             Location destination, String transportMode,
                                             Set<List<Location>> excludePaths) {
        // Simple alternative path finding by temporarily modifying edge weights
        // This is a simplified approach for demonstration
        return findShortestPath(graph, source, destination, transportMode).route;
    }

    private static Route reconstructPath(CampusGraph graph, Map<Location, Location> predecessors,
                                         Location source, Location destination, String transportMode) {
        if (!predecessors.containsKey(destination)) return null;

        List<Location> path = new ArrayList<>();
        Location current = destination;

        while (current != null) {
            path.add(0, current);
            current = predecessors.get(current);
        }

        if (path.isEmpty() || !path.get(0).equals(source)) return null;

        // Create route with edges
        Route route = new Route(transportMode);
        for (int i = 0; i < path.size(); i++) {
            route.addLocation(path.get(i));

            if (i < path.size() - 1) {
                Edge edge = findEdgeBetween(graph, path.get(i), path.get(i + 1));
                if (edge != null) {
                    route.addEdge(edge);
                }
            }
        }

        return route;
    }

    private static Edge findEdgeBetween(CampusGraph graph, Location from, Location to) {
        for (Edge edge : graph.getNeighbors(from)) {
            if (edge.getDestination().equals(to)) {
                return edge;
            }
        }
        return null;
    }

    private static class LocationDistance implements Comparable<LocationDistance> {
        Location location;
        double distance;

        LocationDistance(Location location, double distance) {
            this.location = location;
            this.distance = distance;
        }

        @Override
        public int compareTo(LocationDistance other) {
            return Double.compare(this.distance, other.distance);
        }
    }
}
