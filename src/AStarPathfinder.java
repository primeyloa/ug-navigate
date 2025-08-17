/**
        * A* (A-Star) algorithm implementation with heuristic optimization
 */
import java.util.*;
class AStarPathfinder {

    /**
     * Find optimal path using A* algorithm with Haversine heuristic
     * More efficient than Dijkstra for single-source single-destination
     */
    public static Route findOptimalPath(CampusGraph graph, Location source,
                                        Location destination, String transportMode) {
        Map<Location, Double> gScore = new HashMap<>(); // Cost from start
        Map<Location, Double> fScore = new HashMap<>(); // gScore + heuristic
        Map<Location, Location> predecessors = new HashMap<>();
        PriorityQueue<AStarNode> openSet = new PriorityQueue<>();
        Set<Location> closedSet = new HashSet<>();

        // Initialize
        for (Location location : graph.getAllLocations()) {
            gScore.put(location, Double.POSITIVE_INFINITY);
            fScore.put(location, Double.POSITIVE_INFINITY);
        }

        gScore.put(source, 0.0);
        fScore.put(source, heuristic(source, destination, transportMode));
        openSet.offer(new AStarNode(source, fScore.get(source)));

        while (!openSet.isEmpty()) {
            AStarNode current = openSet.poll();
            Location currentLocation = current.location;

            if (currentLocation.equals(destination)) {
                return reconstructAStarPath(graph, predecessors, source, destination, transportMode);
            }

            if (closedSet.contains(currentLocation)) continue;
            closedSet.add(currentLocation);

            // Explore neighbors
            for (Edge edge : graph.getNeighbors(currentLocation)) {
                if (edge.isClosed() || closedSet.contains(edge.getDestination())) continue;

                Location neighbor = edge.getDestination();
                double edgeWeight = transportMode.equals("walking") ?
                        edge.getWalkingTime() : edge.getDrivingTime();
                double tentativeGScore = gScore.get(currentLocation) + edgeWeight;

                if (tentativeGScore < gScore.get(neighbor)) {
                    predecessors.put(neighbor, currentLocation);
                    gScore.put(neighbor, tentativeGScore);
                    fScore.put(neighbor, gScore.get(neighbor) + heuristic(neighbor, destination, transportMode));
                    openSet.offer(new AStarNode(neighbor, fScore.get(neighbor)));
                }
            }
        }

        return null; // No path found
    }

    private static double heuristic(Location current, Location destination, String transportMode) {
        double distance = DistanceCalculator.calculateHaversineDistance(current, destination);
        // Convert to time estimate based on transport mode
        if (transportMode.equals("walking")) {
            return DistanceCalculator.estimateWalkingTime(distance);
        } else {
            return DistanceCalculator.estimateDrivingTime(distance);
        }
    }

    private static Route reconstructAStarPath(CampusGraph graph, Map<Location, Location> predecessors,
                                              Location source, Location destination, String transportMode) {
        List<Location> path = new ArrayList<>();
        Location current = destination;

        while (current != null) {
            path.add(0, current);
            current = predecessors.get(current);
        }

        if (path.isEmpty() || !path.get(0).equals(source)) return null;

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

    private static class AStarNode implements Comparable<AStarNode> {
        Location location;
        double fScore;

        AStarNode(Location location, double fScore) {
            this.location = location;
            this.fScore = fScore;
        }

        @Override
        public int compareTo(AStarNode other) {
            return Double.compare(this.fScore, other.fScore);
        }
    }
}
