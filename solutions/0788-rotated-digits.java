class Solution {
    public int rotatedDigits(int n) {
        int count = 0;
        for (int i = 1; i <= n; i++) {
            String s = String.valueOf(i);
            boolean valid = true;
            boolean hasDifferent = false;
            for (int j = 0; j < s.length(); j++) {
                char c = s.charAt(j);
                if (c == '3' || c == '4' || c == '7') {
                    valid = false;
                    break;
                }
                if (c == '2' || c == '5' || c == '6' || c == '9') {
                    hasDifferent = true;
                }
                // 0,1,8 are valid but do not make it different
            }
            if (valid && hasDifferent) count++;
        }
        return count;
    }
}