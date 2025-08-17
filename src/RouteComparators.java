/**
 * Route comparison utilities for sorting
 */
import java.util.*;
class RouteComparators {
    public static final Comparator<Route> BY_DISTANCE =
            Comparator.comparingDouble(Route::getTotalDistance);

    public static final Comparator<Route> BY_TIME =
            Comparator.comparingDouble(Route::getTotalTime);

    public static final Comparator<Route> BY_LANDMARKS =
            Comparator.comparingInt(route -> route.getLandmarksPassedThrough().size());
}