class Solution {
    public double champagneTower(int poured, int query_row, int query_glass) {
        double[] dp = new double[query_row + 2];
        dp[0] = poured;
        for (int r = 0; r < query_row; r++) {
            double[] next = new double[query_row + 2];
            for (int j = 0; j <= r; j++) {
                if (dp[j] > 1.0) {
                    double excess = dp[j] - 1.0;
                    next[j] += excess / 2.0;
                    next[j + 1] += excess / 2.0;
                }
            }
            dp = next;
        }
        return Math.min(1.0, dp[query_glass]);
    }
}