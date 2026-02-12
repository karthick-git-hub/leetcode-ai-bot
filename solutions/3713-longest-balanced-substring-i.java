import java.util.*;

class Solution {
    public int longestBalanced(String s) {
        int n = s.length();
        int ans = 0;
        for (int i = 0; i < n; i++) {
            int[] cnt = new int[26];
            int distinct = 0;
            for (int j = i; j < n; j++) {
                int idx = s.charAt(j) - 'a';
                if (cnt[idx] == 0) distinct++;
                cnt[idx]++;
                int len = j - i + 1;
                // quick check: total length must be divisible by number of distinct chars
                if (len % distinct != 0) continue;
                int target = len / distinct;
                boolean ok = true;
                for (int k = 0; k < 26; k++) {
                    if (cnt[k] > 0 && cnt[k] != target) {
                        ok = false;
                        break;
                    }
                }
                if (ok) ans = Math.max(ans, len);
            }
        }
        return ans;
    }
}