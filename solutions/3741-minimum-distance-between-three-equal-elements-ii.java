import java.util.*;

class Solution {
    public int minimumDistance(int[] nums) {
        Map<Integer, List<Integer>> posMap = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            int v = nums[i];
            List<Integer> list = posMap.get(v);
            if (list == null) {
                list = new ArrayList<>();
                posMap.put(v, list);
            }
            list.add(i);
        }
        int best = Integer.MAX_VALUE;
        for (List<Integer> lst : posMap.values()) {
            if (lst.size() < 3) continue;
            for (int i = 0; i + 2 < lst.size(); i++) {
                int span = lst.get(i + 2) - lst.get(i);
                int dist = span * 2;
                if (dist < best) best = dist;
            }
        }
        return best == Integer.MAX_VALUE ? -1 : best;
    }
}