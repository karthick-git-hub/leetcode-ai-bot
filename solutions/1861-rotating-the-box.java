class Solution {
    public char[][] rotateTheBox(char[][] boxGrid) {
        int m = boxGrid.length;
        int n = boxGrid[0].length;
        // Let stones fall to the right in each row (which corresponds to falling down after rotation)
        for (int i = 0; i < m; i++) {
            int write = n - 1; // position to write next stone
            for (int j = n - 1; j >= 0; j--) {
                if (boxGrid[i][j] == '*') {
                    write = j - 1;
                } else if (boxGrid[i][j] == '#') {
                    boxGrid[i][j] = '.';
                    boxGrid[i][write] = '#';
                    write--;
                }
            }
        }
        // Rotate clockwise: (i, j) -> (j, m-1-i)
        char[][] res = new char[n][m];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                res[j][m - 1 - i] = boxGrid[i][j];
            }
        }
        return res;
    }
}