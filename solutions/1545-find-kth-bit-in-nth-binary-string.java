class Solution {
    public char findKthBit(int n, int k) {
        if (n == 1) return '0';
        int mid = 1 << (n - 1); // 2^(n-1)
        if (k == mid) return '1';
        if (k < mid) return findKthBit(n - 1, k);
        int j = (1 << n) - k; // mapped position in S_{n-1}
        char c = findKthBit(n - 1, j);
        return c == '0' ? '1' : '0';
    }
}