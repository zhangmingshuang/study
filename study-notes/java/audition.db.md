# 面试　- 数据库

- (3大范式)[https://www.cnblogs.com/knowledgesea/p/3667395.html]
    1. 第一范式：当关系模式R的所有属性都不能在分解为更基本的数据单位时，称R是满足第一范式的，简记为1NF。满足第一范式是关系模式规范化的最低要求，否则，将有很多基本操作在这样的关系模式中实现不了。

    2. 第二范式：如果关系模式R满足第一范式，并且R得所有非主属性都完全依赖于R的每一个候选关键属性，称R满足第二范式，简记为2NF。

    3. 第三范式：设R是一个满足第一范式条件的关系模式，X是R的任意属性集，如果X非传递依赖于R的任意一个候选关键字，称R满足第三范式，简记为3NF.
    
- 3大范式白话说明
    1. 第一范式：　每一列的属性都是不可再分的，也就是说确保每一列的原子性
    2. 第二范式：　一行数据只做一件事，表数据不能出现数据重复
    3. 第三范式：　数据不能存在传递关系，即每个属性都跟主键有直接关系而不是同接关系

