import java.util.*;

class Solution {
    public int maxProductPath(int[][] grid) {
        int m = grid.length;
        int n = grid[0].length;
        long MOD = 1_000_000_007L;
        long[][] max = new long[m][n];
        long[][] min = new long[m][n];
        boolean[][] reach = new boolean[m][n];

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (i == 0 && j == 0) {
                    max[i][j] = grid[i][j];
                    min[i][j] = grid[i][j];
                    reach[i][j] = true;
                    continue;
                }
                long cur = grid[i][j];
                long curMax = Long.MIN_VALUE;
                long curMin = Long.MAX_VALUE;
                boolean ok = false;

                if (i > 0 && reach[i - 1][j]) {
                    long a = max[i - 1][j] * cur;
                    long b = min[i - 1][j] * cur;
                    curMax = Math.max(curMax, Math.max(a, b));
                    curMin = Math.min(curMin, Math.min(a, b));
                    ok = true;
                }
                if (j > 0 && reach[i][j - 1]) {
                    long a = max[i][j - 1] * cur;
                    long b = min[i][j - 1] * cur;
                    curMax = Math.max(curMax, Math.max(a, b));
                    curMin = Math.min(curMin, Math.min(a, b));
                    ok = true;
                }

                if (ok) {
                    reach[i][j] = true;
                    max[i][j] = curMax;
                    min[i][j] = curMin;
                }
            }
        }

        long result = max[m - 1][n - 1];
        if (!reach[m - 1][n - 1]) return -1;
        if (result < 0) return -1;
        return (int)(result % MOD);
    }
}