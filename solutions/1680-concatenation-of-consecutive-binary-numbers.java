import java.util.*;

class Solution {
    public int concatenatedBinary(int n) {
        final long MOD = 1_000_000_007L;
        long res = 0;
        int bits = 0;
        for (int i = 1; i <= n; i++) {
            if ((i & (i - 1)) == 0) bits++; // i is power of two -> increase bit length
            res = ((res << bits) + i) % MOD;
        }
        return (int) res;
    }
}