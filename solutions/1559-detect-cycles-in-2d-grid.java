import java.util.*;

class Solution {
    public boolean containsCycle(char[][] grid) {
        int m = grid.length;
        int n = grid[0].length;
        boolean[][] visited = new boolean[m][n];
        int[][] depth = new int[m][n];
        int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};
        
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (visited[i][j]) continue;
                // iterative DFS stack entries: r, c, pr, pc
                Deque<int[]> stack = new ArrayDeque<>();
                stack.push(new int[]{i, j, -1, -1});
                visited[i][j] = true;
                depth[i][j] = 1;
                
                while (!stack.isEmpty()) {
                    int[] cur = stack.pop();
                    int r = cur[0], c = cur[1], pr = cur[2], pc = cur[3];
                    for (int[] d : dirs) {
                        int nr = r + d[0], nc = c + d[1];
                        if (nr < 0 || nr >= m || nc < 0 || nc >= n) continue;
                        if (grid[nr][nc] != grid[r][c]) continue;
                        if (nr == pr && nc == pc) continue; // don't go back to immediate parent
                        if (!visited[nr][nc]) {
                            visited[nr][nc] = true;
                            depth[nr][nc] = depth[r][c] + 1;
                            stack.push(new int[]{nr, nc, r, c});
                        } else {
                            // visited and not the immediate parent -> possible cycle
                            int cycleLen = depth[r][c] - depth[nr][nc] + 1;
                            if (cycleLen >= 4) return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}