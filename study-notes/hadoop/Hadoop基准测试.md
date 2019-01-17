# 基准测试
为了获得最佳评测结果，最好不要在运行基准评测程序时还同时运行其他任务。
实际上，在集群入役之前进行评测最为合适。

- 获取文件列表和说明文档：

  `% hadoop jar $HADOOP_HOME/share/hadoop/mapreduce/hadoop-mapreduce-*-tests.jar`

- TeraSort评测

  测评分为三步：
    - 生成随机数据
    - 执行排序
    - 验证结果

  首先，使用`teragen`生成随机数据（可以在示例JAR文件中找到，而不是测试用JAR文件）
  ```
  % hadoop jar \
  $HADOOP_HOME/share/hadoop/mapreduce/hadoop-mapreduce-examples-*.jar \ teragen
   -Dmapreduce.job.maps=1000 10t random-data
  ```
  > 第行 100字节长， 这样使用 1000 个map任务可生成 1TB的数据（10t是10 trillion的缩写）

  接下来，运行 `TeraSort`

  ```
  $ hadoop jar \
  $HADOOP_HOME/share/hadoop/mapreduce/hadoop-mapreduce-examples-*.jar \ terasort
  random-data sorted-data
  ```

  可以通过`Web`界面（`http://resource-manager-host:8088/`)来观察作业的进度

  最后，验证在`sorted-data`文件中的数据是否已经排序好

  ```
  $ hadoop jar \
  $HADOOP_HOME/share/hadoop/mapreduce/hadoop-mapreduce-examples-*.jar \
  teravalidate sorted-data report
  ```
  该命令运行了一个小的`MapReduce`作业，对排序后的数据执行一系列检查，以验证排序结果是否正确。任务错误都可以在输出文件`report/part-r-00000`中找到

- 常用基准评测程序：
  - `TestDFSIO` 主要用于测试`HDFS`的`I/O`性能。 该程序使用一个`MapReduce`作业作为并行读/写文件的一种便捷途径。
  - `MRBench`(使用`mrbench`)会多次运行一个小型作业。与`TeraSort`相互映衬，该基准的主要目的是检验小型作业能否快速响应。
  - `NNBench`(使用`nnbench`)测试`namenode`硬件的加载过程
  - `Gridmix`是一个基准评测程序套装。 通过模拟一些真实常见的数据访问模式，`Gridmix`能逼真地为一个集群的负载建模。（`建议`）
  - `SWIM(Statistical Workload Injector for MapReduce)`，是一个真实的`MapReduce`工作负载库，可以用来为被测系统生成代表性的测试负载。（`建议`）
  - `TPCx-HS`，基于`TeraSort`的基准评测程序
