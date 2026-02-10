public class Solution {
    public int findLongestSubarray(int[] nums) {
        int maxLen = 0;
        int n = nums.length;
        int lastEven = -1; // stores the index of the last seen even number in evens
        int lastOdd = -1;   // stores the index of the last seen odd number in odds

        for (int i = 0; i < n; i++) {
            if (nums[i] % 2 == 0) {
                nums[i] %= 2;
                while (lastEven != -1 && evens[lastEven] != nums[i]) {
                    lastEven = evens[lastEven];
                }
                lastEven = Math.max(lastEven, i);

                int j = i + 1;

                if (lastOdd == -1 || evens[lastEven] == nums[j % n]) {
                    while (lastOdd != -1 && odds[lastOdd] != nums[(j-1) % n]) {
                        lastOdd = odds[lastOdd];
                    }
                    lastOdd = Math.max(lastOdd, i);
                } else {
                    maxLen = Math.max(maxLen, i - lastEven + 1);
                    j++;
                }

                evens[i] = nums[i];
            } else {
                while (lastEven != -1 && odds[lastOdd] != nums[i]) {
                    lastOdd = odds[lastOdd];
                }
                lastOdd = Math.max(lastOdd, i);

                int j = i + 1;

                if (lastEven == -1 || odds[lastOdd] == nums[j % n]) {
                    while (lastEven != -1 && evens[lastEven] != nums[(j-1) % n]) {
                        lastEven = evens[lastEven];
                    }
                    lastEven = Math.max(lastEven, i);
                } else {
                    maxLen = Math.max(maxLen, i - lastOdd + 1);
                    j++;
                }

                odds[i] = nums[i];
            }
        }
        
        return maxLen;
    }

    private final int[] evens = new int[1500];
    private final int[] odds   = new int[1500];
}