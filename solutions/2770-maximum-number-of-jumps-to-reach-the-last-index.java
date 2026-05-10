import java.util.*;

class Solution {
    public int maximumJumps(int[] nums, int target) {
        int n = nums.length;
        int NEG = -1_000_000_000;
        int[] dp = new int[n];
        Arrays.fill(dp, NEG);
        dp[0] = 0;
        for (int j = 1; j < n; ++j) {
            for (int i = 0; i < j; ++i) {
                if (dp[i] == NEG) continue;
                long diff = (long)nums[j] - (long)nums[i];
                if (Math.abs(diff) <= (long)target) {
                    dp[j] = Math.max(dp[j], dp[i] + 1);
                }
            }
        }
        return dp[n-1] < 0 ? -1 : dp[n-1];
    }
}