import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * UPDATED Main Application Class with Enhanced UG Campus Data
 * Replace your existing UGNavigateApplication.java with this version
 */
public class UGNavigateApplication extends JFrame {

    private UGNavigateEngineEnhanced routingEngine; // Changed to enhanced engine
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
            routingEngine = new UGNavigateEngineEnhanced(); // Using enhanced engine
            updateStatus("🎓 UG Campus Navigation initialized with real campus data");
            updateStatus("📍 Loaded authentic University of Ghana locations");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to initialize routing engine: " + e.getMessage(),
                    "Initialization Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    private void initializeGUI() {
        setTitle("UG Navigate - University of Ghana Campus Navigation");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create main panels with enhanced styling
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(248, 249, 250));

        // Header panel with UG branding
        JPanel headerPanel = createHeaderPanel();

        // Input panel
        JPanel inputPanel = createInputPanel();

        // Results panel
        JPanel resultsPanel = createResultsPanel();

        // Status panel
        JPanel statusPanel = createStatusPanel();

        // Add panels to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(inputPanel, BorderLayout.CENTER);
        mainPanel.add(resultsPanel, BorderLayout.EAST);
        mainPanel.add(statusPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Set window properties
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(1000, 700));
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(0, 51, 102)); // UG blue color
        panel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("🎓 University of Ghana Campus Navigation");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("Real Campus Data • Smart Routing • Advanced Algorithms");
        subtitleLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        subtitleLabel.setForeground(new Color(200, 200, 200));

        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setOpaque(false);
        textPanel.add(titleLabel, BorderLayout.NORTH);
        textPanel.add(subtitleLabel, BorderLayout.SOUTH);

        panel.add(textPanel, BorderLayout.WEST);

        return panel;
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new TitledBorder("🗺️ Route Planning"));
        panel.setBackground(Color.WHITE);

        // Basic options panel
        JPanel basicPanel = new JPanel(new GridBagLayout());
        basicPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        // Source location
        gbc.gridx = 0; gbc.gridy = 0;
        basicPanel.add(new JLabel("📍 From:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        sourceComboBox = new JComboBox<>();
        sourceComboBox.setPreferredSize(new Dimension(200, 30));
        basicPanel.add(sourceComboBox, gbc);

        // Destination location
        gbc.gridx = 2; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        basicPanel.add(new JLabel("🎯 To:"), gbc);
        gbc.gridx = 3; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        destinationComboBox = new JComboBox<>();
        destinationComboBox.setPreferredSize(new Dimension(200, 30));
        basicPanel.add(destinationComboBox, gbc);

        // Transport mode
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        basicPanel.add(new JLabel("🚶 Transport:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        transportModeComboBox = new JComboBox<>(new String[]{"walking", "driving"});
        basicPanel.add(transportModeComboBox, gbc);

        // Sort criteria
        gbc.gridx = 2;
        basicPanel.add(new JLabel("📊 Sort by:"), gbc);
        gbc.gridx = 3;
        sortCriteriaComboBox = new JComboBox<>(new String[]{"adaptive", "time", "distance", "landmarks"});
        basicPanel.add(sortCriteriaComboBox, gbc);

        // Advanced options panel
        JPanel advancedPanel = new JPanel(new GridBagLayout());
        advancedPanel.setBackground(Color.WHITE);
        GridBagConstraints agbc = new GridBagConstraints();
        agbc.insets = new Insets(5, 8, 5, 8);
        agbc.anchor = GridBagConstraints.WEST;

        // Landmarks field
        agbc.gridx = 0; agbc.gridy = 0;
        advancedPanel.add(new JLabel("🏛️ Landmarks:"), agbc);
        agbc.gridx = 1; agbc.weightx = 1.0; agbc.fill = GridBagConstraints.HORIZONTAL;
        landmarksField = new JTextField();
        landmarksField.setToolTipText("Enter landmarks (e.g., library, bank, food court, great hall)");
        advancedPanel.add(landmarksField, agbc);

        // Max routes spinner
        agbc.gridx = 2; agbc.weightx = 0; agbc.fill = GridBagConstraints.NONE;
        advancedPanel.add(new JLabel("📋 Routes:"), agbc);
        agbc.gridx = 3;
        maxRoutesSpinner = new JSpinner(new SpinnerNumberModel(3, 1, 10, 1));
        advancedPanel.add(maxRoutesSpinner, agbc);

        // Button panel with enhanced styling
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(Color.WHITE);

        findRoutesButton = new JButton("🔍 Find Routes");
        findRoutesButton.setPreferredSize(new Dimension(140, 40));
        findRoutesButton.setBackground(new Color(0, 123, 255));
        findRoutesButton.setForeground(Color.BLACK);
        findRoutesButton.setFont(new Font("Arial", Font.BOLD, 14));
        findRoutesButton.addActionListener(new FindRoutesActionListener());

        clearButton = new JButton("🗑️ Clear");
        clearButton.setPreferredSize(new Dimension(100, 40));
        clearButton.setBackground(new Color(108, 117, 125));
        clearButton.setForeground(Color.WHITE);
        clearButton.setFont(new Font("Arial", Font.BOLD, 12));
        clearButton.addActionListener(e -> clearResults());

        JButton demoButton = new JButton("🎮 Demo");
        demoButton.setPreferredSize(new Dimension(100, 40));
        demoButton.setBackground(new Color(40, 167, 69));
        demoButton.setForeground(Color.WHITE);
        demoButton.setFont(new Font("Arial", Font.BOLD, 12));
        demoButton.addActionListener(e -> runDemo());

        JButton statsButton = new JButton("📊 Stats");
        statsButton.setPreferredSize(new Dimension(100, 40));
        statsButton.setBackground(new Color(255, 193, 7));
        statsButton.setForeground(new Color(33, 37, 41));
        statsButton.setFont(new Font("Arial", Font.BOLD, 12));
        statsButton.addActionListener(e -> showStatistics());

        buttonPanel.add(findRoutesButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(demoButton);
        buttonPanel.add(statsButton);

        // Combine panels
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        topPanel.setBackground(Color.WHITE);
        topPanel.add(basicPanel, BorderLayout.NORTH);
        topPanel.add(advancedPanel, BorderLayout.CENTER);

        panel.add(topPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createResultsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("🗺️ Navigation Results"));
        panel.setPreferredSize(new Dimension(400, 0));

        resultsArea = new JTextArea();
        resultsArea.setEditable(false);
        resultsArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        resultsArea.setBackground(new Color(248, 249, 250));
        resultsArea.setText("Welcome to UG Campus Navigation!\n\n" +
                "🎓 University of Ghana Campus\n" +
                "📍 40+ Real campus locations loaded\n" +
                "🛣️ 28+ Authentic campus roads mapped\n\n" +
                "Select your starting point and destination,\n" +
                "then click 'Find Routes' to get started!\n\n" +
                "Features:\n" +
                "• Real campus data from Google Maps\n" +
                "• Smart time-aware routing\n" +
                "• Landmark-based navigation\n" +
                "• Accessibility-friendly paths\n" +
                "• Multiple route options\n");

        JScrollPane scrollPane = new JScrollPane(resultsArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(new TitledBorder("📊 System Status"));

        statusArea = new JTextArea(4, 0);
        statusArea.setEditable(false);
        statusArea.setFont(new Font("Arial", Font.PLAIN, 11));
        statusArea.setBackground(new Color(248, 249, 250));

        JScrollPane statusScroll = new JScrollPane(statusArea);
        statusScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setString("Ready - UG Campus Navigation System");
        progressBar.setBackground(new Color(233, 236, 239));

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

            updateStatus("✅ Loaded " + locations.size() + " authentic UG campus locations");

            // Display campus overview in results
            displayCampusOverview();

        } catch (Exception e) {
            updateStatus("❌ Error loading locations: " + e.getMessage());
        }
    }

    private void displayCampusOverview() {
        StringBuilder overview = new StringBuilder();
        overview.append("🎓 UNIVERSITY OF GHANA CAMPUS\n");
        overview.append("=" .repeat(35)).append("\n\n");

        CampusStats stats = routingEngine.getCampusStats();
        overview.append("📊 Campus Statistics:\n");
        overview.append("• Total locations: ").append(stats.getTotalLocations()).append("\n");
        overview.append("• Road connections: ").append(stats.getTotalConnections()).append("\n");
        overview.append("• Cached routes: ").append(stats.getCachedRoutes()).append("\n\n");

        // Count locations by type
        List<Location> locations = routingEngine.getAvailableLocations();
        long academic = locations.stream().filter(l -> l.getType().equals("academic")).count();
        long residential = locations.stream().filter(l -> l.getType().equals("residential")).count();
        long service = locations.stream().filter(l -> l.getType().equals("service")).count();
        long recreational = locations.stream().filter(l -> l.getType().equals("recreational")).count();

        overview.append("📚 Academic buildings: ").append(academic).append("\n");
        overview.append("🏠 Residential halls: ").append(residential).append("\n");
        overview.append("🏥 Service facilities: ").append(service).append("\n");
        overview.append("⚽ Recreational areas: ").append(recreational).append("\n\n");

        overview.append("🗺️ Popular landmarks include:\n");
        overview.append("• Balme Library (study hub)\n");
        overview.append("• Great Hall (ceremonies)\n");
        overview.append("• Commonwealth Hall (Vandals)\n");
        overview.append("• University Square (central)\n");
        overview.append("• Sports Complex (athletics)\n");
        overview.append("• GCB Bank (financial services)\n\n");

        overview.append("Ready to navigate! Select locations above.\n");

        resultsArea.setText(overview.toString());
    }

    private void runDemo() {
        SwingUtilities.invokeLater(() -> {
            JDialog demoDialog = new JDialog(this, "🎮 UG Campus Demo", true);
            demoDialog.setSize(600, 400);
            demoDialog.setLocationRelativeTo(this);

            JTextArea demoArea = new JTextArea();
            demoArea.setEditable(false);
            demoArea.setFont(new Font("Consolas", Font.PLAIN, 11));

            // Run quick demo scenarios
            StringBuilder demoText = new StringBuilder();
            demoText.append("🎓 UG CAMPUS NAVIGATION DEMO\n");
            demoText.append("=" .repeat(40)).append("\n\n");

            // Demo scenario 1: Commonwealth Hall to Balme Library
            demoText.append("📚 SCENARIO 1: Morning Study Session\n");
            demoText.append("Commonwealth Hall → Balme Library\n");
            RouteResult result1 = routingEngine.findOptimalRoutes("HALL001", "LIB001",
                    new RoutePreferences("walking", "time"));
            if (result1.hasRoutes()) {
                Route route = result1.getRoutes().get(0);
                demoText.append("✅ Best route: ").append(route.getFormattedDistance())
                        .append(" in ").append(route.getFormattedTime()).append("\n");
            }

            demoText.append("\n🏦 SCENARIO 2: Banking Visit\n");
            demoText.append("University Square → GCB Bank\n");
            RouteResult result2 = routingEngine.findOptimalRoutes("SQUARE001", "BANK001",
                    new RoutePreferences("walking", "time"));
            if (result2.hasRoutes()) {
                Route route = result2.getRoutes().get(0);
                demoText.append("✅ Best route: ").append(route.getFormattedDistance())
                        .append(" in ").append(route.getFormattedTime()).append("\n");
            }

            demoText.append("\n🍽️ SCENARIO 3: Lunch Break\n");
            demoText.append("CBAS Building → Food Court\n");
            RouteResult result3 = routingEngine.findOptimalRoutes("CBAS001", "FOOD001",
                    new RoutePreferences("walking", "distance"));
            if (result3.hasRoutes()) {
                Route route = result3.getRoutes().get(0);
                demoText.append("✅ Best route: ").append(route.getFormattedDistance())
                        .append(" in ").append(route.getFormattedTime()).append("\n");
            }

            demoText.append("\n🏥 SCENARIO 4: Medical Visit\n");
            demoText.append("Volta Hall → University Hospital\n");
            RouteResult result4 = routingEngine.findOptimalRoutes("HALL005", "HOSP001",
                    new RoutePreferences("walking", "time"));
            if (result4.hasRoutes()) {
                Route route = result4.getRoutes().get(0);
                demoText.append("✅ Best route: ").append(route.getFormattedDistance())
                        .append(" in ").append(route.getFormattedTime()).append("\n");
            }

            demoText.append("\n🎉 Demo completed! Try your own routes above.");

            demoArea.setText(demoText.toString());
            demoDialog.add(new JScrollPane(demoArea));
            demoDialog.setVisible(true);
        });
    }

    // Rest of the existing methods remain the same...
    private class FindRoutesActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            SwingUtilities.invokeLater(() -> findRoutes());
        }
    }

    private void findRoutes() {
        try {
            findRoutesButton.setEnabled(false);
            progressBar.setIndeterminate(true);
            progressBar.setString("🔍 Calculating optimal routes...");
            resultsArea.setText("🔍 Calculating optimal campus routes...\n\n");

            LocationItem sourceItem = (LocationItem) sourceComboBox.getSelectedItem();
            LocationItem destItem = (LocationItem) destinationComboBox.getSelectedItem();

            if (sourceItem == null || destItem == null) {
                throw new IllegalArgumentException("Please select both source and destination");
            }

            if (sourceItem.location.equals(destItem.location)) {
                throw new IllegalArgumentException("Source and destination cannot be the same");
            }

            RoutePreferences preferences = new RoutePreferences(
                    (String) transportModeComboBox.getSelectedItem(),
                    (String) sortCriteriaComboBox.getSelectedItem()
            );

            preferences.withMaxRoutes((Integer) maxRoutesSpinner.getValue());

            String landmarksText = landmarksField.getText().trim();
            if (!landmarksText.isEmpty()) {
                String[] landmarks = landmarksText.split(",");
                for (int i = 0; i < landmarks.length; i++) {
                    landmarks[i] = landmarks[i].trim();
                }
                preferences.withLandmarks(landmarks);
            }

            updateStatus("🗺️ Finding routes from " + sourceItem.location.getName() +
                    " to " + destItem.location.getName());

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
                        updateStatus("✅ Route calculation completed - found " +
                                result.getRoutes().size() + " routes");
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
            StringBuilder formattedResult = new StringBuilder();
            formattedResult.append("🎓 UG CAMPUS NAVIGATION RESULTS\n");
            formattedResult.append("=" .repeat(40)).append("\n");
            formattedResult.append("📅 ").append(result.getTimestamp()).append("\n");
            formattedResult.append("📊 ").append(result.getMessage()).append("\n");
            formattedResult.append("🗺️ Routes Found: ").append(result.getRoutes().size()).append("\n\n");

            for (int i = 0; i < result.getRoutes().size(); i++) {
                Route route = result.getRoutes().get(i);
                formattedResult.append("🛣️ Route ").append(i + 1).append(":\n");
                formattedResult.append("📏 Distance: ").append(route.getFormattedDistance()).append("\n");
                formattedResult.append("⏱️ Time: ").append(route.getFormattedTime())
                        .append(" (").append(route.getTransportMode()).append(")\n");
                formattedResult.append("🗺️ Path: ");

                List<Location> path = route.getPath();
                for (int j = 0; j < path.size(); j++) {
                    formattedResult.append(path.get(j).getName());
                    if (j < path.size() - 1) formattedResult.append(" → ");
                }
                formattedResult.append("\n");

                if (!route.getLandmarksPassedThrough().isEmpty()) {
                    formattedResult.append("🏛️ Features: ")
                            .append(String.join(", ", route.getLandmarksPassedThrough())).append("\n");
                }
                formattedResult.append("\n");
            }

            resultsArea.setText(formattedResult.toString());
            resultsArea.setCaretPosition(0);
        } else {
            resultsArea.setText("❌ No routes found.\n\n" + result.getMessage());
        }

        progressBar.setValue(100);
        progressBar.setString("Complete (" + result.getRoutes().size() + " routes)");
    }

    private void clearResults() {
        displayCampusOverview();
        landmarksField.setText("");
        maxRoutesSpinner.setValue(3);
        updateStatus("🗑️ Results cleared - ready for new navigation");
        progressBar.setValue(0);
        progressBar.setString("Ready");
    }

    private void showStatistics() {
        CampusStats stats = routingEngine.getCampusStats();
        StringBuilder statsText = new StringBuilder();

        statsText.append("🎓 UNIVERSITY OF GHANA CAMPUS STATISTICS\n");
        statsText.append("=" .repeat(50)).append("\n");
        statsText.append("📍 Total Locations: ").append(stats.getTotalLocations()).append("\n");
        statsText.append("🛣️ Total Connections: ").append(stats.getTotalConnections()).append("\n");
        statsText.append("💾 Cached Routes: ").append(stats.getCachedRoutes()).append("\n\n");

        statsText.append("📚 CAMPUS LOCATIONS BY CATEGORY\n");
        statsText.append("-".repeat(35)).append("\n");

        List<Location> locations = routingEngine.getAvailableLocations();

        statsText.append("\n🏛️ ACADEMIC BUILDINGS:\n");
        locations.stream()
                .filter(loc -> loc.getType().equals("academic"))
                .forEach(loc -> statsText.append("  • ").append(loc.getName()).append("\n"));

        statsText.append("\n🏠 RESIDENTIAL HALLS:\n");
        locations.stream()
                .filter(loc -> loc.getType().equals("residential"))
                .forEach(loc -> statsText.append("  • ").append(loc.getName()).append("\n"));

        statsText.append("\n🏥 SERVICE FACILITIES:\n");
        locations.stream()
                .filter(loc -> loc.getType().equals("service"))
                .forEach(loc -> statsText.append("  • ").append(loc.getName()).append("\n"));

        JTextArea statsArea = new JTextArea(statsText.toString());
        statsArea.setEditable(false);
        statsArea.setFont(new Font("Consolas", Font.PLAIN, 11));

        JScrollPane scrollPane = new JScrollPane(statsArea);
        scrollPane.setPreferredSize(new Dimension(600, 500));

        JOptionPane.showMessageDialog(this, scrollPane,
                "🎓 UG Campus Statistics",
                JOptionPane.INFORMATION_MESSAGE);
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
        updateStatus("❌ ERROR: " + errorMsg);
        JOptionPane.showMessageDialog(this, errorMsg, "Error", JOptionPane.ERROR_MESSAGE);
    }

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

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Could not set system look and feel: " + e.getMessage());
        }

        SwingUtilities.invokeLater(() -> {
            try {
                UGNavigateApplication app = new UGNavigateApplication();
                app.setVisible(true);

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