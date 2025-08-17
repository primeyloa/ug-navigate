/**
 * Campus graph representation using adjacency list
 */
import java.util.*;
class CampusGraph {
    private Map<Location, List<Edge>> adjacencyList;
    private Map<String, Location> locationIndex; // for quick lookup by ID
    private Map<String, List<Location>> keywordIndex; // for landmark-based search

    public CampusGraph() {
        this.adjacencyList = new HashMap<>();
        this.locationIndex = new HashMap<>();
        this.keywordIndex = new HashMap<>();
    }

    public void addLocation(Location location) {
        adjacencyList.putIfAbsent(location, new ArrayList<>());
        locationIndex.put(location.getId(), location);

        // Index keywords for searching
        for (String keyword : location.getKeywords()) {
            keywordIndex.computeIfAbsent(keyword.toLowerCase(), k -> new ArrayList<>())
                    .add(location);
        }
    }

    public void addEdge(Edge edge) {
        // Add bidirectional edge
        adjacencyList.get(edge.getSource()).add(edge);

        // Create reverse edge
        Edge reverseEdge = new Edge(edge.getDestination(), edge.getSource(),
                edge.getDistance(), edge.getWalkingTime(),
                edge.getDrivingTime(), edge.getRoadName());
        reverseEdge.setTrafficMultiplier(edge.getTrafficMultiplier());
        adjacencyList.get(edge.getDestination()).add(reverseEdge);
    }

    public List<Edge> getNeighbors(Location location) {
        return adjacencyList.getOrDefault(location, new ArrayList<>());
    }

    public Location getLocationById(String id) {
        return locationIndex.get(id);
    }

    public List<Location> getLocationsByKeyword(String keyword) {
        return keywordIndex.getOrDefault(keyword.toLowerCase(), new ArrayList<>());
    }

    public Set<Location> getAllLocations() {
        return adjacencyList.keySet();
    }

    public int getLocationCount() {
        return adjacencyList.size();
    }

    public int getEdgeCount() {
        return adjacencyList.values().stream().mapToInt(List::size).sum() / 2; // Divide by 2 for bidirectional
    }

    // Update traffic conditions
    public void updateTrafficConditions(Location source, Location destination, double multiplier) {
        for (Edge edge : getNeighbors(source)) {
            if (edge.getDestination().equals(destination)) {
                edge.setTrafficMultiplier(multiplier);
                break;
            }
        }
    }

    // Close/open roads
    public void setRoadClosure(Location source, Location destination, boolean closed) {
        for (Edge edge : getNeighbors(source)) {
            if (edge.getDestination().equals(destination)) {
                edge.setClosed(closed);
                break;
            }
        }
    }
}