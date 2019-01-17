# Hadoop守护进程的关键属性

- 访问正在运行中的实际配置 `http://resource-manager-host:8088/conf`

- 典型的`core-site.xml`配置
```xml
<configuration>
    <property>
        <!--
        默认文件系统， URI定义主机名称和namenode的RPC服务器工作的端口号，默认值是8020
        -->
        <name>fs.defaultFS</name>
        <value>hdfs://namenode/</value>
    </property>

    <property>
        <!--
        缓冲区大小， 默认4KB（4096字节），增大缓存区容量可以显示提高性能，例如：128K(131072字节)
        单位： 字节
        -->
        <name>io.file.buffer.size</name>
        <value>4096</value>
    </property>

    <property>
        <!--
        回收站保留时间设置，单位分钟，如果设置为0表示回收站这个特性没有被启动
        Hadoop的回收站是用户级特性，换句话说，只有由文件系统shell直接删除的文件才会被放到回收站中
        用程序删除的文件会被直接删除
        当然也有例外： 如使用Trash类，构造一个Trash实例，调用 #moveToTrash()方法会把指定路径的文件移到回收站中
        如果操作成功，返回一个值，否则，如果回收站未被启动，或该文件已经在回收站中，该方法返回false

        当回收站特性被启用时，每个用户都有独立的回收站目录，即hmoe目录下的.Trash目录。
        恢复文件也很简单： 在.Trash的子目录中找到文件，交将其移出.Trash目录
        HDFS会自动删除回收站中的文件，但是其他文件系统并不具备这项功能。
        对于这些文件系统，必须定期手动删除
        % hadoop fs -expunge
        Trash类的 #expunge()方法也具有相同的效果。
        -->
        <name>fs.trash.interval</name>
        <value>0</value>
    </property>
</configuration>
```

- 典型的`hdfs-site.xml`配置
```xml
<configuration>
    <property>
        <!--
        HDFS块大小， 默认128MB， 可以设置为 256MB（268435456字节）以降低namenode的内存压力，并向mapper传输更多数据
        -->
        <name>dfs.blocksize</name>
        <value>131072</value>
    </property>
    <property>
        <name>dfs.namenode.name.dir</name>
        <!--
        指定一系列目录来供 namenode 存储永久性的文件系统元数据(编辑日志和文件系统映像)
        通常配置一两个本地磁盘和一个远程磁盘（NFS挂载的目录）之中
        这样，即使本地磁盘发生故障，甚至整个namenode发生故障，都可以恢复元数据文件并且重构新的namenode. （辅助namenode只是定期保存namenode的检查点，不维护namenode的最新备份）
        -->
        <value>/disk1/hdfs/name,/remote/hdfs/name</value>
    </property>

    <property>
        <name>dfs.datanode.data.dir</name>
        <!--
        设定datanode存储数据块的目录列表
        目的是使datanode循环地在各个目录中写数据
        为了提高性能，最好分别为各个本地磁盘指定一个存储目录，这样一来，数据块跨磁盘分布，针对不同数据块的读操作可以并发执行，从而提升读性能。

        PS: 为了充分发挥性能，需要使用 noatime 选项挂载磁盘。 该选项意味着执行读操作时，所读文件的最近访问时间信息并不刷新，从而显著提升性能。

        默认情况下，datanode能够使用存储目录所有闲置空间
        如果计划部份空间留给其他应用（非HDFS）
        则需要设置 dfs.dtanode.du.reserved属性来指定待保留的空间大小（单位：字节）
        -->
        <value>/disk1/hdfs/data,/disk2/hdfs/data</value>
    </property>

    <property>
        <name>dfs.namenode.checkpoint.dir</name>
        <!--
        辅助namenode存储文件系统的检查点的目录
        与namenode类似，检查点映像文件会分别存储在各个目录之中， 以支持冗余备份。
        -->
        <value>/disk1/hdfs/namesecondary,/disk2/hdfs/namesecondary</value>
    </property>
</configuration>
```
> 在默认情况下，HDFS的存储目录放在`Hadoop`的临时目录（通过hadoop.tmp.dir属性配置，默认值是`/tmp/hadoop-${user.name}`）。 因此，正确的设置这些属性的重要性在于，即使清除了系统的临时目录。数据也不会丢失。

- 典型的`yarn-site.xml`配置
```xml
<configuration>
    <property>
        <!--
        运行资源管理器的机器的主机名或IP地址
        -->
        <name>yarn.resourcemanager.hostname</name>
        <value>0.0.0.0</value>
    </property>
    <!--
    <property>

        运行资源管理器的RPC服务器的主机名和端口

        <name>yarn.resourcemanager.address</name>
        <value>0.0.0.0:8032</value>
    </property>
    -->
    <property>
        <!--
        窗口本地临时存储空间，需要一定的足够容量
        -->
        <name>yarn.nodemanager.local-dirs</name>
        <value>/disk1/nm-local-dir,/disk2/nm-local-dir</value>
    </property>

    <property>
        <!--
        节点管理器运行的附加服务列表
        shuffle句柄是长期运行于节点管理器的附加服务
        -->
        <name>yarn.nodemanager.aux-services</name>
        <value>mapreduce.shuffle</value>
    </property>

    <property>
        <!--
        节点管理器运行的容器可以分配到的物理内存地址（单位：MB）
        -->
        <name>yarn.nodemanager.resource.memory-mb</name>
        <value>16384</value>
    </property>

    <property>
        <!-- 节点管理器运行的容器可以分配到的CPU核数目 -->
        <name>yarn.nodemanager.resource.cpu-vcores</name>
        <value>16</value>
    </property>
</configuration>
```
