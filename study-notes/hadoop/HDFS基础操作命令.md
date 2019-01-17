# 基础操作命令
- 将文件复制到HDFS：

  `% hadoop fs -copyFromLocal input/docs/quangle.txt \ hdfs://localhost/user/tom/quanlge.txt`
  > 简化： 省略主机的URI并使用默认设置，即省略`hdfs://localhost`，因为该项在`core-site.xml`中指定了
  > `% hadoop fs -copyFromLocal input/docs/quangle.txt /user/tom/quangle.txt`

- 将文件复制到本地文件系统并校验一致性

  `% hadoop fs -copyToLocal quangle.txt quangle.copy.txt`

  `% md5 input/docs/quangle.txt quangle.copy.txt`

- 创建目录
  `% hadoop fs -mkdir books`

- 文件列表
  `% hadoop fs -mkdir -ls`
