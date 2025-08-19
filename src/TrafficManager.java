/**
 * Traffic management system for dynamic route optimization
 */
import java.util.*;
import java.time.*;
class TrafficManager {
    private Map<String, Double> currentTrafficConditions;
    private Random random;

    public TrafficManager() {
        this.currentTrafficConditions = new HashMap<>();
        this.random = new Random();
    }

    /**
     * Update traffic conditions (simulated for demo)
     */
    public void updateTrafficConditions(CampusGraph graph) {
        // Simulate traffic conditions based on time of day
        LocalDateTime now = LocalDateTime.now();
        double trafficMultiplier = calculateTrafficMultiplier(now);

        for (Location location : graph.getAllLocations()) {
            for (Edge edge : graph.getNeighbors(location)) {
                // Add some randomness to traffic conditions
                double variation = 0.8 + (random.nextDouble() * 0.4); // 0.8 to 1.2
                double finalMultiplier = trafficMultiplier * variation;
                edge.setTrafficMultiplier(finalMultiplier);

                // Record per-road traffic multiplier so RouteUpdate can surface alerts
                String roadKey = edge.getRoadName();
                if (roadKey != null && !roadKey.isEmpty()) {
                    currentTrafficConditions.put(roadKey, finalMultiplier);
                }

                // Occasionally simulate road closures (reduced chance for stability)
                if (random.nextDouble() < 0.02) {
                    edge.setClosed(true);
                } else {
                    edge.setClosed(false);
                }
            }
        }
    }

    private double calculateTrafficMultiplier(LocalDateTime time) {
        int hour = time.getHour();

        // Peak hours: 8-9 AM and 5-6 PM
        if ((hour >= 8 && hour <= 9) || (hour >= 17 && hour <= 18)) {
            return 1.5; // 50% longer travel time
        }
        // Busy hours: 7-11 AM and 2-7 PM
        else if ((hour >= 7 && hour <= 11) || (hour >= 14 && hour <= 19)) {
            return 1.2; // 20% longer travel time
        }
        // Off-peak hours
        else {
            return 1.0; // Normal travel time
        }
    }

    public Map<String, Double> getCurrentConditions() {
        return new HashMap<>(currentTrafficConditions);
    }
}