import java.util.*;

class Solution {
    public int minimumDistance(String word) {
        int n = word.length();
        if (n <= 1) return 0;
        int[] letters = new int[n];
        for (int i = 0; i < n; i++) letters[i] = word.charAt(i) - 'A';
        final int UNUSED = 26;
        final int INF = 1_000_000_000;
        int[] dpPrev = new int[27];
        Arrays.fill(dpPrev, INF);
        // After typing first character, one finger is at letters[0], other is UNUSED with cost 0.
        dpPrev[UNUSED] = 0;
        int prevPos = letters[0];
        for (int i = 1; i < n; i++) {
            int curPos = letters[i];
            int[] dpNext = new int[27];
            Arrays.fill(dpNext, INF);
            for (int j = 0; j <= 26; j++) {
                int curCost = dpPrev[j];
                if (curCost >= INF) continue;
                // Move the finger that is currently on prevPos to curPos
                int r1 = prevPos / 6, c1 = prevPos % 6;
                int r2 = curPos / 6, c2 = curPos % 6;
                int movePrevCost = Math.abs(r1 - r2) + Math.abs(c1 - c2);
                if (dpNext[j] > curCost + movePrevCost) {
                    dpNext[j] = curCost + movePrevCost;
                }
                // Move the other finger (at position j) to curPos
                int moveOtherCost;
                if (j == UNUSED) moveOtherCost = 0;
                else {
                    int rj = j / 6, cj = j % 6;
                    moveOtherCost = Math.abs(rj - r2) + Math.abs(cj - c2);
                }
                // After moving the other finger to curPos, the other finger's new position becomes prevPos
                if (dpNext[prevPos] > curCost + moveOtherCost) {
                    dpNext[prevPos] = curCost + moveOtherCost;
                }
            }
            dpPrev = dpNext;
            prevPos = curPos;
        }
        int ans = INF;
        for (int v : dpPrev) if (v < ans) ans = v;
        return ans;
    }
}