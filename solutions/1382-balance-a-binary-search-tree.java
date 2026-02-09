```java
/**
 * Definition for a binary tree node.
 * public class TreeNode {
 *     int val;
 *     TreeNode left;
 *     TreeNode right;
 *     TreeNode() {}
 *     TreeNode(int val) { this.val = val; }
 *     TreeNode(int val, TreeNode left, TreeNode right)
 *     { this.val = val; this.left = left; this.right = right; }
 * }
 */
public class Solution {
    private int countNodes(TreeNode root) {
        if (root == null) return 0;
        return countNodes(root.left) + countNodes(root.right) + 1;
    }

    private void inOrder(TreeNode root, TreeNode[] sortedNodes, int low, int high) {
        if (root == null) return;

        inOrder(root.left, sortedNodes, low, high);
        sortedNodes[high++] = root;
        inOrder(root.right, sortedNodes, low, high);
    }

    public TreeNode balanceBST(TreeNode root) {
        int count = countNodes(root);
        TreeNode[] nodes = new TreeNode[count];
        inOrder(root, nodes, 0, count - 1);

        TreeNode result = buildBalancedTree(nodes, 0, count - 1);
        return result;
    }

    private TreeNode buildBalancedTree(TreeNode[] nodes, int start, int end) {
        if (start > end) return null;

        int mid = (start + end) / 2;
        TreeNode root = new TreeNode(nodes[mid].val);
        root.left = buildBalancedTree(nodes, start, mid - 1);
        root.right = buildBalancedTree(nodes, mid + 1, end);

        return root;
    }
}
```

This solution first counts the number of nodes in the tree. Then it performs an in-order traversal to get a sorted array of node values. The array is then used to construct a balanced binary search tree by selecting the middle element as the root and recursively building its left and right subtrees. The function `buildBalancedTree` is used to recursively build the balanced tree from the sorted array.

This solution has a time complexity of O(n), where n is the number of nodes in the tree, because it needs to traverse the tree once for counting and another time for sorting. The space complexity is also O(n) due to the need to store the node values in an array.