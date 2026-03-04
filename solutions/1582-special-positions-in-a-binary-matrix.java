import java.util.*;

class Solution {
    public int numSpecial(int[][] mat) {
        int m = mat.length;
        if (m == 0) return 0;
        int n = mat[0].length;
        int[] rowSum = new int[m];
        int[] colSum = new int[n];
        for (int i = 0; i < m; i++) {
            int rs = 0;
            for (int j = 0; j < n; j++) {
                rs += mat[i][j];
            }
            rowSum[i] = rs;
        }
        for (int j = 0; j < n; j++) {
            int cs = 0;
            for (int i = 0; i < m; i++) {
                cs += mat[i][j];
            }
            colSum[j] = cs;
        }
        int count = 0;
        for (int i = 0; i < m; i++) {
            if (rowSum[i] != 1) continue;
            for (int j = 0; j < n; j++) {
                if (mat[i][j] == 1 && colSum[j] == 1) count++;
            }
        }
        return count;
    }
}