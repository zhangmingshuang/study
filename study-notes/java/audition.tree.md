# 面试　－　树结构
- 红黑树

- 红黑树与AVL树的区别是什么
> AVL是严格平衡树，因此在增加或者删除节点的时候，根据不同的情况，旋转的次数要比红黑树多。所以，红黑树的插入效率更高，但是红黑树的查找效率会比AVL树略微逊色
> AVL的平衡条件是，每个节点的左子树和右子树的高度最多差１

- AVL树的定义
    1. 它的左子树和右子树都是AVL树
    2. 左子树和右子树的高度差不能超过1

- 红黑树的定义
    1. 节点是红色或黑色
    2. 根节点是黑色
    3. 每个叶节点(NIL节点,空节点)是黑色的
    4. 每个红色节点的两个子节点都是黑色的。(从每个叶子到根的所有路径上不能有两个连续的红色节点)
    5. 从任一节点到其每个叶子的所有路径都包含相同数目的黑色节点
