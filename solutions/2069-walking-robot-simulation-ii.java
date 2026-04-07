import java.util.*;

class Solution {
    public class Robot {
        private int width;
        private int height;
        private int perim;
        private int pos; // steps from (0,0) along the perimeter, in [0, perim-1]
        private boolean moved; // whether any step call with num>0 has occurred

        public Robot(int width, int height) {
            this.width = width;
            this.height = height;
            // perimeter length (number of distinct edge cells along the border)
            this.perim = 2 * (width + height) - 4;
            this.pos = 0;
            this.moved = false;
        }

        public void step(int num) {
            if (perim <= 0) return;
            num %= perim;
            if (num > 0) moved = true;
            pos = (pos + num) % perim;
        }

        public int[] getPos() {
            int s1 = width - 1;
            int s2 = s1 + (height - 1);
            int s3 = s2 + s1;
            int x, y;
            if (pos <= s1) { // bottom edge, going East
                x = pos;
                y = 0;
            } else if (pos <= s2) { // right edge, going North
                x = width - 1;
                y = pos - s1;
            } else if (pos <= s3) { // top edge, going West
                x = (width - 1) - (pos - s2);
                y = height - 1;
            } else { // left edge, going South
                x = 0;
                y = (height - 1) - (pos - s3);
            }
            return new int[]{x, y};
        }

        public String getDir() {
            if (pos == 0) {
                return moved ? "South" : "East";
            }
            int s1 = width - 1;
            int s2 = s1 + (height - 1);
            int s3 = s2 + s1;
            if (pos <= s1) return "East";
            if (pos <= s2) return "North";
            if (pos <= s3) return "West";
            return "South";
        }
    }
}