import java.util.*;

class Solution {
    public class Robot {

        private int width;
        private int height;
        private int perim;
        private int cur; // current index on the perimeter path
        private int[][] positions; // positions along the perimeter in order
        private String[] dirs; // direction faced at each position

        public Robot(int width, int height) {
            this.width = width;
            this.height = height;
            this.perim = 2 * (width + height) - 4;
            this.positions = new int[this.perim][2];
            this.dirs = new String[this.perim];
            this.cur = 0;

            int idx = 0;
            // East along bottom row from (0,0) to (width-1,0)
            for (int x = 0; x <= width - 1; x++) {
                positions[idx][0] = x;
                positions[idx][1] = 0;
                dirs[idx] = "East";
                idx++;
            }
            // North along right column from (width-1,1) to (width-1,height-1)
            for (int y = 1; y <= height - 1; y++) {
                positions[idx][0] = width - 1;
                positions[idx][1] = y;
                dirs[idx] = "North";
                idx++;
            }
            // West along top row from (width-2,height-1) down to (0,height-1)
            for (int x = width - 2; x >= 0; x--) {
                positions[idx][0] = x;
                positions[idx][1] = height - 1;
                dirs[idx] = "West";
                idx++;
            }
            // South along left column from (0,height-2) down to (0,1)
            for (int y = height - 2; y >= 1; y--) {
                positions[idx][0] = 0;
                positions[idx][1] = y;
                dirs[idx] = "South";
                idx++;
            }
            // idx should equal perim
        }

        public void step(int num) {
            if (perim == 0) return;
            num %= perim;
            cur = (cur + num) % perim;
        }

        public int[] getPos() {
            return new int[] { positions[cur][0], positions[cur][1] };
        }

        public String getDir() {
            return dirs[cur];
        }
    }
}