import java.util.*;

class Solution {
    public int longestBalanced(int[] nums) {
        int n = nums.length;
        if (n == 0) return 0;
        int maxVal = 100000;
        int[] freq = new int[maxVal + 1];
        int[] seenTick = new int[maxVal + 1];
        int tick = 1;
        int best = 0;
        for (int l = 0; l < n; l++) {
            if (n - l <= best) break; // cannot beat current best
            int distinctEven = 0, distinctOdd = 0;
            // increment tick to avoid clearing arrays
            tick++;
            for (int r = l; r < n; r++) {
                int v = nums[r];
                if (seenTick[v] != tick) {
                    seenTick[v] = tick;
                    freq[v] = 0;
                }
                if (freq[v] == 0) {
                    if ((v & 1) == 0) distinctEven++;
                    else distinctOdd++;
                }
                freq[v]++;
                if (distinctEven == distinctOdd) {
                    int len = r - l + 1;
                    if (len > best) best = len;
                }
                // small optimization: if remaining possible length cannot exceed best, break early
                if (n - l <= best) break;
            }
        }
        return best;
    }
}