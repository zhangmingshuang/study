# java类 - FutureTask分析[^jdk1.7]

在`java`中一般通过继承`Thread`类或者`Runnable`接口这两种方式来创建多线程，但是这两种方式都有个缺陷，就是不能在执行完成后获取执行的结果， 因为`java 1.5`之后提供了`Callable`和`Future`接口，通过它们就可以在任务执行完成之后得到任务的执行结果。

- `Callable`接口

```java类

```
