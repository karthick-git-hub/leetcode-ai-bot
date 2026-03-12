import java.util.*;

class Solution {
    public int maxStability(int n, int[][] edges, int k) {
        // Find maximum s to bound binary search
        int maxS = 0;
        for (int[] e : edges) {
            maxS = Math.max(maxS, e[2]);
        }
        int hi = maxS * 2;
        if (hi == 0) return -1; // no edges? but constraints say edges.length>=1

        // Local DSU class
        class DSU {
            int[] p;
            int[] r;
            int comp;
            DSU(int n) {
                p = new int[n];
                r = new int[n];
                for (int i = 0; i < n; i++) {
                    p[i] = i;
                    r[i] = 0;
                }
                comp = n;
            }
            int find(int x) {
                while (p[x] != x) {
                    p[x] = p[p[x]];
                    x = p[x];
                }
                return x;
            }
            boolean union(int a, int b) {
                int pa = find(a);
                int pb = find(b);
                if (pa == pb) return false;
                if (r[pa] < r[pb]) {
                    p[pa] = pb;
                } else if (r[pb] < r[pa]) {
                    p[pb] = pa;
                } else {
                    p[pb] = pa;
                    r[pa]++;
                }
                comp--;
                return true;
            }
        }

        // Feasibility checker for given threshold T
        class Checker {
            boolean can(int T) {
                DSU dsu = new DSU(n);
                // 1) Add must edges (musti == 1). They cannot be upgraded.
                for (int[] e : edges) {
                    int u = e[0], v = e[1], s = e[2], must = e[3];
                    if (must == 1) {
                        if (s < T) return false; // must edge too weak
                        // If it creates a cycle among must edges -> invalid
                        if (!dsu.union(u, v)) return false;
                    }
                }
                if (dsu.comp == 1) return true;

                // 2) Use optional edges with s >= T (cost 0)
                for (int[] e : edges) {
                    int u = e[0], v = e[1], s = e[2], must = e[3];
                    if (must == 0 && s >= T) {
                        dsu.union(u, v);
                        if (dsu.comp == 1) return true;
                    }
                }

                // 3) Use optional edges that need upgrade: s < T but 2*s >= T (cost 1)
                int usedUpgrades = 0;
                for (int[] e : edges) {
                    int u = e[0], v = e[1], s = e[2], must = e[3];
                    if (must == 0 && s < T && 2L * s >= T) {
                        if (dsu.union(u, v)) {
                            usedUpgrades++;
                            if (usedUpgrades > k) return false;
                            if (dsu.comp == 1) return true;
                        }
                    }
                }
                return dsu.comp == 1 && usedUpgrades <= k;
            }
        }

        Checker checker = new Checker();
        // If impossible even for T = 1, return -1
        if (!checker.can(1)) return -1;

        int lo = 1;
        while (lo < hi) {
            int mid = lo + (hi - lo + 1) / 2;
            if (checker.can(mid)) {
                lo = mid;
            } else {
                hi = mid - 1;
            }
        }
        return lo;
    }
}