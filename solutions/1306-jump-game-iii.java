import java.util.*;

class Solution {
    public boolean canReach(int[] arr, int start) {
        int n = arr.length;
        if (start < 0 || start >= n) return false;
        boolean[] visited = new boolean[n];
        ArrayDeque<Integer> q = new ArrayDeque<>();
        q.add(start);
        visited[start] = true;
        while (!q.isEmpty()) {
            int i = q.poll();
            if (arr[i] == 0) return true;
            int[] next = new int[] { i + arr[i], i - arr[i] };
            for (int ni : next) {
                if (ni >= 0 && ni < n && !visited[ni]) {
                    visited[ni] = true;
                    q.add(ni);
                }
            }
        }
        return false;
    }
}