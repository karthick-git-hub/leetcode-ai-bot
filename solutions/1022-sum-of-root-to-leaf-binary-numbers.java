import java.util.*;

class Solution {
    public int sumRootToLeaf(TreeNode root) {
        if (root == null) return 0;
        Deque<TreeNode> nodes = new ArrayDeque<>();
        Deque<Integer> vals = new ArrayDeque<>();
        nodes.push(root);
        vals.push(root.val);
        int sum = 0;
        while (!nodes.isEmpty()) {
            TreeNode node = nodes.pop();
            int cur = vals.pop();
            if (node.left == null && node.right == null) {
                sum += cur;
            } else {
                if (node.right != null) {
                    nodes.push(node.right);
                    vals.push((cur << 1) | node.right.val);
                }
                if (node.left != null) {
                    nodes.push(node.left);
                    vals.push((cur << 1) | node.left.val);
                }
            }
        }
        return sum;
    }
}