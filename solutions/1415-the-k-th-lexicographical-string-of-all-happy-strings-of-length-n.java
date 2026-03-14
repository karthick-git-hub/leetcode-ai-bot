import java.util.*;

class Solution {
    public String getHappyString(int n, int k) {
        char[] letters = new char[] {'a', 'b', 'c'};
        if (n <= 0) return "";
        int total = 3 * (1 << (n - 1)); // total happy strings of length n
        if (k > total) return "";
        StringBuilder sb = new StringBuilder();
        char prev = 0;
        for (int i = 0; i < n; i++) {
            int rem = n - i - 1;
            int block = 1 << rem; // number of completions for each valid choice at this position
            for (char c : letters) {
                if (c == prev) continue;
                if (k > block) {
                    k -= block;
                } else {
                    sb.append(c);
                    prev = c;
                    break;
                }
            }
        }
        return sb.toString();
    }
}