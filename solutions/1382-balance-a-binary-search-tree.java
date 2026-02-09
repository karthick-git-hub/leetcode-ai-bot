class Solution {
    public TreeNode balanceBST(TreeNode root) {
        List<Integer> vals = new ArrayList<>();
        inOrder(root, vals);
        
        int start = 0;
        int end = vals.size() - 1;
        
        while (start <= end) {
            int mid = start + (end - start) / 2;
            TreeNode node = new TreeNode(vals.get(mid));
            
            node.left = balanceBST(rootLeft(vals, start, mid));
            node.right = balanceBST(rootRight(vals, mid + 1, end));
            
            return node;
        }
        
        return null;
    }
    
    private TreeNode rootLeft(List<Integer> vals, int start, int end) {
        if (start > end)
            return null;
        
        TreeNode node = new TreeNode(vals.get(start));
        start++;
        
        return node;
    }
    
    private TreeNode rootRight(List<Integer> vals, int start, int end) {
        if (end < start)
            return null;
        
        TreeNode node = new TreeNode(vals.get(end));
        end--;
        
        return node;
    }
    
    private void inOrder(TreeNode node, List<Integer> vals) {
        if (node == null)
            return;
        
        inOrder(node.left, vals);
        vals.add(node.val);
        inOrder(node.right, vals);
    }
}