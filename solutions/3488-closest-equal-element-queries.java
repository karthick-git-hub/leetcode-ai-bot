import java.util.*;

class Solution {
    public List<Integer> solveQueries(int[] nums, int[] queries) {
        int n = nums.length;
        Map<Integer, List<Integer>> posMap = new HashMap<>();
        for (int i = 0; i < n; i++) {
            int v = nums[i];
            List<Integer> list = posMap.get(v);
            if (list == null) {
                list = new ArrayList<>();
                posMap.put(v, list);
            }
            list.add(i);
        }
        List<Integer> ans = new ArrayList<>(queries.length);
        for (int q : queries) {
            int val = nums[q];
            List<Integer> list = posMap.get(val);
            if (list == null || list.size() == 1) {
                ans.add(-1);
                continue;
            }
            // find position of q in list (list is in ascending order because we filled by increasing i)
            int pos = Collections.binarySearch(list, q);
            // pos should be >= 0
            int size = list.size();
            int succIndex = (pos + 1) % size;
            int predIndex = (pos - 1 + size) % size;
            int succ = list.get(succIndex);
            int pred = list.get(predIndex);
            int forward = (succ - q + n) % n;
            int backward = (q - pred + n) % n;
            int best = Math.min(forward, backward);
            ans.add(best);
        }
        return ans;
    }
}