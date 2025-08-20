import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.net.URI;

/**
 * UPDATED Main Application Class with Enhanced UG Campus Data
 * Replace your existing UGNavigateApplication.java with this version
 */
public class UGNavigateApplication extends JFrame {

    private UGNavigateEngineEnhanced routingEngine; // Changed to enhanced engine
    private JTextField sourceField;
    private JTextField destinationField;
    private AutoCompleteOverlay<Location> sourceOverlay;
    private AutoCompleteOverlay<Location> destOverlay;
    private java.util.List<Location> allLocations = new java.util.ArrayList<>();
    private JComboBox<String> transportModeComboBox;
    private JComboBox<String> sortCriteriaComboBox;
    private JTextField landmarksField;
    private JTextArea resultsArea;
    private JTextArea statusArea;
    private JButton findRoutesButton;
    private JButton clearButton;
    private JButton mapButton;
    private OSMMapPanel mapPanel;
    private JProgressBar progressBar;
    private JSpinner maxRoutesSpinner;
    private JSlider maxDistanceSlider;
    private JSlider maxTimeSlider;

    // Panels/labels we theme dynamically
    private JPanel mainPanel;
    private JPanel headerPanel;
    private JPanel inputPanel;
    private JPanel resultsPanel;
    private JPanel statusPanel;
    private JPanel buttonPanel;
    private JLabel titleLabel;
    private JLabel subtitleLabel;

    // Theme support
    private enum AppTheme { LIGHT, DARK }
    private AppTheme currentTheme = AppTheme.LIGHT;

    private RouteResult lastResult;
    
    // Store current source and destination for accurate marker positioning
    private Location currentSourceLocation;
    private Location currentDestinationLocation;

    public UGNavigateApplication() {
        initializeEngine();
        initializeGUI();
        buildMenuBar();
        applyTheme(currentTheme);
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
        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(248, 249, 250));

        // Header panel with UG branding
        headerPanel = createHeaderPanel();

        // Input panel
        inputPanel = createInputPanel();

        // Results panel
        resultsPanel = createResultsPanel();

        // Status panel
        statusPanel = createStatusPanel();

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
        panel.setBackground(new Color(0, 51, 102)); // default, themed later
        panel.setBorder(new EmptyBorder(15, 20, 15, 20));

        titleLabel = new JLabel("🎓 University of Ghana Campus Navigation");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);

        subtitleLabel = new JLabel("Real Campus Data • Smart Routing • Advanced Algorithms");
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
        sourceField = new JTextField();
        sourceField.setPreferredSize(new Dimension(200, 30));
        sourceField.setToolTipText("Type a place name or ID (e.g., Balme Library or LIB001)");
        sourceOverlay = new AutoCompleteOverlay<>(
                sourceField,
                this::searchLocationsFast,
                (list, value, index, isSelected, cellHasFocus) -> new DefaultListCellRenderer().getListCellRendererComponent(list,
                        value.getName() + " (" + value.getType() + ")", index, isSelected, cellHasFocus),
                8,
                loc -> loc.getName() // keep name only; avoid pre-filling unwanted IDs
        );
        basicPanel.add(sourceField, gbc);

        // Destination location
        gbc.gridx = 2; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        basicPanel.add(new JLabel("🎯 To:"), gbc);
        gbc.gridx = 3; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        destinationField = new JTextField();
        destinationField.setPreferredSize(new Dimension(200, 30));
        destinationField.setToolTipText("Type a place name or ID (e.g., Great Hall or GH001)");
        destOverlay = new AutoCompleteOverlay<>(
                destinationField,
                this::searchLocationsFast,
                (list, value, index, isSelected, cellHasFocus) -> new DefaultListCellRenderer().getListCellRendererComponent(list,
                        value.getName() + " (" + value.getType() + ")", index, isSelected, cellHasFocus),
                8,
                loc -> loc.getName()
        );
        basicPanel.add(destinationField, gbc);

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
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
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
        demoButton.addActionListener(e -> runAlgorithmsDemo());

        JButton statsButton = new JButton("📊 Stats");
        statsButton.setPreferredSize(new Dimension(100, 40));
        statsButton.setBackground(new Color(255, 193, 7));
        statsButton.setForeground(new Color(33, 37, 41));
        statsButton.setFont(new Font("Arial", Font.BOLD, 12));
        statsButton.addActionListener(e -> showStatistics());

        mapButton = new JButton("🗺️ Map");
        mapButton.setPreferredSize(new Dimension(100, 40));
        mapButton.setBackground(new Color(52, 58, 64));
        mapButton.setForeground(Color.WHITE);
        mapButton.setFont(new Font("Arial", Font.BOLD, 12));
        mapButton.setToolTipText("Open embedded map (requires internet)");
        mapButton.addActionListener(e -> openMapView());

        buttonPanel.add(findRoutesButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(demoButton);
        buttonPanel.add(statsButton);
        buttonPanel.add(mapButton);

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
        panel.setPreferredSize(new Dimension(560, 0));

        resultsArea = new JTextArea();
        resultsArea.setEditable(false);
        resultsArea.setFont(new Font("Consolas", Font.PLAIN, 13));
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

        // Map panel below results
        mapPanel = new OSMMapPanel();
        // Configure a tile provider with proper User-Agent via OSMMapPanel and an attribution string.
        // Recommended: use a hosted tile service. Example below uses Wikimedia tiles.
        mapPanel.setTileProvider(
            "https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png",
            "© OpenStreetMap contributors",
            new String[]{"a","b","c"},
            null
        );
        mapPanel.setPreferredSize(new Dimension(560, 380));

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollPane, mapPanel);
        splitPane.setResizeWeight(0.4);
        splitPane.setDividerLocation(0.4);

        panel.add(splitPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(new TitledBorder("📊 System Status"));

        statusArea = new JTextArea(4, 0);
        statusArea.setEditable(false);
        statusArea.setFont(new Font("Arial", Font.PLAIN, 12));
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

    private void buildMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu viewMenu = new JMenu("View");
        JMenu themeMenu = new JMenu("Theme");

        JRadioButtonMenuItem lightItem = new JRadioButtonMenuItem("Light", true);
        JRadioButtonMenuItem darkItem = new JRadioButtonMenuItem("Dark");
        ButtonGroup group = new ButtonGroup();
        group.add(lightItem);
        group.add(darkItem);

        lightItem.addActionListener(e -> {
            currentTheme = AppTheme.LIGHT;
            applyTheme(currentTheme);
        });
        darkItem.addActionListener(e -> {
            currentTheme = AppTheme.DARK;
            applyTheme(currentTheme);
        });

        themeMenu.add(lightItem);
        themeMenu.add(darkItem);
        viewMenu.add(themeMenu);
        menuBar.add(viewMenu);
        setJMenuBar(menuBar);
    }

    private void applyTheme(AppTheme theme) {
        // Define palettes
        Color bg, panelBg, text, subText, headerBg, inputBg, inputFg, accent, resultsBg, statusBg;
        Color buttonText, secondaryButtonBg, secondaryButtonText;

        if (theme == AppTheme.DARK) {
            bg = Color.BLACK;
            panelBg = Color.BLACK;
            headerBg = Color.BLACK;
            text = Color.WHITE;
            subText = Color.WHITE;
            inputBg = Color.BLACK;
            inputFg = Color.WHITE;
            accent = Color.BLACK;
            resultsBg = Color.BLACK;
            statusBg = Color.BLACK;
            buttonText = Color.WHITE;
            secondaryButtonBg = Color.BLACK;
            secondaryButtonText = Color.WHITE;
        } else { // LIGHT
            bg = Color.WHITE;
            panelBg = Color.WHITE;
            headerBg = new Color(0, 51, 102); // Use a blue header for contrast
            text = Color.BLACK;
            subText = new Color(60, 60, 60); // Slightly gray for subtitle
            inputBg = Color.WHITE;
            inputFg = Color.BLACK;
            accent = Color.WHITE; // Buttons should be white
            resultsBg = Color.WHITE;
            statusBg = Color.WHITE;
            buttonText = Color.BLACK; // Button text should be black
            secondaryButtonBg = Color.WHITE; // Secondary buttons should be white
            secondaryButtonText = Color.BLACK;
        }

        // Window background
        if (mainPanel != null) mainPanel.setBackground(bg);
        if (inputPanel != null) inputPanel.setBackground(panelBg);
        if (buttonPanel != null) buttonPanel.setBackground(panelBg);
        if (headerPanel != null) headerPanel.setBackground(headerBg);
        if (resultsPanel != null) resultsPanel.setBackground(panelBg);
        if (statusPanel != null) statusPanel.setBackground(panelBg);

        // Header labels
        if (titleLabel != null) titleLabel.setForeground(Color.WHITE); // Always white on blue header
        if (subtitleLabel != null) subtitleLabel.setForeground(subText);

        // Inputs
        if (sourceField != null) { sourceField.setBackground(inputBg); sourceField.setForeground(text); sourceField.setCaretColor(text); sourceField.setSelectionColor(new Color(0,120,215,80)); }
        if (destinationField != null) { destinationField.setBackground(inputBg); destinationField.setForeground(text); destinationField.setCaretColor(text); destinationField.setSelectionColor(new Color(0,120,215,80)); }
        if (transportModeComboBox != null) { transportModeComboBox.setBackground(inputBg); transportModeComboBox.setForeground(text); }
        if (sortCriteriaComboBox != null) { sortCriteriaComboBox.setBackground(inputBg); sortCriteriaComboBox.setForeground(text); }
        if (landmarksField != null) { landmarksField.setBackground(inputBg); landmarksField.setForeground(text); landmarksField.setCaretColor(text); landmarksField.setSelectionColor(new Color(0,120,215,80)); }
        if (maxRoutesSpinner != null) { maxRoutesSpinner.getEditor().getComponent(0).setBackground(inputBg); maxRoutesSpinner.getEditor().getComponent(0).setForeground(text); }
        if (maxDistanceSlider != null) { maxDistanceSlider.setBackground(inputBg); maxDistanceSlider.setForeground(text); }
        if (maxTimeSlider != null) { maxTimeSlider.setBackground(inputBg); maxTimeSlider.setForeground(text); }

        // Buttons (including demo, stats, map)
        if (findRoutesButton != null) { findRoutesButton.setBackground(accent); findRoutesButton.setForeground(buttonText); }
        if (clearButton != null) { clearButton.setBackground(secondaryButtonBg); clearButton.setForeground(secondaryButtonText); }
        if (mapButton != null) { mapButton.setBackground(accent); mapButton.setForeground(buttonText); }
        if (buttonPanel != null) {
            for (Component c : buttonPanel.getComponents()) {
                if (c instanceof JButton) {
                    c.setBackground(accent);
                    c.setForeground(buttonText);
                }
            }
        }

        // Text areas
        if (resultsArea != null) { resultsArea.setBackground(resultsBg); resultsArea.setForeground(text); resultsArea.setCaretColor(text); resultsArea.setSelectionColor(new Color(0,120,215,80)); }
        if (statusArea != null) { statusArea.setBackground(statusBg); statusArea.setForeground(text); statusArea.setCaretColor(text); statusArea.setSelectionColor(new Color(0,120,215,80)); }

        // ScrollPane backgrounds (results and status)
        for (Component c : resultsPanel.getComponents()) {
            if (c instanceof JScrollPane) {
                c.setBackground(resultsBg);
                ((JScrollPane)c).getViewport().setBackground(resultsBg);
            }
        }
        for (Component c : statusPanel.getComponents()) {
            if (c instanceof JScrollPane) {
                c.setBackground(statusBg);
                ((JScrollPane)c).getViewport().setBackground(statusBg);
            }
        }

        // Progress bar
        if (progressBar != null) { progressBar.setBackground(new Color(233,236,239)); }

        // Recursively theme all child panels
        themePanelRecursive(mainPanel, bg, text);

        // Repaint
        SwingUtilities.updateComponentTreeUI(this);
    }

    // Helper to recursively theme all child panels and components
    private void themePanelRecursive(Container panel, Color bg, Color fg) {
        if (panel == null) return;
        panel.setBackground(bg);
        for (Component c : panel.getComponents()) {
            if (c instanceof JPanel) {
                themePanelRecursive((Container)c, bg, fg);
            } else if (c instanceof JScrollPane) {
                c.setBackground(bg);
                ((JScrollPane)c).getViewport().setBackground(bg);
            } else if (c instanceof JTextComponent) {
                c.setBackground(bg);
                c.setForeground(fg);
            } else if (c instanceof JButton) {
                c.setBackground(bg);
                c.setForeground(fg);
            } else if (c instanceof JComboBox) {
                c.setBackground(bg);
                c.setForeground(fg);
            } else if (c instanceof JSpinner) {
                ((JSpinner)c).getEditor().getComponent(0).setBackground(bg);
                ((JSpinner)c).getEditor().getComponent(0).setForeground(fg);
            } else if (c instanceof JSlider) {
                c.setBackground(bg);
                c.setForeground(fg);
            } else {
                c.setBackground(bg);
                c.setForeground(fg);
            }
        }
    }

    private void loadLocations() {
        try {
            List<Location> locations = routingEngine.getAvailableLocations();
            allLocations.clear();
            allLocations.addAll(locations);

            updateStatus("✅ Loaded " + locations.size() + " authentic UG campus locations");

            // Display campus overview in results
            displayCampusOverview();

        } catch (Exception e) {
            updateStatus("❌ Error loading locations: " + e.getMessage());
        }
    }

    private Location resolveUserInputToLocation(String input, String excludeId) {
        String term = input.trim();
        if (term.isEmpty()) return null;

        // 1) Exact ID (case-sensitive then case-insensitive)
        Location byId = routingEngine.getCampusGraph().getLocationById(term);
        if (byId != null) return byId;
        byId = routingEngine.getCampusGraph().getLocationById(term.toUpperCase());
        if (byId != null && (excludeId == null || !byId.getId().equals(excludeId))) return byId;

        // Preload all
        List<Location> all = allLocations.isEmpty() ? routingEngine.getAvailableLocations() : allLocations;
        String lower = term.toLowerCase();

        // 2) Exact name match (case-insensitive)
        for (Location l : all) {
            if (l.getName().equalsIgnoreCase(term) && (excludeId == null || !l.getId().equals(excludeId))) return l;
        }

        // 3) Starts-with name match
        Location starts = null;
        for (Location l : all) {
            if (l.getName().toLowerCase().startsWith(lower) && (excludeId == null || !l.getId().equals(excludeId))) { starts = l; break; }
        }
        if (starts != null) return starts;

        // 4) Scored fuzzy selection across name, id, keywords
        Location best = null; double bestScore = Double.NEGATIVE_INFINITY;
        for (Location l : all) {
            double score = 0;
            String name = l.getName().toLowerCase();
            if (name.contains(lower)) score += 3;
            if (name.startsWith(lower)) score += 4;
            if (l.getId().equalsIgnoreCase(term)) score += 5;
            if (l.getId().toLowerCase().contains(lower)) score += 2;
            for (String kw : l.getKeywords()) {
                String k = kw.toLowerCase();
                if (k.equals(lower)) score += 3;
                else if (k.startsWith(lower)) score += 2;
                else if (k.contains(lower)) score += 1;
            }
            // Levenshtein proximity (short strings get less penalty)
            int dist = levenshtein(lower, name);
            score += Math.max(0, 3 - (dist / 2.0));

            if ((excludeId == null || !l.getId().equals(excludeId)) && score > bestScore) { bestScore = score; best = l; }
        }
        return best;
    }

    private int levenshtein(String a, String b) {
        int[][] dp = new int[a.length()+1][b.length()+1];
        for (int i=0;i<=a.length();i++) dp[i][0]=i;
        for (int j=0;j<=b.length();j++) dp[0][j]=j;
        for (int i=1;i<=a.length();i++) {
            for (int j=1;j<=b.length();j++) {
                int cost = a.charAt(i-1)==b.charAt(j-1)?0:1;
                dp[i][j] = Math.min(Math.min(dp[i-1][j]+1, dp[i][j-1]+1), dp[i-1][j-1]+cost);
            }
        }
        return dp[a.length()][b.length()];
    }

    private List<Location> searchLocationsFast(String q) {
        String s = q.toLowerCase().trim();
        java.util.List<Location> results = new java.util.ArrayList<>();
        for (Location loc : allLocations) {
            String name = loc.getName().toLowerCase();
            if (name.contains(s) || loc.getId().toLowerCase().contains(s)) { results.add(loc); continue; }
            for (String kw : loc.getKeywords()) { if (kw.contains(s)) { results.add(loc); break; } }
        }
        return results;
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

    private void runAlgorithmsDemo() {
        SwingUtilities.invokeLater(() -> {
            JDialog dlg = new JDialog(this, "🧠 Algorithms Demo (VAM / NCM / CPM)", true);
            dlg.setSize(700, 500);
            dlg.setLocationRelativeTo(this);
            JTextArea area = new JTextArea();
            area.setEditable(false);
            area.setFont(new Font("Consolas", Font.PLAIN, 12));
            area.setText(AlgorithmsDemo.demoText());
            dlg.add(new JScrollPane(area));
            dlg.setVisible(true);
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

            String sourceText = sourceField.getText().trim();
            String destText = destinationField.getText().trim();
            if (sourceText.isEmpty() || destText.isEmpty()) {
                throw new IllegalArgumentException("Please enter both source and destination");
            }
            Location sourceLoc = resolveUserInputToLocation(sourceText, null);
            Location destLoc = resolveUserInputToLocation(destText, sourceLoc != null ? sourceLoc.getId() : null);
            if (sourceLoc == null || destLoc == null) {
                throw new IllegalArgumentException("Could not resolve one or both locations. Try a different name or ID.");
            }
            if (sourceLoc.equals(destLoc)) {
                throw new IllegalArgumentException("Source and destination cannot be the same");
            }
            
            // Store current source and destination for accurate marker positioning
            currentSourceLocation = sourceLoc;
            currentDestinationLocation = destLoc;

            RoutePreferences preferences = new RoutePreferences(
                    (String) transportModeComboBox.getSelectedItem(),
                    (String) sortCriteriaComboBox.getSelectedItem()
            );
            preferences.withMaxRoutes((Integer) maxRoutesSpinner.getValue());

            String landmarksText = landmarksField.getText().trim();
            List<Location> landmarkLocs = new ArrayList<>();
            if (!landmarksText.isEmpty()) {
                String[] landmarks = landmarksText.split(",");
                for (String lm : landmarks) {
                    List<Location> found = routingEngine.getCampusGraph().getLocationsByKeyword(lm.trim());
                    if (!found.isEmpty()) landmarkLocs.add(found.get(0)); // pick first match for each keyword
                }
            }

            // Build ordered list: source, landmarks..., destination
            List<Location> routePoints = new ArrayList<>();
            routePoints.add(sourceLoc);
            routePoints.addAll(landmarkLocs);
            routePoints.add(destLoc);

            updateStatus("🗺️ Finding routes from " + sourceLoc.getName() +
                    " ("+sourceLoc.getId()+") to " + destLoc.getName()+" ("+destLoc.getId()+")");

            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    // Fetch route details from OSRM
                    JSONObject routeDetails = OSMRouteFetcher.fetchRouteDetails(
                        sourceLoc.getLatitude(), sourceLoc.getLongitude(),
                        destLoc.getLatitude(), destLoc.getLongitude()
                    );
                    if (routeDetails != null) {
                        String geometry = routeDetails.optString("geometry", "");
                        List<Location> path = PolylineDecoder.decode(geometry);
                        mapPanel.setRoutePath(path);
                        // Debug: print first few points
                        if (path != null && !path.isEmpty()) {
                            System.out.println("Decoded route path:");
                            for (int i = 0; i < Math.min(5, path.size()); i++) {
                                System.out.println("  " + path.get(i));
                            }
                            System.out.println("Total points: " + path.size());
                        }
                        // Update map with route path and markers
                        mapPanel.setPath(path);
                        mapPanel.setMarkers(sourceLoc, destLoc);
                        // Format step-by-step directions
                        JSONArray steps = routeDetails.optJSONArray("steps");
                        StringBuilder directions = new StringBuilder();
                        directions.append("Distance: ")
                            .append(String.format("%.0f m", routeDetails.optDouble("distance", 0)))
                            .append(", Time: ")
                            .append(String.format("%.0f min", routeDetails.optDouble("duration", 0) / 60)).append("\n\n");
                        if (steps != null) {
                            for (int i = 0; i < steps.length(); i++) {
                                JSONObject step = steps.getJSONObject(i);
                                JSONObject maneuver = step.optJSONObject("maneuver");
                                String type = maneuver != null ? maneuver.optString("type", "") : "";
                                String modifier = maneuver != null ? maneuver.optString("modifier", "") : "";
                                String name = step.optString("name", "");
                                double distance = step.optDouble("distance", 0);
                                String instruction = "";
                                switch (type) {
                                    case "depart":
                                        instruction = "Start on";
                                        break;
                                    case "arrive":
                                        instruction = "Arrive at destination";
                                        break;
                                    case "turn":
                                        if (modifier.equals("left")) instruction = "Turn left";
                                        else if (modifier.equals("right")) instruction = "Turn right";
                                        else if (modifier.equals("straight")) instruction = "Go straight";
                                        else instruction = "Turn";
                                        break;
                                    case "continue":
                                        if (modifier.equals("uturn")) instruction = "Make a U-turn";
                                        else instruction = "Continue";
                                        break;
                                    case "end of road":
                                        instruction = "At the end of the road";
                                        break;
                                    default:
                                        instruction = type.isEmpty() ? "" : type.substring(0, 1).toUpperCase() + type.substring(1);
                                }
                                if (!name.isEmpty()) instruction += " onto " + name;
                                directions.append((i+1) + ". ")
                                    .append(instruction)
                                    .append(" (" + String.format("%.0f m", distance) + ")\n");
                            }
                        }
                        resultsArea.setText(directions.toString());
                    } else {
                        resultsArea.setText("No route found.");
                    }
                    return null;
                }
                @Override
                protected void done() {
                        findRoutesButton.setEnabled(true);
                        progressBar.setIndeterminate(false);
                    progressBar.setString("Ready - UG Campus Navigation System");
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
                // Steps generated from actual route edges (collapse continuous same-road segments)
                formattedResult.append("   Steps:\n");
                int step = 1;
                if (!route.getEdges().isEmpty()) {
                    String currentRoad = safeRoadName(route.getEdges().get(0).getRoadName());
                    double segDistance = 0.0;
                    Location segEnd = route.getEdges().get(0).getDestination();

                    for (int k = 0; k < route.getEdges().size(); k++) {
                        Edge e = route.getEdges().get(k);
                        String rn = safeRoadName(e.getRoadName());

                        if (!rn.equals(currentRoad)) {
                            formattedResult.append("     ").append(step++).append(". ")
                                    .append("Follow ").append(currentRoad)
                                    .append(" for ").append(formatMeters(segDistance))
                                    .append(" to ").append(segEnd.getName()).append("\n");
                            currentRoad = rn;
                            segDistance = 0.0;
                        }
                        segDistance += e.getDistance();
                        segEnd = e.getDestination();

                        // Flush at last edge
                        if (k == route.getEdges().size() - 1) {
                            formattedResult.append("     ").append(step++).append(". ")
                                    .append("Continue on ").append(currentRoad)
                                    .append(" for ").append(formatMeters(segDistance))
                                    .append(" to ").append(segEnd.getName()).append("\n");
                        }
                    }
                }
                formattedResult.append("\n");

                if (!route.getLandmarksPassedThrough().isEmpty()) {
                    formattedResult.append("����️ Features: ")
                            .append(String.join(", ", route.getLandmarksPassedThrough())).append("\n");
                }
                formattedResult.append("\n");
            }

            resultsArea.setText(formattedResult.toString());
            resultsArea.setCaretPosition(0);
            // Update embedded map with first route path
            try {
                if (!result.getRoutes().isEmpty() && mapPanel != null) {
                    Route route = result.getRoutes().get(0);
                    List<Location> path = route.getPath();
                    List<Edge> edges = route.getEdges();
                    
                    // Set both path and edges for better route visualization
                    mapPanel.setPath(path);
                    mapPanel.setRouteEdges(edges);
                    
                    // Debug output
                    System.out.println("Route visualization debug:");
                    System.out.println("  Path points: " + (path != null ? path.size() : 0));
                    System.out.println("  Route edges: " + (edges != null ? edges.size() : 0));
                    
                    // Set markers using the stored source and destination locations
                    if (currentSourceLocation != null && currentDestinationLocation != null) {
                        mapPanel.setMarkers(currentSourceLocation, currentDestinationLocation);
                        System.out.println("  Start marker: " + currentSourceLocation.getName() + " at (" + currentSourceLocation.getLatitude() + ", " + currentSourceLocation.getLongitude() + ")");
                        System.out.println("  End marker: " + currentDestinationLocation.getName() + " at (" + currentDestinationLocation.getLatitude() + ", " + currentDestinationLocation.getLongitude() + ")");
                    } else if (!path.isEmpty()) {
                        // Fallback to path points if stored locations are not available
                        mapPanel.setMarkers(path.get(0), path.get(path.size()-1));
                        System.out.println("  Start: " + path.get(0).getName());
                        System.out.println("  End: " + path.get(path.size()-1).getName());
                    }
                }
            } catch (Exception ignore) {}
        } else {
            resultsArea.setText("❌ No routes found.\n\n" + result.getMessage());
            if (mapPanel != null) {
                mapPanel.setPath(null);
                mapPanel.setRouteEdges(null);
            }
        }

        progressBar.setValue(100);
        progressBar.setString("Complete (" + result.getRoutes().size() + " routes)");
    }

    private String safeRoadName(String rn) {
        return (rn == null || rn.isBlank()) ? "path" : rn;
    }

    private String formatMeters(double meters) {
        if (meters >= 1000) return String.format("%.2f km", Double.valueOf(meters / 1000.0));
        return String.format("%.0f m", Double.valueOf(meters));
    }

    // --- Embedded Map (browser) ---
    private void openMapView() {
        try {
            // Build simple HTML with Leaflet (open-source) for best desktop Swing compatibility
            // NOTE: For Google Maps JS API, licensing requires displaying Google tiles; swap URLs accordingly.
            String html = buildLeafletHtmlFromResult(lastResult);
            Path temp = Files.createTempFile("ug-map-", ".html");
            Files.writeString(temp, html, StandardCharsets.UTF_8);
            Desktop.getDesktop().browse(temp.toUri());
            updateStatus("🌐 Opened map view: " + temp.toAbsolutePath());
        } catch (IOException ex) {
            handleError("Failed to open map view", ex);
        }
    }

    private String buildLeafletHtmlFromResult(RouteResult result) {
        // Default center: UG campus
        double centerLat = 5.6514;
        double centerLng = -0.1888;

        StringBuilder pathJs = new StringBuilder();
        if (result != null && result.hasRoutes()) {
            // Use the first route to draw a polyline
            pathJs.append("const latlngs = [\n");
            for (Location loc : result.getRoutes().get(0).getPath()) {
                pathJs.append("  [").append(loc.getLatitude()).append(", ")
                        .append(loc.getLongitude()).append("],\n");
                centerLat = loc.getLatitude();
                centerLng = loc.getLongitude();
            }
            pathJs.append("];\n");
            pathJs.append("const poly = L.polyline(latlngs, {color: 'dodgerblue', weight: 5}).addTo(map);\n");
            pathJs.append("map.fitBounds(poly.getBounds(), {padding: [40,40]});\n");
        } else {
            pathJs.append("// No route yet; centered on campus\n");
        }

        return "<!doctype html>\n" +
                "<html><head><meta charset=\"utf-8\"/>" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\"/>" +
                "<link rel=\"stylesheet\" href=\"https://unpkg.com/leaflet@1.9.4/dist/leaflet.css\"/>" +
                "<style>html,body,#map{height:100%;margin:0}</style>" +
                "</head><body><div id=\"map\"></div>" +
                "<script src=\"https://unpkg.com/leaflet@1.9.4/dist/leaflet.js\"></script>" +
                "<script>\n" +
                "const map = L.map('map', { center: [" + centerLat + ", " + centerLng + "], zoom: 16 });\n" +
                // OpenStreetMap tiles (free). For Google Maps tiles, use Maps JS API inside a WebView per licensing.
                "L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', { maxZoom: 19, attribution: '&copy; OpenStreetMap' }).addTo(map);\n" +
                pathJs +
                "</script></body></html>";
    }

    private void clearResults() {
        displayCampusOverview();
        landmarksField.setText("");
        maxRoutesSpinner.setValue(Integer.valueOf(3));
        
        // Clear map display
        if (mapPanel != null) {
            mapPanel.setPath(null);
            mapPanel.setRouteEdges(null);
            mapPanel.setMarkers(null, null);
        }
        
        // Clear stored locations
        currentSourceLocation = null;
        currentDestinationLocation = null;
        
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

