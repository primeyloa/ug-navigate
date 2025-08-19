import java.util.*;

/**
 * Simple test to verify route visualization is working
 */
public class test_route_visualization {
    public static void main(String[] args) {
        System.out.println("Testing UG Campus Navigation Route Visualization");
        System.out.println("===============================================");
        
        try {
            // Initialize the enhanced engine
            UGNavigateEngineEnhanced engine = new UGNavigateEngineEnhanced();
            
            // Get available locations
            List<Location> locations = new ArrayList<>(engine.getCampusGraph().getAllLocations());
            
            if (locations.size() < 2) {
                System.out.println("❌ Need at least 2 locations for testing");
                return;
            }
            
            // Test with first and last location
            Location source = locations.get(0);
            Location destination = locations.get(locations.size() - 1);
            
            System.out.println("📍 Source: " + source.getName());
            System.out.println("🎯 Destination: " + destination.getName());
            
            // Create route preferences
            RoutePreferences prefs = new RoutePreferences("walking", "adaptive");
            
            // Find routes
            System.out.println("\n🔍 Finding routes...");
            RouteResult result = engine.findOptimalRoutes(source.getId(), destination.getId(), prefs);
            
            if (result.hasRoutes()) {
                Route route = result.getRoutes().get(0);
                System.out.println("✅ Route found!");
                System.out.println("📏 Distance: " + route.getFormattedDistance());
                System.out.println("⏱️ Time: " + route.getFormattedTime());
                System.out.println("🛣️ Path points: " + route.getPath().size());
                System.out.println("🔗 Route edges: " + route.getEdges().size());
                
                // Test map panel
                System.out.println("\n🗺️ Testing map visualization...");
                OSMMapPanel mapPanel = new OSMMapPanel();
                
                // Set route data
                mapPanel.setPath(route.getPath());
                mapPanel.setRouteEdges(route.getEdges());
                mapPanel.setMarkers(source, destination);
                
                System.out.println("✅ Map panel configured with route data");
                System.out.println("🎨 Route should now display with blue line on map");
                
            } else {
                System.out.println("❌ No routes found: " + result.getMessage());
            }
            
        } catch (Exception e) {
            System.err.println("❌ Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
