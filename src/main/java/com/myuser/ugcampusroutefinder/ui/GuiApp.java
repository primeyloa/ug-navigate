package com.myuser.ugcampusroutefinder.ui;

import com.myuser.ugcampusroutefinder.core.OpenRouteServiceApiClient;
import com.myuser.ugcampusroutefinder.model.ApiRoute;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.painter.WaypointPainter;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactoryInfo;
import org.jxmapviewer.viewer.Waypoint;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class GuiApp extends JFrame {

    private final JXMapViewer mapViewer;
    private final OpenRouteServiceApiClient apiClient;

    public GuiApp() {
        String apiKey = "eyJvcmciOiI1YjNjZTM1OTc4NTExMTAwMDFjZjYyNDgiLCJpZCI6IjNkZjg1MDlmNDMxZDQ2MjM4MjA1MDkyMTA3ZjI1OWZlIiwiaCI6Im11cm11cjY0In0=";
        this.apiClient = new OpenRouteServiceApiClient(apiKey);

        // Setup the main window
        super("UG Campus Route Finder v2.0");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create a JXMapViewer
        mapViewer = new JXMapViewer();

        // Setup the TileFactory
        TileFactoryInfo info = new OSMTileFactoryInfo();
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);
        mapViewer.setTileFactory(tileFactory);

        // Set the focus
        GeoPosition ugandaCampus = new GeoPosition(5.6506, -0.1871);
        mapViewer.setZoom(3);
        mapViewer.setAddressLocation(ugandaCampus);

        // Add input controls
        JPanel controlPanel = new JPanel();
        JTextField startLat = new JTextField("5.6533", 8);
        JTextField startLon = new JTextField("-0.1983", 8);
        JTextField endLat = new JTextField("5.6511", 8);
        JTextField endLon = new JTextField("-0.1841", 8);
        JButton findRouteButton = new JButton("Find Route");

        controlPanel.add(new JLabel("Start Lat/Lon:"));
        controlPanel.add(startLat);
        controlPanel.add(startLon);
        controlPanel.add(new JLabel("End Lat/Lon:"));
        controlPanel.add(endLat);
        controlPanel.add(endLon);
        controlPanel.add(findRouteButton);

        setLayout(new BorderLayout());
        add(mapViewer, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);

        findRouteButton.addActionListener(e -> findRoute(startLat, startLon, endLat, endLon));

        setVisible(true);
    }

    private void findRoute(JTextField startLat, JTextField startLon, JTextField endLat, JTextField endLon) {
        try {
            GeoPosition start = new GeoPosition(Double.parseDouble(startLat.getText()), Double.parseDouble(startLon.getText()));
            GeoPosition end = new GeoPosition(Double.parseDouble(endLat.getText()), Double.parseDouble(endLon.getText()));

            new SwingWorker<ApiRoute, Void>() {
                @Override
                protected ApiRoute doInBackground() throws Exception {
                    return apiClient.getRoute(start, end);
                }

                @Override
                protected void done() {
                    try {
                        ApiRoute route = get();

                        RoutePainter routePainter = new RoutePainter(route.path());

                        Set<Waypoint> waypoints = new HashSet<>();
                        waypoints.add(new DefaultWaypoint(start));
                        waypoints.add(new DefaultWaypoint(end));

                        WaypointPainter<Waypoint> waypointPainter = new WaypointPainter<>();
                        waypointPainter.setWaypoints(waypoints);

                        List<Painter<JXMapViewer>> painters = new ArrayList<>();
                        painters.add(routePainter);
                        painters.add(waypointPainter);

                        CompoundPainter<JXMapViewer> painter = new CompoundPainter<>(painters);
                        mapViewer.setOverlayPainter(painter);

                        String info = String.format("Route Found!\nDistance: %.2f km\nDuration: %.0f minutes",
                                route.distance() / 1000, route.duration() / 60);
                        JOptionPane.showMessageDialog(GuiApp.this, info, "Route Information", JOptionPane.INFORMATION_MESSAGE);

                    } catch (InterruptedException | ExecutionException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(GuiApp.this, "Error fetching route: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid latitude/longitude format.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GuiApp::new);
    }
}
