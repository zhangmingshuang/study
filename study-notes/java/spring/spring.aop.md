# Spring Aop 原理分析
- https://blog.csdn.net/yuexianchang/article/details/77018603

- Aop相关概念
    - Aspect(方面）: 一个关注点的模块化，这个关注点实现可能另外横切多个对象。
    - Joinpoint(连接点): 程序执行过程中明确的点，如方法的调用或特定的异常抛出。
    - Advice(通知):　在特定的连接点，AOP框架执行的动作，各种类型的通知包括"around","before","throws"通知。
    - Pointcut(切入点): 指定一个通知将被引发的一系列连接点的集合
    - Introduction(引入)：添加方法或字段到被通知的类
    - Target Object(目标对象): 包含连接点的对象，也被称作被通知或被代理对象
    - Aop Proxy (Aop代理): Aop框架创建的对象，包括通知
    - Weaving (织入): 组装方面来创建一个被通知对象

