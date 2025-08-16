package com.myuser.ugcampusroutefinder.core;

import com.myuser.ugcampusroutefinder.model.Location;
import com.myuser.ugcampusroutefinder.model.Route;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class CampusGraph {

    private final Map<String, Location> locations = new HashMap<>();
    private final Map<Location, List<Route>> adjacencyList = new HashMap<>();

    public void loadFromCSV(String resourcePath) throws IOException {
        // This logic ensures we can load the file from within a JAR.
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(resourcePath);
             InputStreamReader isr = new InputStreamReader(Objects.requireNonNull(is), StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(isr)) {

            String line;
            while ((line = reader.readLine()) != null) {
                String trimmedLine = line.trim();
                // Skip comments, empty lines, and the header row
                if (trimmedLine.isEmpty() || trimmedLine.startsWith("#") || trimmedLine.startsWith("start_location")) {
                    continue;
                }

                String[] parts = trimmedLine.split(",");
                if (parts.length < 3) continue;

                String startName = parts[0].trim();
                String endName = parts[1].trim();
                double distance = Double.parseDouble(parts[2].trim());

                Location start = locations.computeIfAbsent(startName, Location::new);
                Location end = locations.computeIfAbsent(endName, Location::new);

                adjacencyList.computeIfAbsent(start, k -> new ArrayList<>()).add(new Route(start, end, distance));
                // For an undirected graph, add the reverse route as well
                adjacencyList.computeIfAbsent(end, k -> new ArrayList<>()).add(new Route(end, start, distance));
            }
        } catch (NullPointerException e) {
            throw new IOException("Cannot find resource: " + resourcePath, e);
        }
    }

    public Collection<Location> getLocations() {
        return Collections.unmodifiableCollection(locations.values());
    }

    public Location getLocationByName(String name) {
        return locations.get(name);
    }

    public List<Route> getRoutesFrom(Location location) {
        return adjacencyList.getOrDefault(location, Collections.emptyList());
    }
}
