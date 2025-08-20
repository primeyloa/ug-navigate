import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Minimal OpenStreetMap tile panel that renders a route polyline.
 * No external libraries required; uses HTTP tile fetch with a small in-memory cache.
 */
public class OSMMapPanel extends JPanel {
    private static final int TILE_SIZE = 256;
    private static final int MIN_ZOOM = 3;
    private static final int MAX_ZOOM = 19;

    private List<Location> routePath = new ArrayList<>();
    private Location startMarker = null;
    private Location endMarker = null;
    private int zoom = 16;
    private final Map<String, BufferedImage> tileCache = new HashMap<>();

    private String[] subdomains = new String[]{"a", "b", "c"};
    private String tileTemplate = "https://tile.openstreetmap.org/{z}/{x}/{y}.png"; // default (for demos)
    private String apiKey = null;
    private String attribution = "© OpenStreetMap contributors";
    private final List<Provider> fallbacks = new ArrayList<>();
    private int subdomainIndex = 0;

    private int panOffsetX = 0;
    private int panOffsetY = 0;
    private Point dragStart = null;

    private List<Edge> routeEdges = new ArrayList<>();
    private List<Location> routeLandmarks = new ArrayList<>();

    public OSMMapPanel() {
        setOpaque(true);
        setBackground(Color.BLACK);

        addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                int notches = e.getWheelRotation();
                if (notches < 0 && zoom < MAX_ZOOM) {
                    zoom++;
                    repaint();
                } else if (notches > 0 && zoom > MIN_ZOOM) {
                    zoom--;
                    repaint();
                }
            }
        });

        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                dragStart = e.getPoint();
            }
        });

        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseDragged(java.awt.event.MouseEvent e) {
                if (dragStart != null) {
                    panOffsetX += e.getX() - dragStart.x;
                    panOffsetY += e.getY() - dragStart.y;
                    dragStart = e.getPoint();
                    repaint();
                }
            }
        });
    }

    public void setPath(List<Location> path) {
        routePath = path != null ? new ArrayList<>(path) : new ArrayList<>();
        panOffsetX = 0;
        panOffsetY = 0;
        repaint();
    }

    /**
     * Sets the route path to display and repaints the panel.
     */
    public void setRoutePath(List<Location> path) {
        this.routePath = path != null ? new ArrayList<>(path) : new ArrayList<>();
        repaint();
    }

    public void setMarkers(Location start, Location end) {
        this.startMarker = start;
        this.endMarker = end;
        repaint();
    }

    public void setTileProvider(String template, String attributionText, String[] subdomains, String apiKey) {
        if (template != null && !template.isEmpty()) this.tileTemplate = template;
        if (attributionText != null && !attributionText.isEmpty()) this.attribution = attributionText;
        if (subdomains != null && subdomains.length > 0) this.subdomains = subdomains;
        this.apiKey = apiKey;
        tileCache.clear();
        repaint();
    }

    public void addFallbackProvider(String template, String attributionText, String[] subdomains, String apiKey) {
        fallbacks.add(new Provider(template, attributionText, subdomains, apiKey));
    }

    public void setRouteEdges(List<Edge> edges) {
        this.routeEdges = edges != null ? new ArrayList<>(edges) : new ArrayList<>();
        repaint();
    }
    public void setLandmarks(List<Location> landmarks) {
        this.routeLandmarks = landmarks != null ? new ArrayList<>(landmarks) : new ArrayList<>();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Center logic: use route edges if available, else routePath
        double centerLat = 5.6514, centerLon = -0.1888;
        List<Location> allRouteLocs = new ArrayList<>();
        if (!routeEdges.isEmpty()) {
            for (Edge e : routeEdges) {
                allRouteLocs.add(e.getSource());
                allRouteLocs.add(e.getDestination());
            }
        } else if (!routePath.isEmpty()) {
            allRouteLocs.addAll(routePath);
        }
        if (!allRouteLocs.isEmpty()) {
            double sumLat = 0, sumLon = 0;
            for (Location loc : allRouteLocs) { sumLat += loc.getLatitude(); sumLon += loc.getLongitude(); }
            centerLat = sumLat / allRouteLocs.size();
            centerLon = sumLon / allRouteLocs.size();
        }

        // Compute world pixel bounds for route
        double minXPx = Double.POSITIVE_INFINITY, minYPx = Double.POSITIVE_INFINITY;
        double maxXPx = Double.NEGATIVE_INFINITY, maxYPx = Double.NEGATIVE_INFINITY;

        List<double[]> pathPixels = new ArrayList<>();
        for (Location loc : routePath) {
            double x = lonToPixelX(loc.getLongitude(), zoom);
            double y = latToPixelY(loc.getLatitude(), zoom);
            pathPixels.add(new double[]{x, y});
            minXPx = Math.min(minXPx, x);
            minYPx = Math.min(minYPx, y);
            maxXPx = Math.max(maxXPx, x);
            maxYPx = Math.max(maxYPx, y);
        }

        int padding = 60;
        if (pathPixels.isEmpty()) {
            double cx = lonToPixelX(centerLon, zoom);
            double cy = latToPixelY(centerLat, zoom);
            minXPx = cx - 200;
            maxXPx = cx + 200;
            minYPx = cy - 200;
            maxYPx = cy + 200;
        }

        // Compute tile range; avoid negative ranges if empty
        int startTileX = (int) Math.floor((minXPx - padding) / TILE_SIZE);
        int endTileX = (int) Math.floor((maxXPx + padding) / TILE_SIZE);
        int startTileY = (int) Math.floor((minYPx - padding) / TILE_SIZE);
        int endTileY = (int) Math.floor((maxYPx + padding) / TILE_SIZE);
        if (Double.isInfinite(minXPx) || Double.isInfinite(minYPx)) {
            double cx = lonToPixelX(centerLon, zoom);
            double cy = latToPixelY(centerLat, zoom);
            startTileX = (int) Math.floor((cx - 256) / TILE_SIZE);
            endTileX = (int) Math.floor((cx + 256) / TILE_SIZE);
            startTileY = (int) Math.floor((cy - 256) / TILE_SIZE);
            endTileY = (int) Math.floor((cy + 256) / TILE_SIZE);
        }

        // World origin for drawn area
        int originWorldX = startTileX * TILE_SIZE;
        int originWorldY = startTileY * TILE_SIZE;

        // Center the bounding box inside the panel
        double bboxCenterX = (minXPx + maxXPx) / 2.0;
        double bboxCenterY = (minYPx + maxYPx) / 2.0;
        int width = getWidth();
        int height = getHeight();
        int screenOffsetX = (int) Math.round(width / 2.0 - (bboxCenterX - originWorldX)) + panOffsetX;
        int screenOffsetY = (int) Math.round(height / 2.0 - (bboxCenterY - originWorldY)) + panOffsetY;

        // Draw tiles
        for (int ty = startTileY; ty <= endTileY; ty++) {
            for (int tx = startTileX; tx <= endTileX; tx++) {
                int xOnScreen = tx * TILE_SIZE - originWorldX + screenOffsetX;
                int yOnScreen = ty * TILE_SIZE - originWorldY + screenOffsetY;
                BufferedImage tile = fetchTile(zoom, tx, ty);
                if (tile != null) {
                    g2.drawImage(tile, xOnScreen, yOnScreen, null);
                } else {
                    g2.setColor(Color.DARK_GRAY);
                    g2.fillRect(xOnScreen, yOnScreen, TILE_SIZE, TILE_SIZE);
                }
            }
        }

        // Draw route using edges (actual road segments)
        if (!routeEdges.isEmpty()) {
            System.out.println("Drawing route with " + routeEdges.size() + " edges");
            // Draw shadow first for better visibility
            g2.setStroke(new BasicStroke(7f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(new Color(0, 0, 0, 100)); // Black shadow
            for (Edge edge : routeEdges) {
                double x1 = lonToPixelX(edge.getSource().getLongitude(), zoom) - originWorldX + screenOffsetX;
                double y1 = latToPixelY(edge.getSource().getLatitude(), zoom) - originWorldY + screenOffsetY;
                double x2 = lonToPixelX(edge.getDestination().getLongitude(), zoom) - originWorldX + screenOffsetX;
                double y2 = latToPixelY(edge.getDestination().getLatitude(), zoom) - originWorldY + screenOffsetY;
                g2.drawLine((int)x1+1, (int)y1+1, (int)x2+1, (int)y2+1);
            }
            
            // Draw main route line
            g2.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(new Color(30, 144, 255, 220)); // Bright Dodger blue
            for (Edge edge : routeEdges) {
                double x1 = lonToPixelX(edge.getSource().getLongitude(), zoom) - originWorldX + screenOffsetX;
                double y1 = latToPixelY(edge.getSource().getLatitude(), zoom) - originWorldY + screenOffsetY;
                double x2 = lonToPixelX(edge.getDestination().getLongitude(), zoom) - originWorldX + screenOffsetX;
                double y2 = latToPixelY(edge.getDestination().getLatitude(), zoom) - originWorldY + screenOffsetY;
                g2.drawLine((int)x1, (int)y1, (int)x2, (int)y2);
            }
        } else if (!routePath.isEmpty()) {
            // Fallback: draw polyline between locations
            System.out.println("Drawing route with " + routePath.size() + " path points");
            // Draw shadow first
            g2.setStroke(new BasicStroke(6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(new Color(0, 0, 0, 100)); // Black shadow
            for (int i = 0; i < routePath.size() - 1; i++) {
                double x1 = lonToPixelX(routePath.get(i).getLongitude(), zoom) - originWorldX + screenOffsetX;
                double y1 = latToPixelY(routePath.get(i).getLatitude(), zoom) - originWorldY + screenOffsetY;
                double x2 = lonToPixelX(routePath.get(i+1).getLongitude(), zoom) - originWorldX + screenOffsetX;
                double y2 = latToPixelY(routePath.get(i+1).getLatitude(), zoom) - originWorldY + screenOffsetY;
                g2.drawLine((int)x1+1, (int)y1+1, (int)x2+1, (int)y2+1);
            }
            
            // Draw main route line
            g2.setStroke(new BasicStroke(4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(new Color(30, 144, 255, 200)); // Bright blue
            for (int i = 0; i < routePath.size() - 1; i++) {
                double x1 = lonToPixelX(routePath.get(i).getLongitude(), zoom) - originWorldX + screenOffsetX;
                double y1 = latToPixelY(routePath.get(i).getLatitude(), zoom) - originWorldY + screenOffsetY;
                double x2 = lonToPixelX(routePath.get(i+1).getLongitude(), zoom) - originWorldX + screenOffsetX;
                double y2 = latToPixelY(routePath.get(i+1).getLatitude(), zoom) - originWorldY + screenOffsetY;
                g2.drawLine((int)x1, (int)y1, (int)x2, (int)y2);
            }
        }

        // Draw landmarks
        g2.setColor(new Color(255, 140, 0, 220)); // Orange
        for (Location lm : routeLandmarks) {
            double x = lonToPixelX(lm.getLongitude(), zoom) - originWorldX + screenOffsetX;
            double y = latToPixelY(lm.getLatitude(), zoom) - originWorldY + screenOffsetY;
            g2.fillOval((int)x-8, (int)y-8, 16, 16);
            g2.setColor(Color.BLACK);
            g2.drawOval((int)x-8, (int)y-8, 16, 16);
            g2.setColor(new Color(255, 140, 0, 220));
        }

        // Draw start/end markers
        if (startMarker != null) {
            double x = lonToPixelX(startMarker.getLongitude(), zoom) - originWorldX + screenOffsetX;
            double y = latToPixelY(startMarker.getLatitude(), zoom) - originWorldY + screenOffsetY;
            System.out.println("Start marker: " + startMarker.getName() + " at (" + startMarker.getLatitude() + ", " + startMarker.getLongitude() + ") -> screen (" + (int)x + ", " + (int)y + ")");
            
            // Draw shadow for better visibility
            g2.setColor(new Color(0, 0, 0, 100));
            g2.fillOval((int)x-12, (int)y-12, 24, 24);
            
            // Draw main marker
            g2.setColor(new Color(50, 205, 50, 240)); // Bright lime green
            g2.fillOval((int)x-10, (int)y-10, 20, 20);
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(2f));
            g2.drawOval((int)x-10, (int)y-10, 20, 20);
            
            // Draw "S" label
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 12));
            g2.drawString("S", (int)x-3, (int)y+4);
        }
        if (endMarker != null) {
            double x = lonToPixelX(endMarker.getLongitude(), zoom) - originWorldX + screenOffsetX;
            double y = latToPixelY(endMarker.getLatitude(), zoom) - originWorldY + screenOffsetY;
            System.out.println("End marker: " + endMarker.getName() + " at (" + endMarker.getLatitude() + ", " + endMarker.getLongitude() + ") -> screen (" + (int)x + ", " + (int)y + ")");
            
            // Draw shadow for better visibility
            g2.setColor(new Color(0, 0, 0, 100));
            g2.fillOval((int)x-12, (int)y-12, 24, 24);
            
            // Draw main marker
            g2.setColor(new Color(220, 20, 60, 240)); // Bright crimson
            g2.fillOval((int)x-10, (int)y-10, 20, 20);
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(2f));
            g2.drawOval((int)x-10, (int)y-10, 20, 20);
            
            // Draw "E" label
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 12));
            g2.drawString("E", (int)x-3, (int)y+4);
        }
        g2.dispose();
    }

    private void drawMarker(Graphics2D g2, Location loc, int originWorldX, int originWorldY, int screenOffsetX, int screenOffsetY, Color color) {
        if (loc == null) return;
        double px = lonToPixelX(loc.getLongitude(), zoom) - originWorldX + screenOffsetX;
        double py = latToPixelY(loc.getLatitude(), zoom) - originWorldY + screenOffsetY;
        int r = 6;
        g2.setColor(color);
        g2.fillOval((int)px - r, (int)py - r, r*2, r*2);
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(2f));
        g2.drawOval((int)px - r, (int)py - r, r*2, r*2);
    }

    private BufferedImage fetchTile(int z, int x, int y) {
        // Handle wrap for x
        int max = (1 << z);
        if (x < 0) x = x % max + max;
        if (x >= max) x = x % max;
        if (y < 0 || y >= max) return null; // outside world

        String key = z + "/" + x + "/" + y;
        if (tileCache.containsKey(key)) return tileCache.get(key);
        // Try primary provider and then fallbacks
        BufferedImage img = fetchFromProvider(tileTemplate, subdomains, apiKey, z, x, y, key);
        if (img != null) return img;
        for (Provider p : fallbacks) {
            img = fetchFromProvider(p.template, p.subdomains, p.apiKey, z, x, y, key);
            if (img != null) {
                this.attribution = p.attribution; // switch attribution to the working provider
                return img;
            }
        }
        return null;
    }

    private BufferedImage fetchFromProvider(String template, String[] subs, String apiKey,
                                            int z, int x, int y, String cacheKey) {
        try {
            String sub = subs != null && subs.length > 0 ? subs[subdomainIndex % subs.length] : "";
            subdomainIndex++;
            String urlStr = template
                    .replace("{s}", sub)
                    .replace("{z}", String.valueOf(z))
                    .replace("{x}", String.valueOf(x))
                    .replace("{y}", String.valueOf(y))
                    .replace("{key}", apiKey == null ? "" : apiKey);

            URL url = new URL(urlStr);
            var conn = (java.net.HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(4000);
            conn.setReadTimeout(7000);
            conn.setRequestProperty("User-Agent", "UGNavigate/1.0 (+https://example.com/contact)");
            try (var is = conn.getInputStream()) {
                BufferedImage img = ImageIO.read(is);
                tileCache.put(cacheKey, img);
                return img;
            }
        } catch (Exception ignore) {
            return null;
        }
    }

    private static class Provider {
        String template; String attribution; String[] subdomains; String apiKey;
        Provider(String t, String a, String[] s, String k) {
            this.template = t; this.attribution = a; this.subdomains = s; this.apiKey = k;
        }
    }

    private static double lonToPixelX(double lon, int zoom) {
        double scale = (1 << zoom) * TILE_SIZE;
        return (lon + 180.0) / 360.0 * scale;
    }

    private static double latToPixelY(double lat, int zoom) {
        double sinLat = Math.sin(Math.toRadians(lat));
        double mapSize = (1 << zoom) * TILE_SIZE;
        double y = 0.5 - Math.log((1 + sinLat) / (1 - sinLat)) / (4 * Math.PI);
        return y * mapSize;
    }
}
