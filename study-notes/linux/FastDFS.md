#FastDFS


## 部署说明
192.168.1.195  
192.168.1.196  

所有机器都安装Tracker+Storage+Tengine+NginxModule

以1Group -> 2Storage组成数据冗余备份集群

## 概念说明
Tracker = 跟踪， 主要做调度工作
Storage = 存储，提供容量和备份服务，以group为单位


## 使用的系统软件
| 名称         | 说明           |
|:--|-|
| centos|7.x|
|libfatscommon|FastDFS分离出的一些公用函数包|
|FastDFS|FastDFS本体|
|fastdfs-nginx-module|FastDFS和nginx的关联模块|
|nginx|nginx1.15.4|

## 安装 

#### 编译环境
```shell
yum install git gcc gcc-c++ make automake autoconf libtool pcre pcre-devel zlib zlib-devel openssl-devel wget vim -y
```

#### 用户权限
````shell
groupadd -r fastdfs && useradd -r -g fastdfs fastdfs 

passwd fastdfs
# 设置密码， 这里以f1a2s3t4d4f5s6

mkdir /data/fastdfs

chown -R fastdfs:fastdfs /data/fastdfs

visudo
##找到root ALL=(ALL) ALL行,在下面添加一行
fastdfs ALL=(ALL) ALL

su fastdfs
````

#### 安装
path: `/data/fastdfs`

```sh
mkdir libfastcommon
tar vxf libfastcommon-1.0.41.tar.gz -C ./libfastcommon --strip-components 1
cd libfastcommon
./make.sh
sudo ./make.sh install

# 输入密码 f1a2s3t4d4f5s6
# 安装成功
# libfastcommon 默认安装到了/usr/lib64/libfastcommon.so和/usr/lib64/libfdfsclient.so

mkdir fastdfs
tar vxf fastdfs-6.02.tar.gz -C ./fastdfs --strip-components 1
cd fastdfs
./make.sh
sudo ./make.sh install
# 输入密码 f1a2s3t4d4f5s6
# 安装成功

#切换到Root
exit
cd /etc/fdfs

#tracker配置
cp tracker.conf.sample tracker.conf

vi /etc/fdfs/tracker.conf
port=22122  # tracker服务器端口（默认22122,一般不修改）
# 存储日志和数据的根目录
# 注意，这里的目录需要配置用户权限
# chown -R fastdfs:fastdfs /home/dfs
base_path=/home/dfs  

#storage配置
cp storage.conf.sample storage.conf
vi /etc/fdfs/storage.conf

base_path=/home/dfs
# storage服务端口（默认23000,一般不修改）  
port=23000  
# 数据和日志文件存储根目录
base_path=/home/dfs  
# 第一个存储目录
store_path0=/home/dfs  
tracker_server=192.168.1.195:22122
tracker_server=192.168.1.196:22122
# http访问文件的端口(默认8888,看情况修改,和nginx中保持一致)
http.server_port=18888
```

## 启动
#### 防火墙
```sh
#不关闭防火墙的话无法使用
systemctl stop firewalld.service #关闭
systemctl restart firewalld.service #重启
```
#### tracker
```sh
su fastdfs
sudo /etc/init.d/fdfs_trackerd start #启动tracker服务
sudo /etc/init.d/fdfs_trackerd restart #重启动tracker服务
sudo /etc/init.d/fdfs_trackerd stop #停止tracker服务
sudo chkconfig fdfs_trackerd on #自启动tracker服务

# 输入密码 f1a2s3t4d4f5s6
```

#### storage
```sh
su fastdfs
sudo /etc/init.d/fdfs_storaged start #启动storage服务
sudo /etc/init.d/fdfs_storaged restart #重动storage服务
sudo /etc/init.d/fdfs_storaged stop #停止动storage服务
sudo chkconfig fdfs_storaged on #自启动storage服务
# 输入密码 f1a2s3t4d4f5s6
```
#### 检测集群
```sh
sudo /usr/bin/fdfs_monitor /etc/fdfs/storage.conf
# 会显示会有几台服务器 有3台就会 显示 Storage 1-Storage 3的详细信息
```
#### 测试
###### client.conf
```sh
base_path=/home/dfs
tracker_server=192.168.1.195:22122
tracker_server=192.168.1.196:22122
```
```sh
sudo fdfs_upload_file /etc/fdfs/client.conf ./006aUUh4zy6XWjUcATEca\&690.jpg
# 输入密码 f1a2s3t4d4f5s6 
# 保存后测试,返回ID表示成功 如：group1/M00/00/00/xx.tar.gz
```

#### 与Tengine整合
```sh
# root
cp /data/tengine/fastdfs-nginx-module-1.21/src/mod_fastdfs.conf /etc/fdfs
vi /etc/fdfs/mod_fastdfs.conf
#需要修改的内容如下
tracker_server=192.168.1.195:22122  #tracker服务器IP和端口
tracker_server=192.168.1.196:22122  #tracker服务器IP和端口
url_have_group_name=true
store_path0=/home/dfs

cp http.conf mime.types /etc/fdfs/

## 配置tengine
#添加如下配置
server {
    ## 该端口为storage.conf中的http.server_port相同
    listen       18888;
    server_name  localhost;
    #location ~/group[0-9]/ {
    location ~/group1/ {
        ngx_fastdfs_module;
    }
    error_page   500 502 503 504  /50x.html;
    location = /50x.html {
    root   html;
    }
}

## unknown directive "ngx_fastdfs_module" 
## 安装模块 模块目录：/data/tengine/fastdfs-nginx-module-1.21
cd /data/tengine/tengine-2.3.2/fastdfs-nginx-module-1.21
cp ./src/* ./
cd /data/tengine/tengine-2.3.2
./configure --prefix=/usr/local/tengine \
--user=nginx \
--group=nginx \
--with-threads \
--with-http_ssl_module \
--with-http_sub_module \
--with-http_stub_status_module \
--with-http_gzip_static_module \
--with-http_realip_module \
--with-http_addition_module \
--with-http_v2_module \
--with-pcre \
--with-jemalloc \
--add-module=../ngx_cache_purge-2.3 \
--add-module=./modules/ngx_http_upstream_check_module \
--add-module=./modules/ngx_http_upstream_session_sticky_module \
--add-module=./modules/ngx_http_upstream_dynamic_module \
--add-module=../ngx_http_dyups_module-master \
--add-module=../fastdfs-nginx-module-1.21

make

# 需要先停止nginx
cp ./objs/nginx /usr/local/tengine/sbin/

chown -R nginx:nginx /usr/local/tengine/
```

## Nginx启动异常
> ERROR - file: ini_file_reader.c, line: 631, include file "http.conf" not exists, line: "#include http.conf"
> 
> ERROR - file: /home/packages/fastdfs-nginx-module-master/src/common.c, line: 163, load conf file "/etc/fdfs/mod_fastdfs.conf" fail, ret code: 2

解决：# cp /home/fastdfs-5.11/conf/http.conf /etc/fdfs/

> ERROR - file: shared_func.c, line: 968, file /etc/fdfs/mime.types not exist

解决：# cp /home/fastdfs-5.11/conf/mime.types /etc/fdfs/

## 配置优化
##Storage
```
max_connections=1024
accept_threads=8 #该参数决定接收客户端连接的线程数,默认值为1,适当放大该参数可改善Storage处理连接的能力,[线上环境cpu为多核心可支持足够多的线程数]
work_threads=25 #工作线程用来处理网络IO,默认值为4,该参数影响Stroage可以同时处理的连接数
disk_reader_threads=5 #读取磁盘数据的线程数,对应到每个存储路径,线上环境Storage只有一个路径,默认为1
disk_writer_threads=5 #写磁盘的线程数量,也是对应一个存储路径,默认为1
```

##Client
```
fdfs:
  so-timeout: 3501
  connect-timeout: 3501
  tracker-list:
    - 192.168.1.198:22122
  pool:
    max-total: 200
    max-total-per-key: 50
    max-wait-millis: 50000
```