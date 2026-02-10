import java.util.*;

public class Solution {
    public int longestSubarray(int[] nums) {
        Set<Integer> evens = new HashSet<>();
        Set<Integer> odds = new HashSet<>();

        int maxLen = 0;
        int curLen = 0;

        for (int num : nums) {
            if ((num & 1) != 0) {
                odds.add(num);
            } else {
                evens.add(num);
            }

            while (!evens.isEmpty() && !odds.isEmpty()) {
                if (((nums[(int) evens.iterator().next()] & 1) == 0)) {
                    evens.remove(nums[(int) evens.iterator().next()]);
                } else {
                    odds.remove(nums[(int) odds.iterator().next()]);
                }

                curLen++;
            }

            maxLen = Math.max(maxLen, curLen);

            if ((num & 1) != 0) {
                odds.add(num);
            } else {
                evens.add(num);
            }
        }

        return maxLen;
    }
}