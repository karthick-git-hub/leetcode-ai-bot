import java.util.*;

class Solution {
    public String findTheString(int[][] lcp) {
        int n = lcp.length;
        // Basic validation of diagonal and bounds
        for (int i = 0; i < n; i++) {
            if (lcp[i][i] != n - i) return "";
        }
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                int t = lcp[i][j];
                if (t < 0 || t > n - Math.max(i, j)) return "";
            }
        }
        // DSU local class
        class DSU {
            int[] p;
            int[] r;
            DSU(int size) {
                p = new int[size];
                r = new int[size];
                for (int i = 0; i < size; i++) p[i] = i;
            }
            int find(int x) {
                while (p[x] != x) {
                    p[x] = p[p[x]];
                    x = p[x];
                }
                return x;
            }
            void union(int a, int b) {
                int ra = find(a), rb = find(b);
                if (ra == rb) return;
                if (r[ra] < r[rb]) p[ra] = rb;
                else if (r[rb] < r[ra]) p[rb] = ra;
                else { p[rb] = ra; r[ra]++; }
            }
        }
        DSU dsu = new DSU(n);
        // Validate recurrence and union positions where lcp>0
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                int t = lcp[i][j];
                if (t > 0) {
                    dsu.union(i, j);
                    if (i + 1 < n && j + 1 < n) {
                        if (lcp[i + 1][j + 1] != t - 1) return "";
                    } else {
                        if (t != 1) return "";
                    }
                } else {
                    // t == 0: no immediate equality; nothing to union
                }
            }
        }
        // Map roots to component ids
        Map<Integer, Integer> compMap = new HashMap<>();
        int[] root = new int[n];
        for (int i = 0; i < n; i++) {
            root[i] = dsu.find(i);
            if (!compMap.containsKey(root[i])) compMap.put(root[i], compMap.size());
        }
        int m = compMap.size();
        List<Set<Integer>> adj = new ArrayList<>(m);
        for (int i = 0; i < m; i++) adj.add(new HashSet<>());
        // Build adjacency (inequality) from lcp entries
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                int t = lcp[i][j];
                int maxRemain = n - Math.max(i, j);
                if (t < maxRemain) {
                    int a = i + t;
                    int b = j + t;
                    int ra = compMap.get(dsu.find(a));
                    int rb = compMap.get(dsu.find(b));
                    if (ra == rb) return "";
                    adj.get(ra).add(rb);
                    adj.get(rb).add(ra);
                }
            }
        }
        // Compute minimum index per component to determine lexicographic priority
        int[] minIndex = new int[m];
        Arrays.fill(minIndex, Integer.MAX_VALUE);
        for (int i = 0; i < n; i++) {
            int cid = compMap.get(root[i]);
            if (i < minIndex[cid]) minIndex[cid] = i;
        }
        Integer[] order = new Integer[m];
        for (int i = 0; i < m; i++) order[i] = i;
        Arrays.sort(order, (a, b) -> Integer.compare(minIndex[a], minIndex[b]));
        // Assign letters to components greedily by order
        int[] compLetter = new int[m];
        Arrays.fill(compLetter, -1);
        for (int cidIdx = 0; cidIdx < m; cidIdx++) {
            int cid = order[cidIdx];
            boolean[] used = new boolean[26];
            for (int nei : adj.get(cid)) {
                if (compLetter[nei] != -1) used[compLetter[nei]] = true;
            }
            int choose = -1;
            for (int k = 0; k < 26; k++) {
                if (!used[k]) { choose = k; break; }
            }
            if (choose == -1) return "";
            compLetter[cid] = choose;
        }
        // Build string
        char[] s = new char[n];
        for (int i = 0; i < n; i++) {
            int cid = compMap.get(root[i]);
            s[i] = (char) ('a' + compLetter[cid]);
        }
        // Verify computed lcp matches given lcp
        int[][] calc = new int[n + 1][n + 1];
        for (int i = n - 1; i >= 0; i--) {
            for (int j = n - 1; j >= 0; j--) {
                if (s[i] == s[j]) calc[i][j] = 1 + calc[i + 1][j + 1];
                else calc[i][j] = 0;
                if (calc[i][j] != lcp[i][j]) return "";
            }
        }
        return new String(s);
    }
}