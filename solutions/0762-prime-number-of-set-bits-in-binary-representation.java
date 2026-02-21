import java.util.*;

class Solution {
    public int countPrimeSetBits(int left, int right) {
        // primes up to 31 (but we only need up to ~20 for constraints)
        boolean[] isPrime = new boolean[32];
        int[] primes = {2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31};
        for (int p : primes) {
            if (p < isPrime.length) isPrime[p] = true;
        }
        
        int count = 0;
        for (int n = left; n <= right; n++) {
            int bits = Integer.bitCount(n);
            if (bits < isPrime.length && isPrime[bits]) count++;
        }
        return count;
    }
}