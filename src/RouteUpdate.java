/**
 * Real-time route updates
 */
import java.util.*;
import java.time.format.*;
import java.time.*;
class RouteUpdate {
    private String routeId;
    private Map<String, Double> trafficConditions;
    private List<String> alerts;
    private String updateTime;

    public RouteUpdate(String routeId, Map<String, Double> trafficConditions) {
        this.routeId = routeId;
        this.trafficConditions = trafficConditions;
        this.alerts = new ArrayList<>();
        this.updateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));

        // Generate alerts based on conditions
        generateAlerts();
    }

    private void generateAlerts() {
        for (Map.Entry<String, Double> entry : trafficConditions.entrySet()) {
            if (entry.getValue() > 1.3) {
                alerts.add("Heavy traffic on " + entry.getKey());
            }
        }
    }

    public String getRouteId() { return routeId; }
    public Map<String, Double> getTrafficConditions() { return trafficConditions; }
    public List<String> getAlerts() { return alerts; }
    public String getUpdateTime() { return updateTime; }
}
