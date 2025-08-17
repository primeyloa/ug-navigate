/**
 * Unit testing class for validation
 */
import java.util.*;
class UGNavigateTests {

    public static void runAllTests() {
        System.out.println("=== RUNNING UNIT TESTS ===");

        testDistanceCalculator();
        testCampusGraph();
        testRoutingEngine();
        testSearchFunctionality();
        testSortingAlgorithms();

        System.out.println("All tests completed.");
    }

    private static void testDistanceCalculator() {
        System.out.println("Testing DistanceCalculator...");

        Location loc1 = new Location("TEST1", "Test Location 1", 5.6509, -0.1892, "test");
        Location loc2 = new Location("TEST2", "Test Location 2", 5.6518, -0.1885, "test");

        double distance = DistanceCalculator.calculateHaversineDistance(loc1, loc2);
        assert distance > 0 : "Distance should be positive";

        double walkTime = DistanceCalculator.estimateWalkingTime(1000);
        assert walkTime > 0 : "Walking time should be positive";

        System.out.println("✓ DistanceCalculator tests passed");
    }

    private static void testCampusGraph() {
        System.out.println("Testing CampusGraph...");

        CampusGraph graph = new CampusGraph();
        Location loc1 = new Location("TEST1", "Test 1", 0, 0, "test");
        Location loc2 = new Location("TEST2", "Test 2", 0, 0, "test");

        graph.addLocation(loc1);
        graph.addLocation(loc2);

        assert graph.getLocationCount() == 2 : "Should have 2 locations";

        Edge edge = new Edge(loc1, loc2, 100, 1.5, 0.8, "Test Road");
        graph.addEdge(edge);

        assert !graph.getNeighbors(loc1).isEmpty() : "Should have neighbors";

        System.out.println("✓ CampusGraph tests passed");
    }

    private static void testRoutingEngine() {
        System.out.println("Testing UGNavigateEngine...");

        UGNavigateEngine engine = new UGNavigateEngine();
        List<Location> locations = engine.getAvailableLocations();

        assert !locations.isEmpty() : "Should have locations";

        CampusStats stats = engine.getCampusStats();
        assert stats.getTotalLocations() > 0 : "Should have location count";

        engine.shutdown();
        System.out.println("✓ UGNavigateEngine tests passed");
    }

    private static void testSearchFunctionality() {
        System.out.println("Testing search functionality...");

        UGNavigateEngine engine = new UGNavigateEngine();
        List<Location> results = engine.searchLocations("library");

        // Should find at least the main library
        assert !results.isEmpty() : "Should find library locations";

        engine.shutdown();
        System.out.println("✓ Search functionality tests passed");
    }

    private static void testSortingAlgorithms() {
        System.out.println("Testing sorting algorithms...");

        // Create test routes
        List<Route> routes = new java.util.ArrayList<>();
        Route route1 = new Route("walking");
        Route route2 = new Route("walking");
        Route route3 = new Route("walking");

        // Add mock data (this is simplified for testing)
        routes.add(route1);
        routes.add(route2);
        routes.add(route3);

        // Test that sorting doesn't crash
        RouteSorting.QuickSort.sortByDistance(new java.util.ArrayList<>(routes));
        RouteSorting.MergeSort.sortByTime(new java.util.ArrayList<>(routes));

        System.out.println("✓ Sorting algorithms tests passed");
    }

    public static void main(String[] args) {
        try {
            runAllTests();
        } catch (AssertionError e) {
            System.err.println("Test failed: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Test error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}