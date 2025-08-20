import java.util.*;

/**
 * Advanced sorting algorithms optimized for route organization
 */
class RouteSorting {

    /**
     * Quick Sort implementation optimized for routes
     * Average time complexity: O(n log n)
     * Uses divide and conquer strategy
     */
    public static class QuickSort {

        public static void sortByDistance(List<Route> routes) {
            if (routes.size() <= 1) return;
            quickSortRecursive(routes, 0, routes.size() - 1,
                    Comparator.comparingDouble(Route::getTotalDistance));
        }

        public static void sortByTime(List<Route> routes) {
            if (routes.size() <= 1) return;
            quickSortRecursive(routes, 0, routes.size() - 1,
                    Comparator.comparingDouble(Route::getTotalTime));
        }

        public static void sortByLandmarks(List<Route> routes) {
            if (routes.size() <= 1) return;
            quickSortRecursive(routes, 0, routes.size() - 1,
                    Comparator.comparingInt(r -> r.getLandmarksPassedThrough().size()));
        }

        private static void quickSortRecursive(List<Route> routes, int low, int high,
                                               Comparator<Route> comparator) {
            if (low < high) {
                int pivotIndex = partition(routes, low, high, comparator);
                quickSortRecursive(routes, low, pivotIndex - 1, comparator);
                quickSortRecursive(routes, pivotIndex + 1, high, comparator);
            }
        }

        private static int partition(List<Route> routes, int low, int high,
                                     Comparator<Route> comparator) {
            Route pivot = routes.get(high);
            int i = low - 1;

            for (int j = low; j < high; j++) {
                if (comparator.compare(routes.get(j), pivot) <= 0) {
                    i++;
                    Collections.swap(routes, i, j);
                }
            }

            Collections.swap(routes, i + 1, high);
            return i + 1;
        }
    }

    /**
     * Merge Sort implementation for stable sorting
     * Time complexity: O(n log n) guaranteed
     * More stable than QuickSort, better for partially sorted data
     */
    public static class MergeSort {

        public static void sortByDistance(List<Route> routes) {
            if (routes.size() <= 1) return;
            mergeSortRecursive(routes, Comparator.comparingDouble(Route::getTotalDistance));
        }

        public static void sortByTime(List<Route> routes) {
            if (routes.size() <= 1) return;
            mergeSortRecursive(routes, Comparator.comparingDouble(Route::getTotalTime));
        }

        public static void sortByComposite(List<Route> routes) {
            // Multi-criteria sorting: first by time, then by distance if times are equal
            Comparator<Route> composite = Comparator.comparingDouble(Route::getTotalTime)
                    .thenComparingDouble(Route::getTotalDistance);
            mergeSortRecursive(routes, composite);
        }

        private static void mergeSortRecursive(List<Route> routes, Comparator<Route> comparator) {
            if (routes.size() <= 1) return;

            int mid = routes.size() / 2;
            List<Route> left = new ArrayList<>(routes.subList(0, mid));
            List<Route> right = new ArrayList<>(routes.subList(mid, routes.size()));

            mergeSortRecursive(left, comparator);
            mergeSortRecursive(right, comparator);

            merge(routes, left, right, comparator);
        }

        private static void merge(List<Route> routes, List<Route> left, List<Route> right,
                                  Comparator<Route> comparator) {
            int i = 0, j = 0, k = 0;

            while (i < left.size() && j < right.size()) {
                if (comparator.compare(left.get(i), right.get(j)) <= 0) {
                    routes.set(k++, left.get(i++));
                } else {
                    routes.set(k++, right.get(j++));
                }
            }

            while (i < left.size()) routes.set(k++, left.get(i++));
            while (j < right.size()) routes.set(k++, right.get(j++));
        }
    }

    /**
     * Hybrid sorting algorithm that chooses optimal sorting method based on data characteristics
     * Implements adaptive sorting strategy
     */
    public static class AdaptiveSorting {

        public static void sortOptimal(List<Route> routes, String criteria) {
            Comparator<Route> comparator = getComparator(criteria);
            if (routes.size() <= 10) {
                // Use insertion sort for small datasets
                insertionSort(routes, comparator);
                return;
            }

            if (isNearlySorted(routes, comparator)) {
                // Stable sort for nearly-sorted data according to the requested criteria
                routes.sort(comparator);
                return;
            }

            // Default: comparator-driven sort (leverages TimSort in Java which is adaptive)
            routes.sort(comparator);
        }

        private static boolean isNearlySorted(List<Route> routes, Comparator<Route> comparator) {
            int inversions = 0;
            for (int i = 0; i < routes.size() - 1; i++) {
                if (comparator.compare(routes.get(i), routes.get(i + 1)) > 0) {
                    inversions++;
                    if (inversions > routes.size() / 4) return false; // Threshold: 25%
                }
            }
            return true;
        }

        private static void insertionSort(List<Route> routes, Comparator<Route> comparator) {
            for (int i = 1; i < routes.size(); i++) {
                Route key = routes.get(i);
                int j = i - 1;

                while (j >= 0 && comparator.compare(routes.get(j), key) > 0) {
                    routes.set(j + 1, routes.get(j));
                    j--;
                }
                routes.set(j + 1, key);
            }
        }

        private static Comparator<Route> getComparator(String criteria) {
            switch (criteria.toLowerCase()) {
                case "distance": return Comparator.comparingDouble(Route::getTotalDistance);
                case "time": return Comparator.comparingDouble(Route::getTotalTime);
                case "landmarks": return Comparator.comparingInt(r -> r.getLandmarksPassedThrough().size());
                default: return Comparator.comparingDouble(Route::getTotalTime);
            }
        }
    }
}
