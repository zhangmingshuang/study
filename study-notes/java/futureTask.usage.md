# jdk类 - FutureTask 使用

- Future Sample Usage
```java
    interface ArchiveSearcher { String search(String target); }
    class App {
      ExecutorService executor = ...
      ArchiveSearcher searcher = ...
      void showSearch(final String target)
          throws InterruptedException {
        Future<String> future
          = executor.submit(new Callable<String>() {
            public String call() {
                return searcher.search(target);
            }});
        displayOtherThings(); // do other things while searching
        try {
          displayText(future.get()); // use future
        } catch (ExecutionException ex) { cleanup(); return; }
      }
    }
```


- FutureTask Sample Usage
```java
    FutureTask<String> future =
        new FutureTask<String>(new Callable<String>() {
         public String call() {
           return searcher.search(target);
        }});
    executor.execute(future);
```
