import java.util.*;

class Solution {

    public class Robot {
        int width;
        int height;
        List<int[]> path = new ArrayList<>();
        int perimLen;
        int idx = 0; // current index on path
        long stepsTaken = 0;

        public Robot(int width, int height) {
            this.width = width;
            this.height = height;
            // build perimeter path in CCW order starting from (0,0) moving East
            // bottom edge (0,0) to (w-1,0)
            for (int x = 0; x <= width - 1; x++) {
                path.add(new int[]{x, 0});
            }
            // right edge (w-1,1) to (w-1,h-1)
            for (int y = 1; y <= height - 1; y++) {
                path.add(new int[]{width - 1, y});
            }
            // top edge (w-2,h-1) to (0,h-1) if height>1
            for (int x = width - 2; x >= 0; x--) {
                path.add(new int[]{x, height - 1});
            }
            // left edge (0,h-2) to (0,1) if width>1
            for (int y = height - 2; y >= 1; y--) {
                path.add(new int[]{0, y});
            }
            perimLen = path.size();
            // per constraints width,height >= 2 so perimLen >= 4
            idx = 0;
            stepsTaken = 0;
        }

        public void step(int num) {
            if (perimLen == 0) return;
            stepsTaken += num;
            idx = (int)(stepsTaken % perimLen);
        }

        public int[] getPos() {
            if (perimLen == 0) return new int[]{0, 0};
            int[] p = path.get(idx);
            return new int[]{p[0], p[1]};
        }

        public String getDir() {
            if (perimLen == 0) return "East";
            if (stepsTaken == 0) {
                return "East";
            }
            int prev = (idx - 1 + perimLen) % perimLen;
            int[] a = path.get(prev);
            int[] b = path.get(idx);
            if (b[0] > a[0]) return "East";
            if (b[0] < a[0]) return "West";
            if (b[1] > a[1]) return "North";
            return "South";
        }
    }
}