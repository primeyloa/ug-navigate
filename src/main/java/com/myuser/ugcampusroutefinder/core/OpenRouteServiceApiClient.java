package com.myuser.ugcampusroutefinder.core;

import com.myuser.ugcampusroutefinder.model.ApiRoute;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jxmapviewer.viewer.GeoPosition;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class OpenRouteServiceApiClient {

    private static final String API_BASE_URL = "https://api.openrouteservice.org/v2/directions/driving-car";
    private final String apiKey;
    private final HttpClient httpClient;

    public OpenRouteServiceApiClient(String apiKey) {
        this.apiKey = apiKey;
        this.httpClient = HttpClient.newHttpClient();
    }

    public ApiRoute getRoute(GeoPosition start, GeoPosition end) throws IOException, InterruptedException {
        String url = String.format("%s?api_key=%s&start=%s,%s&end=%s,%s",
                API_BASE_URL,
                apiKey,
                start.getLongitude(), start.getLatitude(),
                end.getLongitude(), end.getLatitude());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/json, application/geo+json, application/gpx+xml, img/png; charset=utf-t-8")
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("Failed to get route from OpenRouteService. Status code: " + response.statusCode() + " Body: " + response.body());
        }

        return parseResponse(response.body());
    }

    private ApiRoute parseResponse(String responseBody) {
        JSONObject jsonResponse = new JSONObject(responseBody);
        JSONArray features = jsonResponse.getJSONArray("features");
        if (features.isEmpty()) {
            throw new RuntimeException("No route found in API response.");
        }

        JSONObject feature = features.getJSONObject(0);
        JSONObject properties = feature.getJSONObject("properties");
        JSONObject summary = properties.getJSONArray("segments").getJSONObject(0);
        double distance = summary.getDouble("distance"); // in meters
        double duration = summary.getDouble("duration"); // in seconds

        JSONObject geometry = feature.getJSONObject("geometry");
        JSONArray coordinates = geometry.getJSONArray("coordinates");

        List<GeoPosition> path = new ArrayList<>();
        for (int i = 0; i < coordinates.length(); i++) {
            JSONArray coord = coordinates.getJSONArray(i);
            double lon = coord.getDouble(0);
            double lat = coord.getDouble(1);
            path.add(new GeoPosition(lat, lon));
        }

        return new ApiRoute(path, distance, duration);
    }
}
