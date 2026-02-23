class Solution {
    public boolean hasAllCodes(String s, int k) {
        if (s.length() < k) return false;
        int need = 1 << k;
        boolean[] seen = new boolean[need];
        int allOnes = need - 1;
        int mask = 0;
        int count = 0;
        for (int i = 0; i < s.length(); i++) {
            mask = ((mask << 1) & allOnes) | (s.charAt(i) - '0');
            if (i >= k - 1) {
                if (!seen[mask]) {
                    seen[mask] = true;
                    count++;
                    if (count == need) return true;
                }
            }
        }
        return false;
    }
}