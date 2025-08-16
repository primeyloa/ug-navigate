package com.myuser.ugcampusroutefinder.core;

import com.myuser.ugcampusroutefinder.model.Location;
import com.myuser.ugcampusroutefinder.model.Path;
import com.myuser.ugcampusroutefinder.model.Route;

import java.util.*;

/**
 * Implements Dijkstra's algorithm to find the shortest path between two locations.
 */
public class Pathfinder {

    private final CampusGraph graph;
    private final TrafficDataProvider trafficProvider;

    public Pathfinder(CampusGraph graph, TrafficDataProvider trafficProvider) {
        this.graph = graph;
        this.trafficProvider = trafficProvider;
    }

    /**
     * Finds the shortest path between two locations using Dijkstra's algorithm.
     * The path cost is calculated using route distance multiplied by a dynamic traffic weight.
     *
     * @param start The starting location.
     * @param end The destination location.
     * @return An Optional containing the Path if found, otherwise an empty Optional.
     */
    public Optional<Path> findShortestPath(Location start, Location end) {
        // A map to store the minimum distance from the start location to any other location.
        Map<Location, Double> distances = new HashMap<>();
        // A map to reconstruct the path once the destination is found.
        Map<Location, Location> predecessors = new HashMap<>();
        // A priority queue to efficiently retrieve the unvisited location with the smallest distance.
        PriorityQueue<Location> pq = new PriorityQueue<>(Comparator.comparing(distances::get));

        // Initialize all distances to infinity, except for the start location.
        for (Location location : graph.getLocations()) {
            distances.put(location, Double.MAX_VALUE);
        }
        distances.put(start, 0.0);
        pq.add(start);

        while (!pq.isEmpty()) {
            Location current = pq.poll();

            // If we've reached the end, we can stop early.
            if (current.equals(end)) {
                break;
            }

            // Explore neighbors
            for (Route route : graph.getRoutesFrom(current)) {
                Location neighbor = route.end();
                double trafficWeight = trafficProvider.getTrafficWeight(route);
                double newDist = distances.get(current) + (route.distance() * trafficWeight);

                // If we've found a shorter path to the neighbor, update it.
                if (newDist < distances.get(neighbor)) {
                    distances.put(neighbor, newDist);
                    predecessors.put(neighbor, current);
                    // Update the priority queue with the new distance.
                    pq.remove(neighbor);
                    pq.add(neighbor);
                }
            }
        }

        // If the destination was never reached, there is no path.
        if (predecessors.get(end) == null && !start.equals(end)) {
            return Optional.empty();
        }

        // Reconstruct the path by backtracking from the end location.
        List<Location> path = new ArrayList<>();
        Location step = end;
        while (step != null) {
            path.add(step);
            step = predecessors.get(step);
        }
        Collections.reverse(path);

        return Optional.of(new Path(path, distances.get(end)));
    }
}
