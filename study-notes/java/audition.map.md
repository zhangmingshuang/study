# 面试 - Map面试相关

- jdk8中HashMap的数据结构
> 采用数组　+ 链表
> 数组是一组连续的内存空间，　易查询，　不易增删
> 链表是不连续的内存空间，通过节点相互连接，易删除，不易查询
> 在jdk8中，为解决hash碰撞过于频繁，而链表查询效率过低，当链表长度达到一定值（默认8），将链表转成红黑树


