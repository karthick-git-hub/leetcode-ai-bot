import java.util.*;

class Solution {
    public int findTheLongestBalancedSubarray(int[] nums) {
        int n = nums.length;
        Map<Integer, Integer> countMap = new HashMap<>();
        Set<Integer> evenSet = new HashSet<>();
        Set<Integer> oddSet = new HashSet<>();
        int maxLength = 0;
        
        for (int i = 0; i < n; i++) {
            if (nums[i] % 2 == 0) {
                evenSet.add(nums[i]);
                countMap.put(nums[i], countMap.getOrDefault(nums[i], 0) + 1);
            } else {
                oddSet.add(nums[i]);
                countMap.put(nums[i], countMap.getOrDefault(nums[i], 0) + 1);
            }
            
            while (evenSet.size() > oddSet.size()) {
                int left = nums[i - maxLength];
                if (left % 2 == 0) {
                    evenSet.remove(left);
                } else {
                    oddSet.remove(left);
                }
                countMap.put(left, countMap.get(left) - 1);
                if (countMap.get(left) == 0) {
                    countMap.remove(left);
                }
            }
            
            maxLength = Math.max(maxLength, evenSet.size());
        }
        
        return maxLength * 2;
    }
}