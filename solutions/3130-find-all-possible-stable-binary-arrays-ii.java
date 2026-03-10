import java.util.*;
class Solution {
    public int numberOfStableArrays(int zero, int one, int limit) {
        final int MOD = 1_000_000_007;
        int maxN = zero + one + 5;
        long[] fact = new long[maxN];
        long[] invfact = new long[maxN];
        fact[0] = 1;
        for (int i = 1; i < maxN; i++) fact[i] = fact[i-1] * i % MOD;
        invfact[maxN-1] = modInverse(fact[maxN-1], MOD);
        for (int i = maxN-2; i >= 0; i--) invfact[i] = invfact[i+1] * (i+1) % MOD;
        // comb helper
        java.util.function.BiFunction<Integer,Integer,Long> comb = (nObj, kObj) -> {
            int n = nObj;
            int k = kObj;
            if (n < 0 || k < 0 || n < k) return 0L;
            return (fact[n] * invfact[k] % MOD) * invfact[n-k] % MOD;
        };
        // compositions: number of ways to write S as 'parts' positive integers each <= limit
        java.util.function.Function<int[],Long> countCompositions = (arr) -> {
            int S = arr[0], parts = arr[1], L = arr[2];
            if (parts <= 0) return 0L;
            if (S < parts) return 0L;
            long res = 0;
            // inclusion-exclusion: sum_{j=0..parts} (-1)^j C(parts, j) * C(S - j*L -1, parts-1)
            for (int j = 0; j <= parts; j++) {
                int n = S - j * L - 1;
                int r = parts - 1;
                if (n < r) {
                    // once n < r for larger j, further j will only decrease n, but j increases; we can continue to next j
                    // just skip
                } else {
                    long term = comb.apply(parts, j) * comb.apply(n, r) % MOD;
                    if ((j & 1) == 1) res = (res - term) % MOD;
                    else res = (res + term) % MOD;
                }
            }
            if (res < 0) res += MOD;
            return res;
        };
        long ans = 0;
        int total = zero + one;
        // iterate starting bit: 0 and 1
        for (int start = 0; start <= 1; start++) {
            // k = number of blocks (alternating runs)
            for (int k = 1; k <= total; k++) {
                int b0 = (start == 0) ? (k + 1) / 2 : k / 2;
                int b1 = (start == 1) ? (k + 1) / 2 : k / 2;
                // must allocate zero into b0 parts and one into b1 parts
                if (b0 <= 0 || b1 <= 0) continue;
                if (b0 > zero || b1 > one) continue; // each part >=1
                if ((long)b0 * limit < zero) continue; // parts max too small
                if ((long)b1 * limit < one) continue;
                long ways0 = countCompositions.apply(new int[]{zero, b0, limit});
                if (ways0 == 0) continue;
                long ways1 = countCompositions.apply(new int[]{one, b1, limit});
                if (ways1 == 0) continue;
                ans = (ans + ways0 * ways1) % MOD;
            }
        }
        return (int) ans;
    }
    private long modInverse(long a, int mod) {
        return powMod(a, mod-2, mod);
    }
    private long powMod(long a, long e, int mod) {
        long r = 1;
        a %= mod;
        while (e > 0) {
            if ((e & 1) == 1) r = (r * a) % mod;
            a = (a * a) % mod;
            e >>= 1;
        }
        return r;
    }
}