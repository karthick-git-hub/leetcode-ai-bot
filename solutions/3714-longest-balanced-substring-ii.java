import java.util.*;
class Solution {
    public int longestBalanced(String s) {
        int n = s.length();
        if (n == 0) return 0;
        int ans = 1; // at least one char is balanced
        
        // k = 1: longest run of same character
        int run = 1;
        for (int i = 1; i < n; ++i) {
            if (s.charAt(i) == s.charAt(i-1)) {
                run++;
            } else {
                ans = Math.max(ans, run);
                run = 1;
            }
        }
        ans = Math.max(ans, run);
        
        // k = 2: for each pair, split by the third char and find longest subarray with equal counts
        char[] letters = new char[] {'a','b','c'};
        for (int i = 0; i < 3; ++i) {
            for (int j = i+1; j < 3; ++j) {
                char x = letters[i], y = letters[j];
                char third = 'a';
                for (char c : letters) if (c != x && c != y) third = c;
                int idx = 0;
                while (idx < n) {
                    // skip third letters
                    while (idx < n && s.charAt(idx) == third) idx++;
                    if (idx >= n) break;
                    int start = idx;
                    while (idx < n && s.charAt(idx) != third) idx++;
                    int len = idx - start;
                    // process segment s[start .. idx-1] containing only x and y
                    Map<Integer, Integer> first = new HashMap<>();
                    first.put(0, 0);
                    int pref = 0;
                    for (int t = 0; t < len; ++t) {
                        char ch = s.charAt(start + t);
                        if (ch == x) pref += 1;
                        else pref -= 1; // must be y
                        if (first.containsKey(pref)) {
                            int prev = first.get(pref);
                            ans = Math.max(ans, t+1 - prev);
                        } else {
                            first.put(pref, t+1);
                        }
                    }
                }
            }
        }
        
        // k = 3: use pair of differences (ca-cb, ca-cc)
        Map<Long, Integer> firstMap = new HashMap<>();
        int ca = 0, cb = 0, cc = 0;
        // prefix before any char
        int offset = n; // to make differences non-negative
        long key0 = (((long)(0 + offset)) << 32) | ((0 + offset) & 0xffffffffL);
        firstMap.put(key0, 0);
        for (int i = 0; i < n; ++i) {
            char ch = s.charAt(i);
            if (ch == 'a') ca++;
            else if (ch == 'b') cb++;
            else cc++;
            int da = ca - cb;
            int db = ca - cc;
            long key = (((long)(da + offset)) << 32) | ((db + offset) & 0xffffffffL);
            if (firstMap.containsKey(key)) {
                int prev = firstMap.get(key);
                ans = Math.max(ans, i+1 - prev);
            } else {
                firstMap.put(key, i+1);
            }
        }
        
        return ans;
    }
}