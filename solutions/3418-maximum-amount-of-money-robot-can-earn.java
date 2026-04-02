import java.util.*;

class Solution {
    public int maximumAmount(int[][] coins) {
        int m = coins.length;
        int n = coins[0].length;
        final int NEG = -1_000_000_000;
        int[][][] dp = new int[m][n][3];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                for (int k = 0; k < 3; k++) dp[i][j][k] = NEG;
            }
        }
        // initialize start cell
        dp[0][0][0] = coins[0][0];
        if (coins[0][0] < 0) {
            // can neutralize this robber using 1 neutralization
            dp[0][0][1] = 0;
        }
        // fill DP
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (i == 0 && j == 0) continue;
                int val = coins[i][j];
                for (int k = 0; k <= 2; k++) {
                    int best = NEG;
                    // from top with same k
                    if (i > 0 && dp[i-1][j][k] > NEG) {
                        best = Math.max(best, dp[i-1][j][k] + val);
                    }
                    // from left with same k
                    if (j > 0 && dp[i][j-1][k] > NEG) {
                        best = Math.max(best, dp[i][j-1][k] + val);
                    }
                    // use a neutralization at this cell (only if it's a robber)
                    if (k >= 1 && val < 0) {
                        if (i > 0 && dp[i-1][j][k-1] > NEG) {
                            best = Math.max(best, dp[i-1][j][k-1]);
                        }
                        if (j > 0 && dp[i][j-1][k-1] > NEG) {
                            best = Math.max(best, dp[i][j-1][k-1]);
                        }
                    }
                    dp[i][j][k] = best;
                }
            }
        }
        int ans = Math.max(dp[m-1][n-1][0], Math.max(dp[m-1][n-1][1], dp[m-1][n-1][2]));
        return ans;
    }
}