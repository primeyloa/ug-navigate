import java.util.*;

/*
        * Floyd-Warshall algorithm for all-pairs shortest path
 * Useful for preprocessing and caching distances between all locations
 */
class FloydWarshallPathfinder {

    public static class AllPairsResult {
        public double[][] distances;
        public Location[][] next;
        public List<Location> locations;

        public AllPairsResult(double[][] distances, Location[][] next, List<Location> locations) {
            this.distances = distances;
            this.next = next;
            this.locations = locations;
        }

        public Route getPath(Location source, Location destination, String transportMode, CampusGraph graph) {
            int srcIndex = locations.indexOf(source);
            int destIndex = locations.indexOf(destination);

            if (srcIndex == -1 || destIndex == -1 || distances[srcIndex][destIndex] == Double.POSITIVE_INFINITY) {
                return null;
            }

            List<Location> path = new ArrayList<>();
            Location current = source;
            path.add(current);

            while (!current.equals(destination)) {
                int currentIndex = locations.indexOf(current);
                current = next[currentIndex][destIndex];
                if (current == null) break;
                path.add(current);
            }

            // Convert to Route object
            Route route = new Route(transportMode);
            for (int i = 0; i < path.size(); i++) {
                route.addLocation(path.get(i));

                if (i < path.size() - 1) {
                    for (Edge edge : graph.getNeighbors(path.get(i))) {
                        if (edge.getDestination().equals(path.get(i + 1))) {
                            route.addEdge(edge);
                            break;
                        }
                    }
                }
            }

            return route;
        }
    }

    /**
     * Compute shortest paths between all pairs of locations
     * Time complexity: O(V^3), suitable for smaller graphs or preprocessing
     */
    public static AllPairsResult computeAllPairsShortestPaths(CampusGraph graph, String transportMode) {
        List<Location> locations = new ArrayList<>(graph.getAllLocations());
        int n = locations.size();

        double[][] distances = new double[n][n];
        Location[][] next = new Location[n][n];

        // Initialize distance matrix
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                distances[i][j] = (i == j) ? 0 : Double.POSITIVE_INFINITY;
            }
        }

        // Fill in direct edges
        for (int i = 0; i < n; i++) {
            Location source = locations.get(i);
            for (Edge edge : graph.getNeighbors(source)) {
                if (!edge.isClosed()) {
                    int j = locations.indexOf(edge.getDestination());
                    double weight = transportMode.equals("walking") ?
                            edge.getWalkingTime() : edge.getDrivingTime();
                    distances[i][j] = weight;
                    next[i][j] = edge.getDestination();
                }
            }
        }

        // Floyd-Warshall main algorithm
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (distances[i][k] + distances[k][j] < distances[i][j]) {
                        distances[i][j] = distances[i][k] + distances[k][j];
                        next[i][j] = next[i][k];
                    }
                }
            }
        }

        return new AllPairsResult(distances, next, locations);
    }
}