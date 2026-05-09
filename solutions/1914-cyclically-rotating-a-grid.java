import java.util.*;

class Solution {
    public int[][] rotateGrid(int[][] grid, int k) {
        int m = grid.length;
        int n = grid[0].length;
        int layers = Math.min(m, n) / 2;
        for (int layer = 0; layer < layers; layer++) {
            int top = layer, left = layer, bottom = m - 1 - layer, right = n - 1 - layer;
            List<Integer> vals = new ArrayList<>();
            // top row
            for (int j = left; j <= right; j++) vals.add(grid[top][j]);
            // right column
            for (int i = top + 1; i <= bottom; i++) vals.add(grid[i][right]);
            // bottom row
            for (int j = right - 1; j >= left; j--) vals.add(grid[bottom][j]);
            // left column
            for (int i = bottom - 1; i > top; i--) vals.add(grid[i][left]);

            int len = vals.size();
            int rot = k % len;
            if (rot == 0) continue;

            int[] arr = new int[len];
            for (int i = 0; i < len; i++) arr[i] = vals.get(i);
            int[] rotated = new int[len];
            for (int i = 0; i < len; i++) {
                rotated[i] = arr[(i + rot) % len];
            }

            int idx = 0;
            // write back top row
            for (int j = left; j <= right; j++) grid[top][j] = rotated[idx++];
            // right column
            for (int i = top + 1; i <= bottom; i++) grid[i][right] = rotated[idx++];
            // bottom row
            for (int j = right - 1; j >= left; j--) grid[bottom][j] = rotated[idx++];
            // left column
            for (int i = bottom - 1; i > top; i--) grid[i][left] = rotated[idx++];
        }
        return grid;
    }
}