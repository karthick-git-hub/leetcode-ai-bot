import java.util.*;

class Solution {
    public String makeLargestSpecial(String s) {
        int n = s.length();
        List<String> parts = new ArrayList<>();
        int count = 0;
        int start = 0;
        for (int i = 0; i < n; i++) {
            if (s.charAt(i) == '1') count++;
            else count--;
            if (count == 0) {
                // s[start..i] is a primitive special string
                String inner = s.substring(start + 1, i);
                String processedInner = makeLargestSpecial(inner);
                parts.add("1" + processedInner + "0");
                start = i + 1;
            }
        }
        Collections.sort(parts, new Comparator<String>() {
            public int compare(String a, String b) {
                return b.compareTo(a); // descending lexicographic
            }
        });
        StringBuilder sb = new StringBuilder();
        for (String p : parts) sb.append(p);
        return sb.toString();
    }
}