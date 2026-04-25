import java.util.*;
class Solution {
    public int maxDistance(int side, int[][] points, int k) {
        int n = points.length;
        long L = 4L * side;
        long[] p = new long[n];
        for (int i = 0; i < n; i++) {
            int x = points[i][0], y = points[i][1];
            if (y == 0) {
                // bottom edge, left->right
                p[i] = x;
            } else if (x == side) {
                // right edge, bottom->top
                p[i] = side + y;
            } else if (y == side) {
                // top edge, right->left
                p[i] = 2L * side + (side - x);
            } else {
                // left edge, top->bottom
                p[i] = 3L * side + (side - y);
            }
        }
        Arrays.sort(p);
        // duplicate array
        long[] p2 = new long[2 * n];
        for (int i = 0; i < n; i++) {
            p2[i] = p[i];
            p2[i + n] = p[i] + L;
        }
        // helper lower_bound
        java.util.function.BiFunction<long[], Long, Integer> lowerBound = (arr, val) -> {
            int lo = 0, hi = arr.length;
            while (lo < hi) {
                int mid = (lo + hi) >>> 1;
                if (arr[mid] >= val) hi = mid;
                else lo = mid + 1;
            }
            return lo;
        };
        // Feasibility check for given D
        java.util.function.IntPredicate feasible = (int Dint) -> {
            if (k == 1) return true;
            long D = Dint;
            int m = 2 * n;
            int INF = m;
            int[] next0 = new int[m + 1];
            next0[m] = INF;
            for (int i = 0; i < m; i++) {
                long target = p2[i] + D;
                int idx = lowerBound.apply(p2, target);
                if (idx > m) idx = m;
                next0[i] = idx;
            }
            // build binary lifting table
            int LOG = 0;
            while ((1 << LOG) <= k) LOG++;
            int[][] nxt = new int[LOG][m + 1];
            for (int i = 0; i <= m; i++) nxt[0][i] = (i < m ? next0[i] : INF);
            for (int b = 1; b < LOG; b++) {
                for (int i = 0; i <= m; i++) {
                    int mid = nxt[b - 1][i];
                    nxt[b][i] = (mid <= m ? nxt[b - 1][mid] : INF);
                }
            }
            int need = k - 1;
            for (int start = 0; start < n; start++) {
                int idx = start;
                int rem = need;
                for (int b = 0; b < LOG && idx < INF; b++) {
                    if ((rem & (1 << b)) != 0) {
                        idx = nxt[b][idx];
                    }
                    if (idx >= start + n) break; // exceeded one full circle
                }
                if (idx < INF && idx < start + n) {
                    long span = p2[idx] - p2[start];
                    if (L - span >= D) return true;
                }
            }
            return false;
        };
        int lo = 0, hi = 2 * side; // Manhattan distances range 0..2*side
        while (lo < hi) {
            int mid = lo + (hi - lo + 1) / 2;
            if (feasible.test(mid)) lo = mid;
            else hi = mid - 1;
        }
        return lo;
    }
}