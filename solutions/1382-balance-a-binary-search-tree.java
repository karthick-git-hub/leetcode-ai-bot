import java.util.*;

public class Solution {
    public TreeNode balanceBST(TreeNode root) {
        List<Integer> vals = new ArrayList<>();
        inorder(root, vals);
        
        return buildBalancedBST(vals);
    }
    
    private void inorder(TreeNode node, List<Integer> vals) {
        if (node == null)
            return;
        inorder(node.left, vals);
        vals.add(node.val);
        inorder(node.right, vals);
    }
    
    private TreeNode buildBalancedBST(List<Integer> vals) {
        if (vals.isEmpty())
            return null;
        
        int mid = vals.size() / 2;
        TreeNode root = new TreeNode(vals.get(mid));
        root.left = buildBalancedBST(Arrays.copyOfRange(vals, 0, mid));
        root.right = buildBalancedBST(Arrays.copyOfRange(vals, mid + 1, vals.size()));
        return root;