import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Main Application Class with Swing GUI
 */
public class UGNavigateApplication extends JFrame {

    private UGNavigateEngine routingEngine;
    private JComboBox<LocationItem> sourceComboBox;
    private JComboBox<LocationItem> destinationComboBox;
    private JComboBox<String> transportModeComboBox;
    private JComboBox<String> sortCriteriaComboBox;
    private JTextField landmarksField;
    private JTextArea resultsArea;
    private JTextArea statusArea;
    private JButton findRoutesButton;
    private JButton clearButton;
    private JProgressBar progressBar;
    private JSpinner maxRoutesSpinner;
    private JSlider maxDistanceSlider;
    private JSlider maxTimeSlider;

    public UGNavigateApplication() {
        initializeEngine();
        initializeGUI();
        loadLocations();
    }

    private void initializeEngine() {
        try {
            routingEngine = new UGNavigateEngine();
            updateStatus("Routing engine initialized successfully");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to initialize routing engine: " + e.getMessage(),
                    "Initialization Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    private void initializeGUI() {
        setTitle("UG Navigate - Campus Routing System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create main panels
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Input panel
        JPanel inputPanel = createInputPanel();

        // Results panel
        JPanel resultsPanel = createResultsPanel();

        // Status panel
        JPanel statusPanel = createStatusPanel();

        // Add panels to main panel
        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(resultsPanel, BorderLayout.CENTER);
        mainPanel.add(statusPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Set window properties
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(800, 600));
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new TitledBorder("Route Configuration"));

        // Basic options panel
        JPanel basicPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Source location
        gbc.gridx = 0; gbc.gridy = 0;
        basicPanel.add(new JLabel("From:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        sourceComboBox = new JComboBox<>();
        sourceComboBox.setPreferredSize(new Dimension(200, 25));
        basicPanel.add(sourceComboBox, gbc);

        // Destination location
        gbc.gridx = 2; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        basicPanel.add(new JLabel("To:"), gbc);
        gbc.gridx = 3; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        destinationComboBox = new JComboBox<>();
        destinationComboBox.setPreferredSize(new Dimension(200, 25));
        basicPanel.add(destinationComboBox, gbc);

        // Transport mode
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        basicPanel.add(new JLabel("Transport:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        transportModeComboBox = new JComboBox<>(new String[]{"walking", "driving"});
        basicPanel.add(transportModeComboBox, gbc);

        // Sort criteria
        gbc.gridx = 2;
        basicPanel.add(new JLabel("Sort by:"), gbc);
        gbc.gridx = 3;
        sortCriteriaComboBox = new JComboBox<>(new String[]{"time", "distance", "landmarks", "adaptive"});
        basicPanel.add(sortCriteriaComboBox, gbc);

        // Advanced options panel
        JPanel advancedPanel = new JPanel(new GridBagLayout());
        GridBagConstraints agbc = new GridBagConstraints();
        agbc.insets = new Insets(5, 5, 5, 5);
        agbc.anchor = GridBagConstraints.WEST;

        // Landmarks field
        agbc.gridx = 0; agbc.gridy = 0;
        advancedPanel.add(new JLabel("Landmarks:"), agbc);
        agbc.gridx = 1; agbc.weightx = 1.0; agbc.fill = GridBagConstraints.HORIZONTAL;
        landmarksField = new JTextField();
        landmarksField.setToolTipText("Enter landmarks separated by commas (e.g., bank, library, clinic)");
        advancedPanel.add(landmarksField, agbc);

        // Max routes spinner
        agbc.gridx = 2; agbc.weightx = 0; agbc.fill = GridBagConstraints.NONE;
        advancedPanel.add(new JLabel("Max routes:"), agbc);
        agbc.gridx = 3;
        maxRoutesSpinner = new JSpinner(new SpinnerNumberModel(3, 1, 10, 1));
        advancedPanel.add(maxRoutesSpinner, agbc);

        // Distance filter
        agbc.gridx = 0; agbc.gridy = 1;
        advancedPanel.add(new JLabel("Max distance (m):"), agbc);
        agbc.gridx = 1; agbc.fill = GridBagConstraints.HORIZONTAL;
        maxDistanceSlider = new JSlider(0, 5000, 5000);
        maxDistanceSlider.setMajorTickSpacing(1000);
        maxDistanceSlider.setPaintTicks(true);
        maxDistanceSlider.setPaintLabels(true);
        advancedPanel.add(maxDistanceSlider, agbc);

        // Time filter
        agbc.gridx = 2; agbc.fill = GridBagConstraints.NONE;
        advancedPanel.add(new JLabel("Max time (min):"), agbc);
        agbc.gridx = 3; agbc.fill = GridBagConstraints.HORIZONTAL;
        maxTimeSlider = new JSlider(0, 60, 60);
        maxTimeSlider.setMajorTickSpacing(15);
        maxTimeSlider.setPaintTicks(true);
        maxTimeSlider.setPaintLabels(true);
        advancedPanel.add(maxTimeSlider, agbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        findRoutesButton = new JButton("Find Routes");
        findRoutesButton.setPreferredSize(new Dimension(120, 35));
        findRoutesButton.setBackground(new Color(76, 175, 80));
        findRoutesButton.setForeground(Color.WHITE);
        findRoutesButton.setFont(new Font("Arial", Font.BOLD, 12));
        findRoutesButton.addActionListener(new FindRoutesActionListener());

        clearButton = new JButton("Clear");
        clearButton.setPreferredSize(new Dimension(80, 35));
        clearButton.addActionListener(e -> clearResults());

        JButton statsButton = new JButton("Statistics");
        statsButton.setPreferredSize(new Dimension(100, 35));
        statsButton.addActionListener(e -> showStatistics());

        buttonPanel.add(findRoutesButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(statsButton);

        // Combine panels
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        topPanel.add(basicPanel, BorderLayout.NORTH);
        topPanel.add(advancedPanel, BorderLayout.CENTER);

        panel.add(topPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createResultsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("Route Results"));

        resultsArea = new JTextArea();
        resultsArea.setEditable(false);
        resultsArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        resultsArea.setBackground(new Color(248, 248, 248));

        JScrollPane scrollPane = new JScrollPane(resultsArea);
        scrollPane.setPreferredSize(new Dimension(0, 300));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        statusArea = new JTextArea(3, 0);
        statusArea.setEditable(false);
        statusArea.setFont(new Font("Arial", Font.PLAIN, 10));
        statusArea.setBackground(new Color(240, 240, 240));

        JScrollPane statusScroll = new JScrollPane(statusArea);
        statusScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setString("Ready");

        panel.add(statusScroll, BorderLayout.CENTER);
        panel.add(progressBar, BorderLayout.SOUTH);

        return panel;
    }

    private void loadLocations() {
        try {
            List<Location> locations = routingEngine.getAvailableLocations();

            // Clear existing items
            sourceComboBox.removeAllItems();
            destinationComboBox.removeAllItems();

            // Add locations to combo boxes
            for (Location location : locations) {
                LocationItem item = new LocationItem(location);
                sourceComboBox.addItem(item);
                destinationComboBox.addItem(item);
            }

            updateStatus("Loaded " + locations.size() + " campus locations");

        } catch (Exception e) {
            updateStatus("Error loading locations: " + e.getMessage());
        }
    }

    private class FindRoutesActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            SwingUtilities.invokeLater(() -> findRoutes());
        }
    }

    private void findRoutes() {
        try {
            // Disable button and show progress
            findRoutesButton.setEnabled(false);
            progressBar.setIndeterminate(true);
            progressBar.setString("Calculating routes...");
            resultsArea.setText("Calculating optimal routes...\n");

            // Get selected locations
            LocationItem sourceItem = (LocationItem) sourceComboBox.getSelectedItem();
            LocationItem destItem = (LocationItem) destinationComboBox.getSelectedItem();

            if (sourceItem == null || destItem == null) {
                throw new IllegalArgumentException("Please select both source and destination");
            }

            if (sourceItem.location.equals(destItem.location)) {
                throw new IllegalArgumentException("Source and destination cannot be the same");
            }

            // Build preferences
            RoutePreferences preferences = new RoutePreferences(
                    (String) transportModeComboBox.getSelectedItem(),
                    (String) sortCriteriaComboBox.getSelectedItem()
            );

            preferences.withMaxRoutes((Integer) maxRoutesSpinner.getValue());

            // Parse landmarks
            String landmarksText = landmarksField.getText().trim();
            if (!landmarksText.isEmpty()) {
                String[] landmarks = landmarksText.split(",");
                for (int i = 0; i < landmarks.length; i++) {
                    landmarks[i] = landmarks[i].trim();
                }
                preferences.withLandmarks(landmarks);
            }

            // Create filter if needed
            if (maxDistanceSlider.getValue() < 5000 || maxTimeSlider.getValue() < 60) {
                LandmarkSearch.RouteFilter filter = new LandmarkSearch.RouteFilter()
                        .setMaxDistance(maxDistanceSlider.getValue())
                        .setMaxTime(maxTimeSlider.getValue());
                preferences.withFilter(filter);
            }

            updateStatus("Finding routes from " + sourceItem.location.getName() +
                    " to " + destItem.location.getName());

            // Use SwingWorker for background processing
            SwingWorker<RouteResult, Void> worker = new SwingWorker<RouteResult, Void>() {
                @Override
                protected RouteResult doInBackground() throws Exception {
                    long startTime = System.currentTimeMillis();
                    RouteResult result = routingEngine.findOptimalRoutes(
                            sourceItem.location.getId(),
                            destItem.location.getId(),
                            preferences
                    );
                    long endTime = System.currentTimeMillis();
                    PerformanceMonitor.recordOperation("route_calculation", endTime - startTime);
                    return result;
                }

                @Override
                protected void done() {
                    try {
                        RouteResult result = get();
                        displayResults(result);
                        updateStatus("Route calculation completed - found " + result.getRoutes().size() + " routes");
                    } catch (Exception ex) {
                        handleError("Error calculating routes", ex);
                    } finally {
                        findRoutesButton.setEnabled(true);
                        progressBar.setIndeterminate(false);
                        progressBar.setString("Ready");
                    }
                }
            };

            worker.execute();

        } catch (Exception ex) {
            handleError("Error setting up route calculation", ex);
            findRoutesButton.setEnabled(true);
            progressBar.setIndeterminate(false);
            progressBar.setString("Ready");
        }
    }

    private void displayResults(RouteResult result) {
        if (result.hasRoutes()) {
            resultsArea.setText(result.getFormattedResult());
            resultsArea.setCaretPosition(0); // Scroll to top
        } else {
            resultsArea.setText("No routes found.\n\n" + result.getMessage());
        }

        // Update progress
        progressBar.setValue(100);
        progressBar.setString("Complete (" + result.getRoutes().size() + " routes)");
    }

    private void clearResults() {
        resultsArea.setText("");
        landmarksField.setText("");
        maxDistanceSlider.setValue(5000);
        maxTimeSlider.setValue(60);
        maxRoutesSpinner.setValue(3);
        updateStatus("Results cleared");
        progressBar.setValue(0);
        progressBar.setString("Ready");
    }

    private void showStatistics() {
        CampusStats stats = routingEngine.getCampusStats();
        StringBuilder statsText = new StringBuilder();
        statsText.append("=== CAMPUS STATISTICS ===\n");
        statsText.append("Total Locations: ").append(stats.getTotalLocations()).append("\n");
        statsText.append("Total Connections: ").append(stats.getTotalConnections()).append("\n");
        statsText.append("Cached Routes: ").append(stats.getCachedRoutes()).append("\n\n");

        statsText.append("=== AVAILABLE LOCATIONS ===\n");
        List<Location> locations = routingEngine.getAvailableLocations();
        for (Location loc : locations) {
            statsText.append("• ").append(loc.getName()).append(" (").append(loc.getType()).append(")\n");
            if (!loc.getKeywords().isEmpty()) {
                statsText.append("  Keywords: ").append(String.join(", ", loc.getKeywords())).append("\n");
            }
        }

        JTextArea statsArea = new JTextArea(statsText.toString());
        statsArea.setEditable(false);
        statsArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));

        JScrollPane scrollPane = new JScrollPane(statsArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));

        JOptionPane.showMessageDialog(this, scrollPane, "Campus Statistics", JOptionPane.INFORMATION_MESSAGE);
    }

    private void updateStatus(String message) {
        SwingUtilities.invokeLater(() -> {
            statusArea.append("[" + java.time.LocalTime.now().format(
                    java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")) + "] " + message + "\n");
            statusArea.setCaretPosition(statusArea.getDocument().getLength());
        });
    }

    private void handleError(String message, Exception ex) {
        String errorMsg = message + ": " + ex.getMessage();
        updateStatus("ERROR: " + errorMsg);
        JOptionPane.showMessageDialog(this, errorMsg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Wrapper class for Location objects in combo boxes
     */
    private static class LocationItem {
        public final Location location;

        public LocationItem(Location location) {
            this.location = location;
        }

        @Override
        public String toString() {
            return location.getName() + " (" + location.getType() + ")";
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            LocationItem that = (LocationItem) obj;
            return location.equals(that.location);
        }

        @Override
        public int hashCode() {
            return location.hashCode();
        }
    }

    /**
     * Main method to launch the application
     */
    public static void main(String[] args) {
        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Could not set system look and feel: " + e.getMessage());
        }

        // Create and show the application
        SwingUtilities.invokeLater(() -> {
            try {
                UGNavigateApplication app = new UGNavigateApplication();
                app.setVisible(true);

                // Add shutdown hook to cleanup resources
                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    app.routingEngine.shutdown();
                    PerformanceMonitor.printStats();
                }));

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                        "Failed to start UG Navigate: " + e.getMessage(),
                        "Startup Error",
                        JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }
}
