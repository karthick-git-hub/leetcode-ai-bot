import java.util.*;

class Solution {
    public int minimumDistance(int[] nums) {
        Map<Integer, List<Integer>> pos = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            pos.computeIfAbsent(nums[i], k -> new ArrayList<>()).add(i);
        }
        int bestSpan = Integer.MAX_VALUE;
        for (List<Integer> lst : pos.values()) {
            if (lst.size() < 3) continue;
            for (int i = 2; i < lst.size(); i++) {
                int span = lst.get(i) - lst.get(i - 2);
                if (span < bestSpan) bestSpan = span;
            }
        }
        if (bestSpan == Integer.MAX_VALUE) return -1;
        return 2 * bestSpan;
    }
}