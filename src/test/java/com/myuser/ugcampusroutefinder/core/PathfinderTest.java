package com.myuser.ugcampusroutefinder.core;

import com.myuser.ugcampusroutefinder.model.Location;
import com.myuser.ugcampusroutefinder.model.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class PathfinderTest {

    private CampusGraph graph;
    private Pathfinder pathfinder;

    @BeforeEach
    void setUp() throws IOException {
        graph = new CampusGraph();
        // Use the dedicated test CSV for predictable results
        graph.loadFromCSV("data/test_campus_routes.csv");

        // Use a mock traffic provider that always returns 1.0 for predictable test calculations.
        // This is a form of dependency injection for testing.
        TrafficDataProvider mockTrafficProvider = route -> 1.0;
        pathfinder = new Pathfinder(graph, mockTrafficProvider);
    }

    @Test
    void findShortestPath_whenPathExists_returnsCorrectShortestPath() {
        Location start = graph.getLocationByName("A");
        Location end = graph.getLocationByName("C");

        Optional<Path> result = pathfinder.findShortestPath(start, end);

        assertTrue(result.isPresent(), "A path should be found from A to C.");

        Path path = result.get();
        // The shortest path from A to C is A -> B -> C (cost 10 + 20 = 30),
        // not the direct route A -> C (cost 40).
        assertEquals(30.0, path.totalWeight(), "The total weight of the shortest path should be 30.");

        assertEquals(3, path.locations().size(), "The path should have 3 locations.");
        assertEquals("A", path.locations().get(0).getName());
        assertEquals("B", path.locations().get(1).getName());
        assertEquals("C", path.locations().get(2).getName());
    }

    @Test
    void findShortestPath_whenNoPathExists_returnsEmptyOptional() {
        // Location D is in a disconnected part of the graph relative to A.
        Location start = graph.getLocationByName("A");
        Location end = graph.getLocationByName("D");

        Optional<Path> result = pathfinder.findShortestPath(start, end);

        assertFalse(result.isPresent(), "No path should be found between A and D.");
    }

    @Test
    void findShortestPath_whenStartAndEndAreTheSame_returnsZeroWeightPath() {
        Location start = graph.getLocationByName("A");

        Optional<Path> result = pathfinder.findShortestPath(start, start);

        assertTrue(result.isPresent(), "A path should be found when start and end are the same.");
        assertEquals(0.0, result.get().totalWeight(), "The path weight should be 0.");
        assertEquals(1, result.get().locations().size(), "The path should contain only one location.");
    }
}
