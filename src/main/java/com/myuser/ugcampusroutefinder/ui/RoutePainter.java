package com.myuser.ugcampusroutefinder.ui;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.painter.Painter;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class RoutePainter implements Painter<JXMapViewer> {

    private final List<GeoPosition> track;
    private final Color color;
    private final Stroke stroke;

    public RoutePainter(List<GeoPosition> track) {
        // Make a copy of the track
        this.track = new ArrayList<>(track);
        this.color = new Color(0, 0, 255, 150); // Blue, slightly transparent
        this.stroke = new BasicStroke(4.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    }

    @Override
    public void paint(Graphics2D g, JXMapViewer map, int w, int h) {
        g = (Graphics2D) g.create();

        // Convert from geo-positions to screen points
        Rectangle rect = map.getViewportBounds();
        g.translate(-rect.x, -rect.y);

        g.setColor(this.color);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setStroke(this.stroke);

        int lastX = -1;
        int lastY = -1;

        for (GeoPosition gp : this.track) {
            Point2D pt = map.getTileFactory().geoToPixel(gp, map.getZoom());
            if (lastX != -1 && lastY != -1) {
                g.drawLine(lastX, lastY, (int) pt.getX(), (int) pt.getY());
            }
            lastX = (int) pt.getX();
            lastY = (int) pt.getY();
        }

        g.dispose();
    }
}
