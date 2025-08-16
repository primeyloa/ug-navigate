package com.myuser.ugcampusroutefinder.core;

import com.myuser.ugcampusroutefinder.model.Route;

/**
 * Defines the contract for any class that can provide traffic data.
 * This abstraction allows us to easily switch between simulated data and a live API feed.
 */
public interface TrafficDataProvider {
    /**
     * Gets the current traffic weight for a given route.
     * A weight of 1.0 means normal traffic, > 1.0 means heavier traffic,
     * and < 1.0 could mean lighter than usual traffic.
     *
     * @param route The route for which to get the traffic weight.
     * @return A double representing the traffic multiplier.
     */
    double getTrafficWeight(Route route);
}
