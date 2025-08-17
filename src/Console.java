/**
 * Console-based interface for command-line usage
 */
import java.util.*;
class UGNavigateConsole {

    private UGNavigateEngine engine;
    private java.util.Scanner scanner;

    public UGNavigateConsole() {
        this.engine = new UGNavigateEngine();
        this.scanner = new java.util.Scanner(System.in);
    }

    public void run() {
        System.out.println("=================================");
        System.out.println("   UG NAVIGATE - CONSOLE MODE   ");
        System.out.println("=================================");
        System.out.println("Campus Routing System for University of Ghana");
        System.out.println();

        boolean running = true;
        while (running) {
            try {
                System.out.println("\n--- MAIN MENU ---");
                System.out.println("1. Find Routes");
                System.out.println("2. Search Locations");
                System.out.println("3. View Statistics");
                System.out.println("4. Performance Stats");
                System.out.println("5. Clear Cache");
                System.out.println("0. Exit");
                System.out.print("Choose an option: ");

                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        findRoutesInteractive();
                        break;
                    case 2:
                        searchLocations();
                        break;
                    case 3:
                        showStatistics();
                        break;
                    case 4:
                        PerformanceMonitor.printStats();
                        break;
                    case 5:
                        engine.clearCache();
                        System.out.println("Cache cleared successfully.");
                        break;
                    case 0:
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid option. Please try again.");
                }

            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
                scanner.nextLine(); // Clear invalid input
            }
        }

        System.out.println("Thank you for using UG Navigate!");
        engine.shutdown();
    }

    private void findRoutesInteractive() {
        try {
            // Show available locations
            System.out.println("\n=== AVAILABLE LOCATIONS ===");
            List<Location> locations = engine.getAvailableLocations();
            for (int i = 0; i < locations.size(); i++) {
                Location loc = locations.get(i);
                System.out.printf("%d. %s (%s)\n", i + 1, loc.getName(), loc.getType());
            }

            // Get source
            System.out.print("\nSelect source location (number): ");
            int sourceIndex = scanner.nextInt() - 1;
            scanner.nextLine();

            if (sourceIndex < 0 || sourceIndex >= locations.size()) {
                System.out.println("Invalid source selection.");
                return;
            }

            // Get destination
            System.out.print("Select destination location (number): ");
            int destIndex = scanner.nextInt() - 1;
            scanner.nextLine();

            if (destIndex < 0 || destIndex >= locations.size()) {
                System.out.println("Invalid destination selection.");
                return;
            }

            if (sourceIndex == destIndex) {
                System.out.println("Source and destination cannot be the same.");
                return;
            }

            Location source = locations.get(sourceIndex);
            Location destination = locations.get(destIndex);

            // Get transport mode
            System.out.print("Transport mode (walking/driving): ");
            String transportMode = scanner.nextLine().toLowerCase();
            if (!transportMode.equals("walking") && !transportMode.equals("driving")) {
                transportMode = "walking";
                System.out.println("Invalid transport mode. Using 'walking'.");
            }

            // Get sort criteria
            System.out.print("Sort by (time/distance/landmarks/adaptive): ");
            String sortCriteria = scanner.nextLine().toLowerCase();
            if (!java.util.Arrays.asList("time", "distance", "landmarks", "adaptive").contains(sortCriteria)) {
                sortCriteria = "time";
                System.out.println("Invalid sort criteria. Using 'time'.");
            }

            // Get landmarks (optional)
            System.out.print("Landmarks to pass through (comma-separated, or press Enter to skip): ");
            String landmarksInput = scanner.nextLine().trim();

            // Create preferences
            RoutePreferences preferences = new RoutePreferences(transportMode, sortCriteria);

            if (!landmarksInput.isEmpty()) {
                String[] landmarks = landmarksInput.split(",");
                for (int i = 0; i < landmarks.length; i++) {
                    landmarks[i] = landmarks[i].trim();
                }
                preferences.withLandmarks(landmarks);
            }

            // Find routes
            System.out.println("\nCalculating routes...");
            long startTime = System.currentTimeMillis();
            RouteResult result = engine.findOptimalRoutes(source.getId(), destination.getId(), preferences);
            long endTime = System.currentTimeMillis();

            // Display results
            System.out.println(result.getFormattedResult());
            System.out.printf("Calculation time: %d ms\n", endTime - startTime);

        } catch (Exception e) {
            System.err.println("Error finding routes: " + e.getMessage());
        }
    }

    private void searchLocations() {
        System.out.print("Enter search keyword: ");
        String keyword = scanner.nextLine().trim();

        if (keyword.isEmpty()) {
            System.out.println("Please enter a valid keyword.");
            return;
        }

        List<Location> results = engine.searchLocations(keyword);

        if (results.isEmpty()) {
            System.out.println("No locations found matching '" + keyword + "'.");
        } else {
            System.out.println("\n=== SEARCH RESULTS for '" + keyword + "' ===");
            for (Location loc : results) {
                System.out.printf("• %s (%s)\n", loc.getName(), loc.getType());
                if (!loc.getKeywords().isEmpty()) {
                    System.out.printf("  Keywords: %s\n", String.join(", ", loc.getKeywords()));
                }
            }
        }
    }

    private void showStatistics() {
        CampusStats stats = engine.getCampusStats();
        System.out.println("\n" + stats.toString());

        System.out.println("\n=== LOCATION DETAILS ===");
        List<Location> locations = engine.getAvailableLocations();
        for (Location loc : locations) {
            System.out.printf("• %s (%s)\n", loc.getName(), loc.getType());
            if (!loc.getKeywords().isEmpty()) {
                System.out.printf("  Keywords: %s\n", String.join(", ", loc.getKeywords()));
            }
        }
    }

    /**
     * Console main method for command-line usage
     */
    public static void main(String[] args) {
        // Check if GUI mode is requested
        if (args.length > 0 && args[0].equals("--gui")) {
            UGNavigateApplication.main(args);
            return;
        }

        try {
            UGNavigateConsole console = new UGNavigateConsole();
            console.run();
        } catch (Exception e) {
            System.err.println("Failed to start UG Navigate Console: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
