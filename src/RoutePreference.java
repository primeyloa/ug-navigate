/**
 * User preferences for route calculation
 */
class RoutePreferences {
    public String transportMode = "walking"; // "walking" or "driving"
    public String sortCriteria = "time"; // "time", "distance", "landmarks", "adaptive"
    public int maxRoutes = 3;
    public String[] landmarks = null;
    public LandmarkSearch.RouteFilter filter = null;

    // Weighting factors for composite scoring (Greedy optimization)
    public double timeWeight = 0.5;
    public double distanceWeight = 0.3;
    public double landmarkWeight = 0.2;

    public RoutePreferences() {}

    public RoutePreferences(String transportMode, String sortCriteria) {
        this.transportMode = transportMode;
        this.sortCriteria = sortCriteria;
    }

    public RoutePreferences withLandmarks(String... landmarks) {
        this.landmarks = landmarks;
        return this;
    }

    public RoutePreferences withFilter(LandmarkSearch.RouteFilter filter) {
        this.filter = filter;
        return this;
    }

    public RoutePreferences withMaxRoutes(int maxRoutes) {
        this.maxRoutes = maxRoutes;
        return this;
    }

    public RoutePreferences withWeights(double timeWeight, double distanceWeight, double landmarkWeight) {
        this.timeWeight = timeWeight;
        this.distanceWeight = distanceWeight;
        this.landmarkWeight = landmarkWeight;
        return this;
    }
}