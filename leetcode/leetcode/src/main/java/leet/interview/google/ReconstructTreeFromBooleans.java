package leet.interview.google;

public class ReconstructTreeFromBooleans {
/*
 * 
Generic tree, aka, n-ary tree: fanout degree has no restriction, i.e., 
any node may have 0 or 1 or 2 or more children, e.g:

struct TreeNode {
  ValueType value;
  vector<TreeNode*> children;
};
Now you are given pre-order and post-order iterators that let you access nodes in the tree, and you want to build a new tree that has same structure as original tree.

interface TreeIterator {
   public:
     bool hasNext();
     void next();
     ValueType getValue();

     bool isLeaf();  // Information you should use
};
class PreOrderTreeIterator implements TreeIterator {...} class PostOrderTreeIterator implements TreeIterator {...}

Notice that, because of duplicates, you cannot rely on value to distinguish nodes. In other words, 
value is completely useless, the input is essentially two arrays of "isLeaf" booleans,
 in pre-order and post-order traversal sequence.

Can you still reconstruct the tree:

TreeNode* reconstructTree(PreOrderTreeIterator preIter, PreOrderTreeIterator postIter) {
  // Implement this
}
 */
}
