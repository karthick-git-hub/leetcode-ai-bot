import java.util.*;

class Solution {
    public int longestBalanced(int[] nums) {
        int n = nums.length;
        // compute first and last positions for each value
        int maxVal = 100000; // as per constraints
        int sizeMap = maxVal + 1;
        int[] first = new int[sizeMap];
        int[] last = new int[sizeMap];
        Arrays.fill(first, -1);
        Arrays.fill(last, -1);
        for (int i = 0; i < n; ++i) {
            int v = nums[i];
            if (first[v] == -1) first[v] = i;
            last[v] = i;
        }

        // Segment tree with range add and ability to find leftmost index in a query range [ql,qr] whose value == 0
        class SegTree {
            int N;
            int[] min;
            int[] max;
            int[] lazy;
            SegTree(int n) {
                N = n;
                int size = 4 * n + 5;
                min = new int[size];
                max = new int[size];
                lazy = new int[size];
                // initially all zeros -> min=0 max=0 lazy=0
            }
            void apply(int idx, int val) {
                min[idx] += val;
                max[idx] += val;
                lazy[idx] += val;
            }
            void push(int idx) {
                int l = idx << 1, r = l | 1;
                int v = lazy[idx];
                if (v != 0) {
                    apply(l, v);
                    apply(r, v);
                    lazy[idx] = 0;
                }
            }
            void pull(int idx) {
                int l = idx << 1, r = l | 1;
                min[idx] = Math.min(min[l], min[r]);
                max[idx] = Math.max(max[l], max[r]);
            }
            void rangeAdd(int ql, int qr, int val) {
                if (ql > qr) return;
                rangeAdd(1, 0, N-1, ql, qr, val);
            }
            void rangeAdd(int idx, int l, int r, int ql, int qr, int val) {
                if (ql <= l && r <= qr) {
                    apply(idx, val);
                    return;
                }
                int mid = (l + r) >> 1;
                push(idx);
                if (ql <= mid) rangeAdd(idx<<1, l, mid, ql, qr, val);
                if (qr > mid) rangeAdd(idx<<1|1, mid+1, r, ql, qr, val);
                pull(idx);
            }
            // find leftmost index in [ql,qr] with value == 0, or -1 if none
            int findFirstZero(int ql, int qr) {
                if (ql > qr) return -1;
                return findFirstZero(1, 0, N-1, ql, qr);
            }
            int findFirstZero(int idx, int l, int r, int ql, int qr) {
                if (r < ql || l > qr) return -1;
                if (min[idx] > 0 || max[idx] < 0) return -1; // no zero possible in this node
                if (l == r) {
                    // value must be zero here
                    return l;
                }
                push(idx);
                int mid = (l + r) >> 1;
                int res = -1;
                if (ql <= mid) {
                    res = findFirstZero(idx<<1, l, mid, ql, qr);
                    if (res != -1) return res;
                }
                if (qr > mid) {
                    res = findFirstZero(idx<<1|1, mid+1, r, ql, qr);
                }
                return res;
            }
        }

        if (n == 0) return 0;
        SegTree st = new SegTree(n);
        int ans = 0;
        for (int r = 0; r < n; ++r) {
            int v = nums[r];
            int f = first[v];
            if (f == r) {
                int lpos = last[v];
                int w = (v % 2 == 0) ? 1 : -1;
                // add w to range [0, lpos]
                st.rangeAdd(0, lpos, w);
            }
            int L = st.findFirstZero(0, r);
            if (L != -1) {
                int len = r - L + 1;
                if (len > ans) ans = len;
            }
        }
        return ans;
    }
}