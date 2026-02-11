import java.util.*;

class Solution {
    int n;
    int[] minv, maxv, lazy;
    int N;
    void build(int node, int l, int r) {
        minv[node] = 0;
        maxv[node] = 0;
        lazy[node] = 0;
        if (l == r) return;
        int mid = (l + r) >>> 1;
        build(node<<1, l, mid);
        build(node<<1|1, mid+1, r);
    }
    void apply(int node, int val) {
        minv[node] += val;
        maxv[node] += val;
        lazy[node] += val;
    }
    void push(int node) {
        int v = lazy[node];
        if (v != 0) {
            apply(node<<1, v);
            apply(node<<1|1, v);
            lazy[node] = 0;
        }
    }
    void rangeAdd(int node, int l, int r, int ql, int qr, int val) {
        if (ql > r || qr < l) return;
        if (ql <= l && r <= qr) {
            apply(node, val);
            return;
        }
        push(node);
        int mid = (l + r) >>> 1;
        rangeAdd(node<<1, l, mid, ql, qr, val);
        rangeAdd(node<<1|1, mid+1, r, ql, qr, val);
        minv[node] = Math.min(minv[node<<1], minv[node<<1|1]);
        maxv[node] = Math.max(maxv[node<<1], maxv[node<<1|1]);
    }
    int findFirstEqual(int node, int l, int r, int ql, int qr, int target) {
        if (ql > r || qr < l) return -1;
        if (ql <= l && r <= qr) {
            if (minv[node] > target || maxv[node] < target) return -1;
            if (l == r) return l;
        }
        push(node);
        int mid = (l + r) >>> 1;
        int res = findFirstEqual(node<<1, l, mid, ql, qr, target);
        if (res != -1) return res;
        return findFirstEqual(node<<1|1, mid+1, r, ql, qr, target);
    }

    public int longestBalanced(int[] nums) {
        n = nums.length;
        if (n == 0) return 0;
        N = n;
        minv = new int[4*N];
        maxv = new int[4*N];
        lazy = new int[4*N];
        build(1, 0, N-1);
        int maxLen = 0;
        int maxVal = 100000;
        int[] lastPos = new int[maxVal + 1];
        Arrays.fill(lastPos, -1);
        int totalEven = 0, totalOdd = 0;
        for (int r = 0; r < n; r++) {
            int v = nums[r];
            int prev = lastPos[v];
            int s = (v % 2 == 0) ? 1 : -1;
            if (prev == -1) {
                // new distinct
                if (s == 1) totalEven++; else totalOdd++;
            } else {
                // remove old last occurrence sign at prev
                rangeAdd(1, 0, N-1, prev, N-1, -s);
            }
            // add sign at r
            rangeAdd(1, 0, N-1, r, N-1, s);
            lastPos[v] = r;
            int Tdiff = totalEven - totalOdd;
            int bestL = -1;
            if (Tdiff == 0) {
                bestL = 0;
            }
            int pos = findFirstEqual(1, 0, N-1, 0, r, Tdiff);
            if (pos != -1) {
                int lCandidate = pos + 1;
                if (bestL == -1 || lCandidate < bestL) bestL = lCandidate;
            }
            if (bestL != -1) {
                int len = r - bestL + 1;
                if (len > maxLen) maxLen = len;
            }
        }
        return maxLen;
    }
}