class Solution {
    public boolean checkOnesSegment(String s) {
        boolean seenOne = false;
        boolean seenZeroAfterOnes = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '1') {
                if (seenZeroAfterOnes) return false;
                seenOne = true;
            } else { // c == '0'
                if (seenOne) seenZeroAfterOnes = true;
            }
        }
        return true;
    }
}