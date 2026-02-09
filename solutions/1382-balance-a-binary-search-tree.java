public class Solution {
    public TreeNode balanceBST(TreeNode root) {
        List<TreeNode> list = new ArrayList<>();
        inorder(root, list);
        int n = list.size();
        TreeNode mid = list((n - 1) / 2);
        return build(list.get(0), list.get(n - 1), mid);
    }

    private void inorder(TreeNode node, List<TreeNode> list) {
        if (node != null) {
            inorder(node.left, list);
            list.add(node);
            inorder(node.right, list);
        }
    }

    private TreeNode build(TreeNode left, TreeNode right, TreeNode mid) {
        if (left == null && right == null)
            return mid;
        if (left == null)
            return build(null, mid, right);
        if (right == null)
            return build(left, null, mid);

        TreeNode node = new TreeNode(0);
        List<TreeNode> leftList = new ArrayList<>();
        inorder(left, leftList);
        List<TreeNode> rightList = new ArrayList<>();
        inorder(right, rightList);

        int i = 0;
        int j = (leftList.size() - 1) / 2;

        for (; ; ) {
            if (i > rightList.size() - 1 || j < leftList.size())
                return build(leftList.get(i), rightList.get(j), node);
            if (leftList.get(i).val == rightList.get(j).val)
                i++, j++;
            else
                return build(leftList.get(i), rightList.get(j), node);
        }
    }

    static class TreeNode {
        int val;
        TreeNode left, right;

        public TreeNode(int x) {
            val = x;
        }
    }
}