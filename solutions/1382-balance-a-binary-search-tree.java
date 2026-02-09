```java
import java.util.*;

class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;
    TreeNode(int x) { val = x; }
}

public class Solution {

    public TreeNode balanceBST(TreeNode root) {
        List<Integer> values = inorder(root);
        return buildBST(values, 0, values.size() - 1);
    }

    private List<Integer> inorder(TreeNode node) {
        if (node == null) {
            return new ArrayList<>();
        }
        List<Integer> left = inorder(node.left);
        List<Integer> right = inorder(node.right);
        left.addAll(right);
        left.add(node.val);
        return left;
    }

    private TreeNode buildBST(List<Integer> values, int start, int end) {
        if (start > end) {
            return null;
        }
        int mid = (start + end) / 2;
        TreeNode node = new TreeNode(values.get(mid));
        node.left = buildBST(values, start, mid - 1);
        node.right = buildBST(values, mid + 1, end);
        return node;
    }
}
```
The above Java code uses an in-order traversal of the binary tree to get a sorted list of values. It then constructs a new balanced binary search tree from this sorted list using a recursive `buildBST` function. This solution has a time complexity of O(n), where n is the number of nodes in the input tree, and a space complexity of O(n) for storing the in-order traversal result.