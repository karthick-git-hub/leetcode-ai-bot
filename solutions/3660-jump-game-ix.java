import java.util.*;

class Solution {
    public int[] maxValue(int[] nums) {
        int n = nums.length;
        int[] prevGreater = new int[n];
        Arrays.fill(prevGreater, -1);
        Deque<Integer> st = new ArrayDeque<>();
        // nearest greater to left (strictly greater)
        for (int i = 0; i < n; i++) {
            while (!st.isEmpty() && nums[st.peekLast()] <= nums[i]) st.removeLast();
            if (!st.isEmpty()) prevGreater[i] = st.peekLast();
            st.addLast(i);
        }
        // next smaller to right (strictly smaller)
        int[] nextSmaller = new int[n];
        Arrays.fill(nextSmaller, -1);
        st.clear();
        for (int i = 0; i < n; i++) {
            while (!st.isEmpty() && nums[st.peekLast()] > nums[i]) {
                nextSmaller[st.removeLast()] = i;
            }
            st.addLast(i);
        }
        // DSU
        int[] parent = new int[n];
        int[] rank = new int[n];
        for (int i = 0; i < n; i++) parent[i] = i;
        for (int i = 0; i < n; i++) rank[i] = 1;
        java.util.function.BiConsumer<Integer, Integer> unite = new java.util.function.BiConsumer<Integer, Integer>() {
            public void accept(Integer a, Integer b) {
                int ra = find(parent, a);
                int rb = find(parent, b);
                if (ra == rb) return;
                if (rank[ra] < rank[rb]) {
                    parent[ra] = rb;
                    rank[rb] += rank[ra];
                } else {
                    parent[rb] = ra;
                    rank[ra] += rank[rb];
                }
            }
        };
        for (int i = 0; i < n; i++) {
            if (prevGreater[i] != -1) unite.accept(i, prevGreater[i]);
            if (nextSmaller[i] != -1) unite.accept(i, nextSmaller[i]);
        }
        int[] compMax = new int[n];
        Arrays.fill(compMax, Integer.MIN_VALUE);
        for (int i = 0; i < n; i++) {
            int r = find(parent, i);
            if (nums[i] > compMax[r]) compMax[r] = nums[i];
        }
        int[] ans = new int[n];
        for (int i = 0; i < n; i++) ans[i] = compMax[find(parent, i)];
        return ans;
    }
    
    private int find(int[] parent, int x) {
        if (parent[x] != x) parent[x] = find(parent, parent[x]);
        return parent[x];
    }
}