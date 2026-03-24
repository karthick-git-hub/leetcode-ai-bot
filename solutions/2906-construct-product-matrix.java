import java.util.*;

class Solution {
    public int[][] constructProductMatrix(int[][] grid) {
        final int MOD = 12345;
        int n = grid.length;
        int m = grid[0].length;
        int L = n * m;
        int[] flat = new int[L];
        int idx = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                flat[idx++] = grid[i][j] % MOD;
                if (flat[idx-1] < 0) flat[idx-1] += MOD;
            }
        }
        int[] pre = new int[L + 1];
        int[] suf = new int[L + 1];
        pre[0] = 1;
        for (int i = 0; i < L; i++) {
            pre[i + 1] = (int)((pre[i] * 1L * flat[i]) % MOD);
        }
        suf[L] = 1;
        for (int i = L - 1; i >= 0; i--) {
            suf[i] = (int)((suf[i + 1] * 1L * flat[i]) % MOD);
        }
        int[][] ans = new int[n][m];
        for (int k = 0; k < L; k++) {
            int val = (int)((pre[k] * 1L * suf[k + 1]) % MOD);
            ans[k / m][k % m] = val;
        }
        return ans;
    }
}