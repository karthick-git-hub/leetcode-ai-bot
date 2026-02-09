// Definition for a binary tree node.
public class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;
    TreeNode() {}
    TreeNode(int val) { this.val = val; }
    TreeNode(int val, TreeNode left, TreeNode right) {
        this.val = val;
        this.left = left;
        this.right = right;
    }
}

class Solution {
    private int minVal = Integer.MAX_VALUE;
    private int maxVal = Integer.MIN_VALUE;

    public TreeNode balanceBST(TreeNode root) {
        inOrder(root);
        TreeNode head = new TreeNode(0), tail = head;
        
        while (head.left != null) {
            if (valList.get(head.left) < valList.get(tail)) {
                head.right = new TreeNode(valList.get(tail));
                tail.right = head.right;
                head = head.right;
            } else {
                tail.right = new TreeNode(valList.get(head.left));
                head.left = tail.right;
                tail = tail.right;
            }
        }

        return head.right;
    }

    private void inOrder(TreeNode node) {
        if (node == null)
            return;
        inOrder(node.left);
        valList.add(node.val);
        minVal = Math.min(minVal, node.val);
        maxVal = Math.max(maxVal, node.val);
        inOrder(node.right);
    }
}