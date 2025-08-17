/**
 * Demonstration class showing algorithm performance comparisons
 */
import java.util.*;
class AlgorithmDemo {

    public static void runPerformanceComparison() {
        System.out.println("=== ALGORITHM PERFORMANCE COMPARISON ===");

        UGNavigateEngine engine = new UGNavigateEngine();
        List<Location> locations = engine.getAvailableLocations();

        if (locations.size() < 2) {
            System.out.println("Need at least 2 locations for comparison.");
            return;
        }

        Location source = locations.get(0);
        Location destination = locations.get(locations.size() - 1);

        System.out.printf("Testing route from %s to %s\n\n", source.getName(), destination.getName());

        // Test Dijkstra's Algorithm
        long startTime = System.currentTimeMillis();
        List<Route> dijkstraRoutes = DijkstraPathfinder.findMultiplePaths(
                engine.campusGraph, source, destination, "walking", 3);
        long dijkstraTime = System.currentTimeMillis() - startTime;

        System.out.printf("Dijkstra's Algorithm: %d routes found in %d ms\n",
                dijkstraRoutes.size(), dijkstraTime);

        // Test A* Algorithm
        startTime = System.currentTimeMillis();
        Route aStarRoute = AStarPathfinder.findOptimalPath(
                engine.campusGraph, source, destination, "walking");
        long aStarTime = System.currentTimeMillis() - startTime;

        System.out.printf("A* Algorithm: %s in %d ms\n",
                aStarRoute != null ? "1 route found" : "No route found", aStarTime);

        // Test sorting algorithms
        if (!dijkstraRoutes.isEmpty()) {
            List<Route> testRoutes = new java.util.ArrayList<>(dijkstraRoutes);

            // QuickSort test
            startTime = System.currentTimeMillis();
            RouteSorting.QuickSort.sortByTime(new java.util.ArrayList<>(testRoutes));
            long quickSortTime = System.currentTimeMillis() - startTime;

            // MergeSort test
            startTime = System.currentTimeMillis();
            RouteSorting.MergeSort.sortByTime(new java.util.ArrayList<>(testRoutes));
            long mergeSortTime = System.currentTimeMillis() - startTime;

            System.out.printf("QuickSort: %d ms\n", quickSortTime);
            System.out.printf("MergeSort: %d ms\n", mergeSortTime);
        }

        engine.shutdown();
        System.out.println("\nPerformance comparison completed.");
    }

    public static void main(String[] args) {
        runPerformanceComparison();
    }
}
