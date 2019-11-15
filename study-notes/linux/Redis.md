# Redis 群集搭建

> 依赖于[`jemalloc`](./jemalloc.md)


## 1主2从3哨兵
主+哨兵： 192.168.1.194  
从1+哨兵：192.168.1.195  
从2+哨兵：192.168.1.196  

## 安装Redis
```sh
cd /data
mkdir redis5
tar -vxf ./env/redis-5.0.6.tar.gz -C ./redis5 --strip-components 1
cd redis5
make
make install
```

###### 安全相关
```sh
groupadd -r redis && useradd -r -g redis redis  
mkdir /data/redis5/log
chown -R redis:redis /data/redis5  
chown redis:redis /usr/local/bin/redis-*

mkdir /home/redis
chown -R redis:redis /home/redis


```  
###### 添加systemctl（可不添加）
`vi /lib/systemd/system/redis.service`  
```sh
[Unit]
Description=redis
After=network.target

[Service]
User=redis
Group=redis
Type=forking
PIDFile=/data/redis5/redis_16379.pid
ExecStart=/usr/local/bin/redis-server /data/redis5/redis_16379.conf
ExecReload=/bin/kill -s HUP $MAINPID
ExecStop=/bin/kill -s QUIT $MAINPID
PrivateTmp=true

[Install]
WantedBy=multi-user.target
```

#### 操作脚本
```sh
systemctl status redis  
systemctl start redis  
systemctl stop redis
```
###### redis用户操作脚本
###### 关闭
`ps -ef | grep redis`
`kill -s QUIT $PID`


kill -s QUIT `ps -ef | grep redis-server | grep -v "grep" | grep -v "/bin/bash" | awk '{print $2}'`
``
###### 启动
`redis-server /data/redis5/redis_16379.conf`

###### 问题与解决
-  Server can't set maximum open files to 10032 because of OS error: Operation not permitted.

      `vi /etc/security/limits.conf`  
      添加：  
      `redis soft nofile 65536`   
      `redis hard nofile 65536`  
      注释当前用户，重新进入即可
      
- WARNING you have Transparent Huge Pages (THP) support enabled in your kernel
  将
  ```
  if test -f /sys/kernel/mm/redhat_transparent_hugepage/enabled; then
     echo never > /sys/kernel/mm/redhat_transparent_hugepage/enabled
  fi
  ```
  添加到`/etc/rc.local`中

#### 配置

`daemon yes`  
####### 建议配置
> 当使用主从复制时，性能压测下，数据量会急剧增长，导致从节点需要复制的数据很大，消耗时长增加。slave没挂但被阻塞住了，比如正在loading Master发过来的RDB， Master的指令不能立刻发送给slave，就会放在output buffer中(见oll是命令数量，omem是大小)，在配置文件中有如下配置：client-output-buffer-limit slave 256mb 64mb 60， 这是说负责发数据给slave的client，如果buffer超过256m或者连续60秒超过64m，就会被立刻强行关闭。所以此时应该相应调大数值，否则就会出现很悲剧的循环：Master传输一个很大的RDB给Slave，Slave努力地装载，但还没装载完，Master对client的缓存满了，再来一次。
```sh
client-output-buffer-limit replica 0 0 0 
dir /data/redis5  
timeout 300  
pidfile /data/redis5/redis_16379.pid  
protected-mode no  
maxmemory-policy allkeys-lfu  
loglevel warning  
bind 127.0.0.1 192.168.1.194 #指定IP可访问    
requirepass 密码  
port 16379    
maxmemory 60gb  
rename-command FLUSHALL ""    
rename-command EVAL     ""    
rename-command FLUSHDB  "" 
rename-command SHUTDOWN ""  
rename-command KEYS ""
rename-command MONITOR ""  
```
#### 主节点特殊配置
```
min-slaves-to-write 1
min-slaves-max-lag 10
```
#### 从配置
replicaof 192.168.1.194 16379    
masterauth 123456  


## 哨兵
mkdir /data/redis5/sentinel

#### 配置
```
daemonize yes  
dir /data/redis5/sentinel
pidfile /data/redis5/redis-sentinel.pid     
bind 127.0.0.1 192.168.1.194
sentinel monitor mymaster 192.168.1.194 16379 2  
sentinel auth-pass mymaster 123456
> 指定多少毫秒之后 主节点没有应答哨兵sentinel 此时哨兵主观上认为主节点下线 默认30秒  

sentinel down-after-milliseconds mymaster 3000
  
# 故障转移的超时时间 failover-timeout 可以用在以下这些方面：  
# 1. 同一个sentinel对同一个master两次failover之间的间隔时间。  
# 2. 当一个slave从一个错误的master那里同步数据开始计算时间。直到slave被纠正为向正确的master那里同步数据时。  
# 3. 当想要取消一个正在进行的failover所需要的时间。  
# 4. 当进行failover时，配置所有slaves指向新的master所需的最大时间。不过，即使过了这个超时，slaves依然会被正确配置为指向master，但是就不按parallel-syncs所配置的规则来了  
# 默认三分钟
  
sentinel failover-timeout mymaster 180000

# 指定在发生failover主备切换时最多可以有多少个slave同时对新的master进行同步，  
# 这个数字越小，完成failover所需的时间就越长，  
# 但是如果这个数字越大，就意味着越多的slave因为replication而不可用。  
# 可以通过将这个值设为 1 来保证每次只有一个slave 处于不能处理命令请求的状态。  

sentinel parallel-syncs mymaster 1


# 配置当某一事件发生时所需要执行的脚本，可以通过脚本来通知管理员，例如当系统运行不正常时发邮件通知相关人员。  
# 对于脚本的运行结果有以下规则：  
# 若脚本执行后返回1，那么该脚本稍后将会被再次执行，重复次数目前默认为10  
# 若脚本执行后返回2，或者比2更高的一个返回值，脚本将不会重复执行。  
# 如果脚本在执行过程中由于收到系统中断信号被终止了，则同返回值为1时的行为相同。  
# 一个脚本的最大执行时间为60s，如果超过这个时间，脚本将会被一个SIGKILL信号终止，之后重新执行。  
# 通知型脚本:当sentinel有任何警告级别的事件发生时（比如说redis实例的主观失效和客观失效等等），将会去调用这个脚本，  
# 这时这个脚本应该通过邮件，SMS等方式去通知系统管理员关于系统不正常运行的信息。调用该脚本时，将传给脚本两个参数，  
# 一个是事件的类型，  
# 一个是事件的描述。  
# 如果sentinel.conf配置文件中配置了这个脚本路径，那么必须保证这个脚本存在于这个路径，并且是可执行的，否则sentinel无法正常启动成功。  
# 通知脚本  

# sentinel notification-script <master-name> <script-path>



# 客户端重新配置主节点参数脚本  
# 当一个master由于failover而发生改变时，这个脚本将会被调用，通知相关的客户端关于master地址已经发生改变的信息。  
# 以下参数将会在调用脚本时传给脚本:  
# <master-name> <role> <state> <from-ip> <from-port> <to-ip> <to-port>  
# 目前<state>总是“failover”,  
# <role>是“leader”或者“observer”中的一个。  
# 参数 from-ip, from-port, to-ip, to-port是用来和旧的master和新的master(即旧的slave)通信的  
# 这个脚本应该是通用的，能被多次调用，不是针对性的。  

# sentinel client-reconfig-script <master-name> <script-path>
```

~注意配置中的bind在不同的机子上需要进行修改！！~

###### 启动
`redis-server /data/redis5/sentinel.conf --sentinel`
###### 关闭
```sh
ps -ef | grep sentinel  
kill -s QUIT $PID
```



## 安装布隆过滤器
[RedisBloom](./RedisBloom.md)