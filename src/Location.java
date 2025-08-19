import java.util.*;

/**
 * Represents a location/landmark on the UG campus
 */
class Location {
    private String id;
    private String name;
    private double latitude;
    private double longitude;
    private String type; // "academic", "administrative", "recreational", "service", etc.
    private List<String> keywords; // for searching (e.g., "bank", "library", "cafeteria")
    private String googlePhotoUrl; // Google Photos link for the location

    public Location(String id, String name, double latitude, double longitude, String type) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.type = type;
        this.keywords = new ArrayList<>();
        this.googlePhotoUrl = null;
    }

    public Location(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.id = null;
        this.name = null;
        this.type = null;
        this.keywords = new ArrayList<>();
        this.googlePhotoUrl = null;
    }

    // Getters and setters
    public String getId() { return id; }
    public String getName() { return name; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public String getType() { return type; }
    public List<String> getKeywords() { return keywords; }

    public String getGooglePhotoUrl() {
        return googlePhotoUrl;
    }

    public void setGooglePhotoUrl(String url) {
        this.googlePhotoUrl = url;
    }

    public void addKeyword(String keyword) {
        keywords.add(keyword.toLowerCase());
    }

    @Override
    public String toString() {
        return name + " (" + type + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Location location = (Location) obj;
        return Objects.equals(id, location.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}