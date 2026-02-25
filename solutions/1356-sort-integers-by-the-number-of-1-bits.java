import java.util.*;

class Solution {
    public int[] sortByBits(int[] arr) {
        Integer[] a = new Integer[arr.length];
        for (int i = 0; i < arr.length; i++) {
            a[i] = arr[i];
        }
        Arrays.sort(a, new Comparator<Integer>() {
            public int compare(Integer x, Integer y) {
                int bx = Integer.bitCount(x);
                int by = Integer.bitCount(y);
                if (bx != by) return bx - by;
                return x - y;
            }
        });
        for (int i = 0; i < arr.length; i++) {
            arr[i] = a[i];
        }
        return arr;
    }
}