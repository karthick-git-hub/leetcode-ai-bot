class Solution {
    public boolean checkOnesSegment(String s) {
        boolean inOne = false;
        boolean seenZeroAfterOne = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '1') {
                if (seenZeroAfterOne) return false;
                inOne = true;
            } else { // c == '0'
                if (inOne) seenZeroAfterOne = true;
                inOne = false;
            }
        }
        return true;
    }
}