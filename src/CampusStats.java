/**
 * Campus statistics
 */
class CampusStats {
    private int totalLocations;
    private int totalConnections;
    private int cachedRoutes;

    public CampusStats(int totalLocations, int totalConnections, int cachedRoutes) {
        this.totalLocations = totalLocations;
        this.totalConnections = totalConnections;
        this.cachedRoutes = cachedRoutes;
    }

    public int getTotalLocations() { return totalLocations; }
    public int getTotalConnections() { return totalConnections; }
    public int getCachedRoutes() { return cachedRoutes; }

    @Override
    public String toString() {
        return String.format("Campus Stats: %d locations, %d connections, %d cached routes",
                totalLocations, totalConnections, cachedRoutes);
    }
}
