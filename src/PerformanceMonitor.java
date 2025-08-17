/**
 * Utility class for performance monitoring
 */
import java.util.*;
class PerformanceMonitor {
    private static Map<String, Long> operationTimes = new HashMap<>();
    private static Map<String, Integer> operationCounts = new HashMap<>();

    public static void recordOperation(String operation, long timeMs) {
        operationTimes.put(operation, operationTimes.getOrDefault(operation, 0L) + timeMs);
        operationCounts.put(operation, operationCounts.getOrDefault(operation, 0) + 1);
    }

    public static double getAverageTime(String operation) {
        if (!operationCounts.containsKey(operation) || operationCounts.get(operation) == 0) {
            return 0;
        }
        return (double) operationTimes.get(operation) / operationCounts.get(operation);
    }

    public static void printStats() {
        System.out.println("=== PERFORMANCE STATISTICS ===");
        for (String operation : operationTimes.keySet()) {
            System.out.printf("%s: %.2f ms average (%d operations)\n",
                    operation, getAverageTime(operation), operationCounts.get(operation));
        }
    }

    public static void reset() {
        operationTimes.clear();
        operationCounts.clear();
    }
}