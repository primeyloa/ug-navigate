/**
 * Represents a road/path between two locations
 */
class Edge {
    private Location source;
    private Location destination;
    private double distance; // in meters
    private double walkingTime; // in minutes
    private double drivingTime; // in minutes
    private boolean isAccessible; // for disabled access
    private boolean isClosed; // for road closures
    private double trafficMultiplier; // 1.0 = normal, >1.0 = heavy traffic
    private String roadName;

    public Edge(Location source, Location destination, double distance, double walkingTime, double drivingTime, String roadName) {
        this.source = source;
        this.destination = destination;
        this.distance = distance;
        this.walkingTime = walkingTime;
        this.drivingTime = drivingTime;
        this.roadName = roadName;
        this.isAccessible = true;
        this.isClosed = false;
        this.trafficMultiplier = 1.0;
    }

    // Getters and setters
    public Location getSource() { return source; }
    public Location getDestination() { return destination; }
    public double getDistance() { return distance; }
    public double getWalkingTime() { return walkingTime * trafficMultiplier; }
    public double getDrivingTime() { return drivingTime * trafficMultiplier; }
    public boolean isAccessible() { return isAccessible; }
    public boolean isClosed() { return isClosed; }
    public String getRoadName() { return roadName; }
    public double getTrafficMultiplier() { return trafficMultiplier; }

    public void setAccessible(boolean accessible) { isAccessible = accessible; }
    public void setClosed(boolean closed) { isClosed = closed; }
    public void setTrafficMultiplier(double multiplier) { trafficMultiplier = multiplier; }

    @Override
    public String toString() {
        return source.getName() + " -> " + destination.getName() +
                " (" + distance + "m, " + roadName + ")";
    }
}
