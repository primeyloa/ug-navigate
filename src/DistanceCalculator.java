/**
 * Utility class for distance calculations and heuristics
 */
class DistanceCalculator {
    private static final double EARTH_RADIUS = 6371000; // Earth's radius in meters

    /**
     * Calculate Haversine distance between two locations (great-circle distance)
     * Used as heuristic for A* algorithm
     */
    public static double calculateHaversineDistance(Location loc1, Location loc2) {
        double lat1Rad = Math.toRadians(loc1.getLatitude());
        double lat2Rad = Math.toRadians(loc2.getLatitude());
        double deltaLat = Math.toRadians(loc2.getLatitude() - loc1.getLatitude());
        double deltaLon = Math.toRadians(loc2.getLongitude() - loc1.getLongitude());

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                        Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c;
    }

    /**
     * Estimate walking time based on distance (average walking speed: 5 km/h)
     */
    public static double estimateWalkingTime(double distanceMeters) {
        return (distanceMeters / 1000.0) * 12; // 12 minutes per km
    }

    /**
     * Estimate driving time based on distance (average campus speed: 20 km/h)
     */
    public static double estimateDrivingTime(double distanceMeters) {
        return (distanceMeters / 1000.0) * 3; // 3 minutes per km
    }
}