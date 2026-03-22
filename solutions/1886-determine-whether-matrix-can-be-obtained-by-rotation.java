class Solution {
    public boolean findRotation(int[][] mat, int[][] target) {
        int n = mat.length;
        for (int rot = 0; rot < 4; rot++) {
            boolean ok = true;
            for (int i = 0; i < n && ok; i++) {
                for (int j = 0; j < n; j++) {
                    int val;
                    if (rot == 0) {
                        val = mat[i][j];
                    } else if (rot == 1) { // 90 degrees
                        val = mat[n - 1 - j][i];
                    } else if (rot == 2) { // 180 degrees
                        val = mat[n - 1 - i][n - 1 - j];
                    } else { // 270 degrees
                        val = mat[j][n - 1 - i];
                    }
                    if (val != target[i][j]) {
                        ok = false;
                        break;
                    }
                }
            }
            if (ok) return true;
        }
        return false;
    }
}