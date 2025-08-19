/**
 * Result container for route finding operations
 */
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.io.*;
import java.time.*;

class RouteResult {
    private List<Route> routes;
    private String message;
    private long calculationTime; // deprecated: kept for backward compatibility
    private String timestamp;
    private long durationMs;
    private long startedAtMs;

    public RouteResult(List<Route> routes, String message) {
        this.routes = routes;
        this.message = message;
        this.calculationTime = System.currentTimeMillis();
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.durationMs = -1; // unknown in legacy constructor
        this.startedAtMs = -1;
    }

    public RouteResult(List<Route> routes, String message, long durationMs, long startedAtMs) {
        this.routes = routes;
        this.message = message;
        this.calculationTime = System.currentTimeMillis();
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.durationMs = durationMs;
        this.startedAtMs = startedAtMs;
    }

    public List<Route> getRoutes() { return routes; }
    public String getMessage() { return message; }
    public long getCalculationTime() { return durationMs >= 0 ? durationMs : calculationTime; }
    public String getTimestamp() { return timestamp; }
    public long getDurationMs() { return durationMs; }
    public long getStartedAtMs() { return startedAtMs; }

    public boolean hasRoutes() { return routes != null && !routes.isEmpty(); }

    public String getFormattedResult() {
        StringBuilder result = new StringBuilder();
        result.append("=== UG NAVIGATE - ROUTE RESULTS ===\n");
        result.append("Timestamp: ").append(timestamp).append("\n");
        result.append("Message: ").append(message).append("\n");
        result.append("Routes Found: ").append(routes.size()).append("\n\n");

        for (int i = 0; i < routes.size(); i++) {
            Route route = routes.get(i);
            result.append("Route ").append(i + 1).append(":\n");
            result.append("  Distance: ").append(route.getFormattedDistance()).append("\n");
            result.append("  Time: ").append(route.getFormattedTime()).append(" (").append(route.getTransportMode()).append(")\n");
            result.append("  Path: ");

            List<Location> path = route.getPath();
            for (int j = 0; j < path.size(); j++) {
                result.append(path.get(j).getName());
                if (j < path.size() - 1) result.append(" → ");
            }
            result.append("\n");

            if (!route.getLandmarksPassedThrough().isEmpty()) {
                result.append("  Landmarks: ").append(String.join(", ", route.getLandmarksPassedThrough())).append("\n");
            }
            result.append("\n");
        }

        return result.toString();
    }
}