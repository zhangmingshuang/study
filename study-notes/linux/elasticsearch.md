# elasticsearch

## yum安装
```sh
vi /etc/yum.repos.d/elasticsearch.repo
#
[elasticsearch-7.x]
name=Elasticsearch repository for 7.x packages
baseurl=https://artifacts.elastic.co/packages/7.x/yum
gpgcheck=1
gpgkey=https://artifacts.elastic.co/GPG-KEY-elasticsearch
enabled=1
autorefresh=1
type=rpm-md
#
yum install elasticsearch
```
## 查找安装目录
```sh
# yum安装默认为/usr/share/elasticsearch
rpm -qa | grep elastic
rpm -ql elasticsearch-7.4.2-1.x86_64
```
## 配置
```sh
# 默认配置目录/etc/elasticsearch/
vi /etc/elasticsearch/elasticsearch.yml 
#这两个配置的目录分别用来存放索引数据和日志，它们的默认路径位于$_ES_HOME的子文件夹内。这样有很大风险，特别是在升级Elasticsearch版本时，这些数据很可能被删除，在生产环境中可参考下面的配置

path:
  logs: /var/log/elasticsearch
  data: /var/data/elasticsearch
#另外path.data支持配置多个目录，每个目录都会用来存放数据，但是单个分片会存放在同一个目录内，多目录配置参考

path:
  data:
    - /mnt/elasticsearch_1
    - /mnt/elasticsearch_2
    - /mnt/elasticsearch_3
	
#集群名称
#默认情况下集群名为elasticsearch，为了区分不同集群，在生产环境需要进行修改。每个节点需要配置相同的集群名才能加入同一个集群中，且每个节点只能加入一个集群，要保证集群名相同，否则会加入错误的集群中。

cluster.name: test-cluster

#节点名称
##默认情况下节点名称是操作系统的主机名，在Linux下使用hostname -f可查看主机名。也可通过elasticsearch.yml 配置文件显示的配置，使可读性更好。配置示例如下
node.name: test-node

#网络地址 network.host
#默认配置下，Elasticsearch绑定的是一个环回地址127.0.0.1 ，这只适合在单机开发时使用。在正式环境中，为了保证该节点能够被其它节点找到，形成一个集群，需要设置一个非环回地址，如果在内网中部署集群，可通过ifconfig命令查看当前节点的内网ip地址。配置如下
network.host: 192.168.60.11

#服务发现种子主机 discovery.seed_hosts
#在开发环境中，服务发现主机名不需要设置，Elasticsearch默认会从本机的9300-9305端口尝试去连接其它节点，这提供了自动集群的体验，不需要任何配置。但在正式环境中，每个节点理论上都是不同的机器，这时候需要配置discovery.seed_hosts，discovery.seed_hosts可以是ip、ip:端口和域名。如果配置是ip，Elasticsearch默认会使用transport.profiles.default.port配置项的端口，该端口默认为9300；如果配置是域名，且该域名下绑定了多个ip，ES会尝试去连接多个ip。下面是配置示例
discovery.seed_hosts:
   - 192.168.1.10:9300
   - 192.168.1.11 
   - seeds.mydomain.com 
   
#初始主节点 cluster.initial_master_nodes
#当开启一个全新的集群时，会有一个集群的引导步骤，这步骤用来确定哪些节点参与第一次的主节点选举。在开发模式下，这个步骤由节点自动完成，这种模式本质上是不安全的，因为不是所有节点都适合做主节点，主节点关系到集群的稳定性。因此在生产模式下，集群第一次启动时，需要有一个适合作为主节点的节点列表，这个列表就是通过cluster.initial_master_nodes来配置，在配置中需要写出具体的节点名称，对应node.name配置项。配置示例如下
cluster.initial_master_nodes: 
   - master-node-a
   - master-node-b
   - master-node-c
   
```

## JVM
```sh
#设置堆内存容量
#默认情况下，Elasticsearch中JVM堆内存的最小值和最大值为1GB，在生产模式下，堆内存容量是非常重要的，需要确保Elasticsearch有足够的堆内存可用。我们可以在jvm.options 配置文件中，通过配置Xmx 和Xms项来决定JVM堆内存容量，配置的容量本身也取决于服务器的物理内存，Xmx 和Xms的值不超过物理内存的50%。因为Elasticsearch除了堆内存，也会有其它的操作，比如使用堆外缓冲区进行网络通信，通过操作系统的文件系统缓存来访问文件，还有JVM自身也需要一些内存。对内存容量，最大可设置接近32GB，26GB是安全值，有些系统下可到达30GB。示例配置如下
-Xms2g 
-Xmx2g 

```


## 用户与组
```sh
groupadd elasticsearch
useradd elasticsearch -g elasticsearch -p elasticsearch
chown -R elasticsearch:elasticsearch /usr/share/elasticsearch
# 注释设置对应的目录权限
# 如默认的/var/log和/var/data
```



## Q&A

Q: 禁止交换空间
Linux的交换空间机制是指，当内存资源不足时，Linux把某些页的内容转移至硬盘上的一块空间上，以释放内存空间。硬盘上的那块空间叫做交换空间(swap space)。如果不关闭swap，Elasticsearch的堆内存可能会被挤到磁盘中，垃圾回收速度会从毫秒级别变成分钟级别，导致节点的响应速度慢甚至和集群断开连接。有三种方式来避免交换空间发生

A: 
永久修改方法：/etc/fstab注释掉所有行  
临时修改方法：sudo swapoff -a




Q: max file descriptors [4096] for elasticsearch process likely too low, increase to at least [65536]

A: 
```sh
vim /etc/security/limits.conf打开limits文件
#添加或修改如下两行参数：
*        hard    nofile           65536
*        soft    nofile           65536
```

Q: max virtual memory areas vm.max_map_count [65530] likely too low, increase to at least [262144]

A:
```sh
#修改vm.max_map_count参数
#临时修改方法：
sysctl -w vm.max_map_count=262144
sysctl -p

#查看参数指令：
sysctl -a | grep "vm.max_map_count"
#注：主机重启后，参数会还原。

#永久修改方法：
vim /etc/sysctl.conf
#添加
vm.max_map_count=262144
#保存后执行
sysctl -p
```


Q: memory locking requested for elasticsearch process but memory is not locked

A:
vi /etc/security/limits.conf
* soft memlock unlimited 
* hard memlock unlimited 

Q: 交换空间
vi /etc/sysctl.conf 

vm.swappiness=0


# Kibana

## YUM安装
```sh
vi /etc/yum.repos.d/kibana.repo

[kibana-7.x]
name=Kibana repository for 7.x packages
baseurl=https://artifacts.elastic.co/packages/7.x/yum
gpgcheck=1
gpgkey=https://artifacts.elastic.co/GPG-KEY-elasticsearch
enabled=1
autorefresh=1
type=rpm-md

yum install kibana
```
## 配置
 if you installed Kibana from an archive distribution (`.tar.gz or .zip`), by default it is in `$KIBANA_HOME/config`. By default, with package distributions (`Debian or RPM`), it is in `/etc/kibana`.
 
 ```sh
 # 修改remote地址
 server.host: 0.0.0.0 
 # ES地址
 elasticsearch.hosts: [""]
 # 编码
 il8n.local: "zh-CN"
 ```

 # 用户授权
 `chown -R elasticsearch:elasticsearch /data/kibana`

 
 # 启动
 ```sh
 su elasticsearch
 ./kibana
 # Server running at http://0.0.0.0:5601 启动成功
 ```
 
 
 
