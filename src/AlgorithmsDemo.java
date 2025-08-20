import java.util.*;

/**
 * Educational algorithms demo for VAM, Northwest Corner and CPM.
 * These implementations are simplified for teaching and small inputs.
 */
public class AlgorithmsDemo {

    // ---------------- Transportation Problem Helpers ----------------
    public static class TransportationResult {
        public final int[][] allocation; // same shape as cost
        public final int totalCost;
        public TransportationResult(int[][] allocation, int totalCost) {
            this.allocation = allocation; this.totalCost = totalCost;
        }
    }

    public static int computeCost(int[][] allocation, int[][] cost) {
        int sum = 0;
        for (int i = 0; i < allocation.length; i++)
            for (int j = 0; j < allocation[0].length; j++)
                sum += allocation[i][j] * cost[i][j];
        return sum;
    }

    // Northwest Corner Method
    public static TransportationResult northwestCorner(int[] supply, int[] demand, int[][] cost) {
        int m = supply.length, n = demand.length;
        int[][] alloc = new int[m][n];
        int i = 0, j = 0;
        int[] s = Arrays.copyOf(supply, m);
        int[] d = Arrays.copyOf(demand, n);
        while (i < m && j < n) {
            int x = Math.min(s[i], d[j]);
            alloc[i][j] = x;
            s[i] -= x; d[j] -= x;
            if (s[i] == 0) i++; else j++;
        }
        return new TransportationResult(alloc, computeCost(alloc, cost));
    }

    // Vogel's Approximation Method (simplified)
    public static TransportationResult vogelApproximation(int[] supply, int[] demand, int[][] cost) {
        int m = supply.length, n = demand.length;
        int[][] alloc = new int[m][n];
        int[] s = Arrays.copyOf(supply, m);
        int[] d = Arrays.copyOf(demand, n);
        boolean[] rowDone = new boolean[m];
        boolean[] colDone = new boolean[n];
        int remainingRows = m, remainingCols = n;

        while (remainingRows > 0 && remainingCols > 0) {
            // penalties
            int bestRow = -1, bestRowPenalty = -1;
            for (int i = 0; i < m; i++) if (!rowDone[i]) {
                int[] two = twoSmallest(cost[i], colDone);
                int p = two[1] - two[0];
                if (p > bestRowPenalty) { bestRowPenalty = p; bestRow = i; }
            }

            int bestCol = -1, bestColPenalty = -1;
            for (int j = 0; j < n; j++) if (!colDone[j]) {
                int[] two = twoSmallestCol(cost, j, rowDone);
                int p = two[1] - two[0];
                if (p > bestColPenalty) { bestColPenalty = p; bestCol = j; }
            }

            boolean chooseRow = bestRowPenalty >= bestColPenalty;
            if (chooseRow) {
                // pick cheapest in row bestRow
                int col = -1, minC = Integer.MAX_VALUE;
                for (int j = 0; j < n; j++) if (!colDone[j] && cost[bestRow][j] < minC) { minC = cost[bestRow][j]; col = j; }
                int x = Math.min(s[bestRow], d[col]);
                alloc[bestRow][col] += x;
                s[bestRow] -= x; d[col] -= x;
                if (s[bestRow] == 0) { rowDone[bestRow] = true; remainingRows--; }
                if (d[col] == 0) { colDone[col] = true; remainingCols--; }
            } else {
                int row = -1, minC = Integer.MAX_VALUE;
                for (int i = 0; i < m; i++) if (!rowDone[i] && cost[i][bestCol] < minC) { minC = cost[i][bestCol]; row = i; }
                int x = Math.min(s[row], d[bestCol]);
                alloc[row][bestCol] += x;
                s[row] -= x; d[bestCol] -= x;
                if (s[row] == 0) { rowDone[row] = true; remainingRows--; }
                if (d[bestCol] == 0) { colDone[bestCol] = true; remainingCols--; }
            }
        }
        return new TransportationResult(alloc, computeCost(alloc, cost));
    }

    private static int[] twoSmallest(int[] row, boolean[] colDone) {
        int a = Integer.MAX_VALUE, b = Integer.MAX_VALUE;
        for (int j = 0; j < row.length; j++) if (!colDone[j]) {
            int v = row[j];
            if (v < a) { b = a; a = v; }
            else if (v < b) { b = v; }
        }
        if (b == Integer.MAX_VALUE) b = a;
        return new int[]{a, b};
    }
    private static int[] twoSmallestCol(int[][] cost, int col, boolean[] rowDone) {
        int a = Integer.MAX_VALUE, b = Integer.MAX_VALUE;
        for (int i = 0; i < cost.length; i++) if (!rowDone[i]) {
            int v = cost[i][col];
            if (v < a) { b = a; a = v; }
            else if (v < b) { b = v; }
        }
        if (b == Integer.MAX_VALUE) b = a;
        return new int[]{a, b};
    }

    // ---------------- Critical Path Method (CPM) ----------------
    public static class CPMTask {
        public final String id; public final int duration; public final List<String> deps;
        public CPMTask(String id, int duration, String... deps) { this.id = id; this.duration = duration; this.deps = Arrays.asList(deps); }
    }
    public static class CPMResult {
        public final Map<String,Integer> earliestFinish; public final List<String> criticalPath; public final int projectTime;
        public CPMResult(Map<String,Integer> ef, List<String> cp, int t) { earliestFinish = ef; criticalPath = cp; projectTime = t; }
    }

    public static CPMResult cpm(List<CPMTask> tasks) {
        Map<String, CPMTask> byId = new HashMap<>();
        Map<String, Integer> indeg = new HashMap<>();
        Map<String, List<String>> adj = new HashMap<>();
        for (CPMTask t : tasks) { byId.put(t.id, t); indeg.put(t.id, 0); adj.put(t.id, new ArrayList<>()); }
        for (CPMTask t : tasks) for (String d : t.deps) { adj.get(d).add(t.id); indeg.put(t.id, indeg.get(t.id)+1); }

        // topological order
        Deque<String> dq = new ArrayDeque<>();
        for (String id : indeg.keySet()) if (indeg.get(id) == 0) dq.add(id);
        Map<String,Integer> earliestFinish = new HashMap<>();
        Map<String,String> parent = new HashMap<>();
        while (!dq.isEmpty()) {
            String u = dq.remove();
            int start = 0;
            CPMTask tu = byId.get(u);
            for (String pred : tu.deps) start = Math.max(start, earliestFinish.get(pred));
            earliestFinish.put(u, start + tu.duration);
            for (String v : adj.get(u)) {
                if (earliestFinish.get(u) >= (earliestFinish.getOrDefault(parent.get(v), 0))) parent.put(v, u);
                indeg.put(v, indeg.get(v)-1);
                if (indeg.get(v) == 0) dq.add(v);
            }
        }
        // find project time and reconstruct critical path
        String last = null; int t = 0;
        for (String id : earliestFinish.keySet()) if (earliestFinish.get(id) >= t) { t = earliestFinish.get(id); last = id; }
        List<String> path = new ArrayList<>();
        while (last != null) { path.add(0, last); last = parent.get(last); }
        return new CPMResult(earliestFinish, path, t);
    }

    // ---------------- Convenience Demos ----------------
    public static String demoText() {
        StringBuilder out = new StringBuilder();

        // Example small cost matrix representing 3 halls to 3 classrooms flow costs
        int[] supply = {20, 30, 25};
        int[] demand = {10, 35, 30};
        int[][] cost = {
                {8, 6, 10},
                {9, 7, 4},
                {3, 4, 2}
        };
        TransportationResult ncm = northwestCorner(supply, demand, cost);
        TransportationResult vam = vogelApproximation(supply, demand, cost);

        out.append("🚚 Transportation Problem (Initial Solutions)\n");
        out.append("Northwest Corner cost: ").append(ncm.totalCost).append("\n");
        out.append("Vogel Approximation cost: ").append(vam.totalCost).append("\n\n");

        // CPM tiny example (class-change trip composed of legs)
        List<CPMTask> tasks = Arrays.asList(
                new CPMTask("WalkHall", 4),
                new CPMTask("CrossSquare", 3, "WalkHall"),
                new CPMTask("LibraryStop", 2, "CrossSquare"),
                new CPMTask("EnterClass", 1, "LibraryStop")
        );
        CPMResult cpm = cpm(tasks);
        out.append("🕒 Critical Path Method\n");
        out.append("Critical path: ").append(String.join(" → ", cpm.criticalPath)).append("\n");
        out.append("Project time: ").append(cpm.projectTime).append(" min\n");

        return out.toString();
    }
}


