package com.myuser.ugcampusroutefinder.core;

import com.myuser.ugcampusroutefinder.model.Route;
import java.time.LocalTime;

/**
 * A simulated traffic data provider that changes traffic weights based on the time of day.
 * This class provides a simple way to test dynamic routing.
 */
public class SimulatedTrafficProvider implements TrafficDataProvider {

    @Override
    public double getTrafficWeight(Route route) {
        LocalTime now = LocalTime.now();

        // Peak hours are defined as 8:00-10:00 AM and 4:00-6:00 PM
        boolean isMorningRush = now.isAfter(LocalTime.of(8, 0)) && now.isBefore(LocalTime.of(10, 0));
        boolean isEveningRush = now.isAfter(LocalTime.of(16, 0)) && now.isBefore(LocalTime.of(18, 0));

        if (isMorningRush || isEveningRush) {
            return 1.5; // 50% heavier traffic during peak hours
        }

        return 1.0; // Normal traffic outside of peak hours
    }
}
