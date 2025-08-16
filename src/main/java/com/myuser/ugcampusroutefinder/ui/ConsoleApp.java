package com.myuser.ugcampusroutefinder.ui;

import com.myuser.ugcampusroutefinder.core.CampusGraph;
import com.myuser.ugcampusroutefinder.core.Pathfinder;
import com.myuser.ugcampusroutefinder.core.SimulatedTrafficProvider;
import com.myuser.ugcampusroutefinder.model.Location;
import com.myuser.ugcampusroutefinder.model.Path;

import java.io.IOException;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * A console-based user interface for the UG Campus Route Finder.
 */
public class ConsoleApp {

    public static void main(String[] args) {
        // 1. Initialize the core components
        CampusGraph graph = new CampusGraph();
        try {
            // The resource path is relative to the `resources` directory
            graph.loadFromCSV("data/campus_routes.csv");
        } catch (IOException e) {
            System.err.println("FATAL: Failed to load map data. Make sure 'campus_routes.csv' is in the resources/data directory.");
            e.printStackTrace();
            return;
        }

        Pathfinder pathfinder = new Pathfinder(graph, new SimulatedTrafficProvider());
        Scanner scanner = new Scanner(System.in);

        System.out.println("--- UG Campus Route Finder (Console Interface) ---");

        // 2. Main application loop
        while (true) {
            System.out.println("\nAvailable locations:");
            String availableLocations = graph.getLocations().stream()
                .map(Location::getName)
                .sorted()
                .collect(Collectors.joining(", "));
            System.out.println(availableLocations);

            System.out.print("\nEnter start location (or type 'exit' to quit): ");
            String startName = scanner.nextLine();
            if (startName.equalsIgnoreCase("exit")) {
                break;
            }

            System.out.print("Enter end location: ");
            String endName = scanner.nextLine();

            // 3. Input validation
            Location start = graph.getLocationByName(startName);
            Location end = graph.getLocationByName(endName);

            if (start == null) {
                System.err.println("Error: Start location '" + startName + "' not found.");
                continue;
            }
            if (end == null) {
                System.err.println("Error: End location '" + endName + "' not found.");
                continue;
            }
            if (start.equals(end)) {
                System.err.println("Error: Start and end locations cannot be the same.");
                continue;
            }

            // 4. Call the pathfinder and display results
            Optional<Path> result = pathfinder.findShortestPath(start, end);

            if (result.isPresent()) {
                Path path = result.get();
                System.out.println("\n--- Best Route Found ---");
                String routeStr = path.locations().stream()
                    .map(Location::getName)
                    .collect(Collectors.joining(" -> "));
                System.out.println("Route: " + routeStr);
                System.out.printf("Total weighted distance (cost): %.2f units\n", path.totalWeight());
            } else {
                System.err.println("\nSorry, no path could be found between '" + startName + "' and '" + endName + "'.");
            }
        }

        scanner.close();
        System.out.println("\nThank you for using the Route Finder!");
    }
}
