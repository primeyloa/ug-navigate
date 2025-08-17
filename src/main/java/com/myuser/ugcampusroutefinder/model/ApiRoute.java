package com.myuser.ugcampusroutefinder.model;

import org.jxmapviewer.viewer.GeoPosition;

import java.util.List;

/**
 * Represents a route returned from the OpenRouteService API.
 *
 * @param path      A list of geographic coordinates representing the route's geometry.
 * @param distance  The total distance of the route in meters.
 * @param duration  The estimated duration of the route in seconds.
 */
public record ApiRoute(List<GeoPosition> path, double distance, double duration) {
}
