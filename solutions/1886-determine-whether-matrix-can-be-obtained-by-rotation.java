import java.util.*;

class Solution {
    public boolean findRotation(int[][] mat, int[][] target) {
        int n = mat.length;
        for (int r = 0; r < 4; r++) {
            boolean match = true;
            for (int i = 0; i < n && match; i++) {
                for (int j = 0; j < n; j++) {
                    int val;
                    if (r == 0) {
                        val = mat[i][j];
                    } else if (r == 1) {
                        // 90 degrees clockwise
                        val = mat[n - 1 - j][i];
                    } else if (r == 2) {
                        // 180 degrees
                        val = mat[n - 1 - i][n - 1 - j];
                    } else {
                        // 270 degrees clockwise
                        val = mat[j][n - 1 - i];
                    }
                    if (val != target[i][j]) {
                        match = false;
                        break;
                    }
                }
            }
            if (match) return true;
        }
        return false;
    }
}