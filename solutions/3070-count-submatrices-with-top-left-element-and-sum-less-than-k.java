import java.util.*;

class Solution {
    public int countSubmatrices(int[][] grid, int k) {
        int m = grid.length;
        if (m == 0) return 0;
        int n = grid[0].length;
        long K = k;
        long[][] pref = new long[m][n];
        int count = 0;
        for (int i = 0; i < m; i++) {
            long rowSum = 0;
            for (int j = 0; j < n; j++) {
                rowSum += grid[i][j];
                long above = (i > 0) ? pref[i-1][j] : 0L;
                pref[i][j] = rowSum + above;
                if (pref[i][j] <= K) count++;
            }
        }
        return count;
    }
}