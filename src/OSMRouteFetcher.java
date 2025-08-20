import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;

/**
 * Utility class to fetch walking route distance and duration from OSRM API
 */
public class OSMRouteFetcher {
    private static final Map<String, double[]> cache = new HashMap<>();
    private static final String CACHE_FILE = "osrm_cache.json";
    static {
        loadCacheFromDisk();
    }

    private static void loadCacheFromDisk() {
        File file = new File(CACHE_FILE);
        if (!file.exists()) return;
        try (FileReader reader = new FileReader(file)) {
            StringBuilder sb = new StringBuilder();
            int c;
            while ((c = reader.read()) != -1) sb.append((char)c);
            JSONObject obj = new JSONObject(sb.toString());
            for (String key : obj.keySet()) {
                JSONArray arr = obj.getJSONArray(key);
                cache.put(key, new double[]{arr.getDouble(0), arr.getDouble(1)});
            }
        } catch (Exception e) {
            // Ignore cache load errors
        }
    }

    private static void saveCacheToDisk() {
        try (FileWriter writer = new FileWriter(CACHE_FILE)) {
            JSONObject obj = new JSONObject();
            for (Map.Entry<String, double[]> entry : cache.entrySet()) {
                obj.put(entry.getKey(), new JSONArray(entry.getValue()));
            }
            writer.write(obj.toString());
        } catch (IOException e) {
            // Ignore cache save errors
        }
    }

    /**
     * Fetches walking route distance (meters) and duration (seconds) from OSRM
     * @param lat1 Latitude of origin
     * @param lon1 Longitude of origin
     * @param lat2 Latitude of destination
     * @param lon2 Longitude of destination
     * @return double[]{distanceMeters, durationSeconds} or null if not found
     */
    public static double[] fetchRoute(double lat1, double lon1, double lat2, double lon2) {
        String key = lat1 + "," + lon1 + "," + lat2 + "," + lon2;
        if (cache.containsKey(key)) return cache.get(key);
        try {
            String urlStr = String.format(
                "http://router.project-osrm.org/route/v1/foot/%f,%f;%f,%f?overview=false",
                lon1, lat1, lon2, lat2
            );
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) response.append(inputLine);
            in.close();
            JSONObject json = new JSONObject(response.toString());
            JSONArray routes = json.getJSONArray("routes");
            if (routes.length() > 0) {
                JSONObject route = routes.getJSONObject(0);
                double distance = route.getDouble("distance");
                double duration = route.getDouble("duration");
                double[] result = new double[]{distance, duration};
                cache.put(key, result);
                saveCacheToDisk();
                return result;
            }
        } catch (Exception e) {
            // Could not fetch route, return null
        }
        return null;
    }

    /**
     * Fetches full route details (steps, geometry) from OSRM Directions API
     * @param lat1 Latitude of origin
     * @param lon1 Longitude of origin
     * @param lat2 Latitude of destination
     * @param lon2 Longitude of destination
     * @return JSONObject with distance, duration, steps, geometry
     */
    public static JSONObject fetchRouteDetails(double lat1, double lon1, double lat2, double lon2) {
        try {
            String urlStr = String.format(
                "http://router.project-osrm.org/route/v1/foot/%f,%f;%f,%f?overview=full&steps=true&geometries=polyline",
                lon1, lat1, lon2, lat2
            );
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) response.append(inputLine);
            in.close();
            JSONObject json = new JSONObject(response.toString());
            JSONArray routes = json.getJSONArray("routes");
            if (routes.length() > 0) {
                JSONObject route = routes.getJSONObject(0);
                double distance = route.getDouble("distance");
                double duration = route.getDouble("duration");
                String geometry = route.getString("geometry");
                JSONArray steps = route.getJSONArray("legs").getJSONObject(0).getJSONArray("steps");
                JSONObject result = new JSONObject();
                result.put("distance", distance);
                result.put("duration", duration);
                result.put("geometry", geometry);
                result.put("steps", steps);
                return result;
            }
        } catch (Exception e) {
            // Could not fetch route details
        }
        return null;
    }
}
