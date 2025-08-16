package com.myuser.ugcampusroutefinder.ui;

import com.myuser.ugcampusroutefinder.core.CampusGraph;
import com.myuser.ugcampusroutefinder.core.Pathfinder;
import com.myuser.ugcampusroutefinder.core.SimulatedTrafficProvider;
import com.myuser.ugcampusroutefinder.model.Location;
import com.myuser.ugcampusroutefinder.model.Path;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * A graphical user interface for the UG Campus Route Finder using Java Swing.
 */
public class GuiApp extends JFrame {

    private final CampusGraph graph;
    private final Pathfinder pathfinder;

    private JComboBox<Location> startComboBox;
    private JComboBox<Location> endComboBox;
    private JButton findRouteButton;
    private JTextArea resultArea;

    public GuiApp() {
        // 1. Initialize Core Components
        graph = new CampusGraph();
        try {
            graph.loadFromCSV("data/campus_routes.csv");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Failed to load map data: " + e.getMessage(), "Fatal Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        pathfinder = new Pathfinder(graph, new SimulatedTrafficProvider());

        // 2. Setup the Main Window (JFrame)
        setTitle("UG Campus Route Finder");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        setLocationRelativeTo(null); // Center the window

        // 3. Create UI Components
        // Input Panel
        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Route Selection"));

        Location[] locations = graph.getLocations().stream().sorted(Comparator.comparing(Location::getName)).toArray(Location[]::new);
        startComboBox = new JComboBox<>(locations);
        endComboBox = new JComboBox<>(locations);

        inputPanel.add(new JLabel("  Start Location:"));
        inputPanel.add(startComboBox);
        inputPanel.add(new JLabel("  End Location:"));
        inputPanel.add(endComboBox);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        findRouteButton = new JButton("Find Best Route");
        buttonPanel.add(findRouteButton);

        // Result Panel
        resultArea = new JTextArea("Results will be displayed here.");
        resultArea.setEditable(false);
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Result"));

        // 4. Add Components to Frame
        add(inputPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.SOUTH);

        // 5. Add Event Listener for the Button
        findRouteButton.addActionListener(e -> findRoute());
    }

    private void findRoute() {
        Location start = (Location) startComboBox.getSelectedItem();
        Location end = (Location) endComboBox.getSelectedItem();

        if (start == null || end == null) {
            resultArea.setText("Please select a start and end location.");
            return;
        }

        if (start.equals(end)) {
            resultArea.setText("Start and end locations must be different.");
            return;
        }

        resultArea.setText("Calculating route from '" + start + "' to '" + end + "'...");
        findRouteButton.setEnabled(false); // Disable button during calculation

        // Use SwingWorker to perform the calculation off the Event Dispatch Thread (EDT)
        new SwingWorker<Optional<Path>, Void>() {
            @Override
            protected Optional<Path> doInBackground() throws Exception {
                // This runs on a background thread
                return pathfinder.findShortestPath(start, end);
            }

            @Override
            protected void done() {
                // This runs on the EDT after doInBackground() is finished
                try {
                    Optional<Path> result = get(); // Get the result from doInBackground()
                    if (result.isPresent()) {
                        Path path = result.get();
                        String routeStr = path.locations().stream()
                            .map(Location::getName)
                            .collect(Collectors.joining(" -> "));
                        resultArea.setText(String.format("--- Best Route Found ---\n\nRoute: %s\n\nTotal Cost: %.2f units", routeStr, path.totalWeight()));
                    } else {
                        resultArea.setText("Sorry, no path could be found between '" + start.getName() + "' and '" + end.getName() + "'.");
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    resultArea.setText("An error occurred during calculation: " + ex.getMessage());
                    ex.printStackTrace();
                } finally {
                    findRouteButton.setEnabled(true); // Re-enable the button
                }
            }
        }.execute();
    }

    public static void main(String[] args) {
        // Ensure the UI is created and updated on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            new GuiApp().setVisible(true);
        });
    }
}
