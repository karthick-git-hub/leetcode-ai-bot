import java.util.*;

class Solution {
    public int maxDistance(int[] nums1, int[] nums2) {
        int n1 = nums1.length, n2 = nums2.length;
        int i = 0;
        int ans = 0;
        for (int j = 0; j < n2; j++) {
            while (i < n1 && nums1[i] > nums2[j]) {
                i++;
            }
            if (i == n1) break;
            ans = Math.max(ans, j - i);
        }
        return ans;
    }
}