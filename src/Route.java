import java.util.*;


/**
 * Represents a complete route from source to destination
 */
class Route {
    private List<Location> path;
    private List<Edge> edges;
    private double totalDistance;
    private double totalWalkingTime;
    private double totalDrivingTime;
    private String transportMode; // "walking" or "driving"
    private List<String> landmarksPassedThrough;


    private String routeDescription;

    public Route(String transportMode) {
        this.transportMode = transportMode;
        this.path = new ArrayList<>();
        this.edges = new ArrayList<>();
        this.landmarksPassedThrough = new ArrayList<>();
        this.totalDistance = 0.0;
        this.totalWalkingTime = 0.0;
        this.totalDrivingTime = 0.0;
    }

    public void addLocation(Location location) {
        path.add(location);
    }

    public void addEdge(Edge edge) {
        edges.add(edge);
        totalDistance += edge.getDistance();
        totalWalkingTime += edge.getWalkingTime();
        totalDrivingTime += edge.getDrivingTime();
    }

    public void addLandmark(String landmark) {
        if (!landmarksPassedThrough.contains(landmark)) {
            landmarksPassedThrough.add(landmark);
        }
    }

    // Getters
    public List<Location> getPath() { return path; }
    public List<Edge> getEdges() { return edges; }
    public double getTotalDistance() { return totalDistance; }
    public double getTotalWalkingTime() { return totalWalkingTime; }
    public double getTotalDrivingTime() { return totalDrivingTime; }
    public String getTransportMode() { return transportMode; }
    public List<String> getLandmarksPassedThrough() { return landmarksPassedThrough; }

    public double getTotalTime() {
        return transportMode.equals("walking") ? totalWalkingTime : totalDrivingTime;
    }

    public String getFormattedDistance() {
        if (totalDistance >= 1000) {
            return String.format("%.2f km", totalDistance / 1000);
        }
        return String.format("%.0f m", totalDistance);
    }

    public String getFormattedTime() {
        double time = getTotalTime();
        int hours = (int) (time / 60);
        int minutes = (int) (time % 60);

        if (hours > 0) {
            return String.format("%d hr %d min", hours, minutes);
        }
        return String.format("%d min", minutes);
    }

    @Override
    public String toString() {
        return String.format("Route (%s): %s, %s via %s",
                transportMode, getFormattedDistance(),
                getFormattedTime(), String.join(" -> ",
                        path.stream().map(Location::getName).toArray(String[]::new)));
    }
}
