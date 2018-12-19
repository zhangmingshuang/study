# java类 - Executors解析(基于jdk1.8)

- #callable

```java
    /**
     * Returns a {@link Callable} object that, when
     * called, runs the given task and returns the given result.  This
     * can be useful when applying methods requiring a
     * {@code Callable} to an otherwise resultless action.
     * @param task the task to run
     * @param result the result to return
     * @param <T> the type of the result
     * @return a callable object
     * @throws NullPointerException if task null
     */
    public static <T> Callable<T> callable(Runnable task, T result) {
        if (task == null)
            throw new NullPointerException();
        //使用适配器模式，生成callable
        return new RunnableAdapter<T>(task, result);
    }
```

- :cow: RunableAdapter

```java
    /**
    * A callable that runs given task and returns given result
    */
   static final class RunnableAdapter<T> implements Callable<T> {
       final Runnable task;
       final T result;
       RunnableAdapter(Runnable task, T result) {
           this.task = task;
           this.result = result;
       }
       public T call() {
           //特别注意， runable能转换成callable的关键， 使用的raunable的run方法，即同步执行，而不是start方法
           task.run();
           return result;
       }
   }
```
