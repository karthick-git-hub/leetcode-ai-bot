import java.util.*;

class Solution {
    public String getHappyString(int n, int k) {
        // total number of happy strings of length n is 3 * 2^(n-1)
        if (n <= 0) return "";
        int total = 3 * (1 << (n - 1));
        if (k > total) return "";
        k -= 1; // make zero-based
        
        char[] letters = new char[] {'a','b','c'};
        StringBuilder sb = new StringBuilder();
        
        // count of strings for each choice at position i is 2^(n-i-1)
        for (int pos = 0; pos < n; pos++) {
            int rem = n - pos - 1;
            int block = (rem >= 0) ? (1 << rem) : 1;
            if (pos == 0) {
                int idx = k / block;
                sb.append(letters[idx]);
                k %= block;
            } else {
                char prev = sb.charAt(pos - 1);
                char[] cand = new char[2];
                int ci = 0;
                for (char c : letters) {
                    if (c != prev) cand[ci++] = c;
                }
                int idx = k / block; // 0 or 1
                sb.append(cand[idx]);
                k %= block;
            }
        }
        
        return sb.toString();
    }
}