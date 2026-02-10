import java.util.*;

public class Solution {
    public int findLongestSubarray(int[] nums) {
        if (nums == null || nums.length == 0) return 0;

        int even = new HashSet<>();
        int odd = new HashSet<>();

        int maxLen = 1;
        int left = 0, right = 0;
        
        while (right < nums.length) {
            if (nums[right] % 2 != 0) {
                odd.add(nums[right]);
            } else {
                even.add(nums[right]);
            }
            
            if ((even.size() > odd.size()) ^ (odd.size() > even.size())) {
                left++;
                
                if (nums[left-1] % 2 == 0) {
                    even.remove(nums[left-1]);
                } else {
                    odd.remove(nums[left-1]);
                }
            } else {
                maxLen = Math.max(maxLen, right - left + 1);
            }

            right++;
        }

        return maxLen;
    }
}