# Tengine
> 192.168.1.196

## 环境依赖
`yum install gcc openssl-devel gd-devel pcre pcre-devel zlib zlib-devel libxml2-devel libxslt-devel GeoIP-devel -y`

如果无法线上安装  
必须依赖包zlib, pcre在安装时指定目录即可

## 模块依赖
- openssl-1.0.1h.tar.gz
- zlib-1.2.11.tar.gz
- fastdfs-nginx-module-1.21.tar.gz
- ngx_cache_purge-2.3.tar.gz [用于清除指定url的缓存](https://www.cnblogs.com/Eivll0m/p/4921829.html)
- http_stub_status_module [监控运行状态](https://www.cnblogs.com/94cool/p/3872492.html)
- ngx_http_upstream_check_module [健康检测模块](https://www.cnblogs.com/cheyunhua/p/8044904.html)
- ngx_http_upstream_dynamic_module [动态域名解析](http://tengine.taobao.org/document/http_upstream_dynamic.html)
  
```sh
groupadd -r nginx && useradd -r -g nginx nginx  

tar -zvxf tengine-2.3.2.tar.gz
tar -vxf fastdfs-nginx-module-1.21.tar.gz
tar -vxf ngx_cache_purge-2.3.tar.gz
cd tengine-2.3.2

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
--add-module=../fastdfs-nginx-module-1.21/src

make 

make install

chown -R nginx:nginx /usr/local/tengine/
```

## 启动
```sh
# 普通用户启动,端口不支持1024以下. Linux1024以下端口必须root才可以
./nginx
# 查看 
ps -ef | grep nginx
# nginx    29542     1  0 15:45 ?        00:00:00 nginx: master process ./nginx
# nginx    29543 29542  0 15:45 ?        00:00:00 nginx: worker process
```

## 配置upstream
```sh
upstream backend {
  dynamic_resolve fallback=stale fail_timeout=30s;
  server 127.0.0.1:16881;
  server 127.0.0.1:16882;
}

server {
    listen 16881;
    server_name localhost;
    location / {
      root /data/;
      index 16881.html;
    }
}
server {
    listen 16882;
    server_name localhost;
    location / {
      root /data/;
      index 16882.html;
    }
}
server {
    listen       16880;
    server_name  localhost;

    location / {
         proxy_pass http://backend;
    }
}
```
    
## 动态管理
- 安装模块
```sh
unzip  ngx_http_dyups_module-master.zip
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
--add-module=../ngx_http_dyups_module-master

make

# 需要先停止nginx
cp ./objs/nginx /usr/local/tengine/sbin/

chown -R nginx:nginx /usr/local/tengine/
```
#### 动态配置
```sh
# 配置访问域名，这里以IP访问，所以配置为192.168.1.196
upstream 192.168.1.196 {
  dynamic_resolve fallback=stale fail_timeout=30s;
  server 127.0.0.1:16881;
  server 127.0.0.1:16882;
}
server {
    listen       16880;
    server_name  localhost;

    location / {
         proxy_pass http://$host;
    }
}
```
#### 动态管理配置
```sh
server {
    # dyups 动态管理， 线上应该配置为127.0.0.1OIP允许其他不允许操作
    # 该配置的变更只能是内存变更，如果需要刷盘变更，需要手动
    listen 16883;
    location / {
        dyups_interface;
    }
}
```
#### 使用(restful interface)
GET
> /detail get all upstreams and their servers  
> /list get the list of upstreams  
> /upstream/name find the upstream by it's name

POST  
> /upstream/name update one upstream  
>   body commands;  
>   body server ip:port;

DELETE  
> /upstream/name delete one upstream  

```sh
# 运维使用示例
curl 127.0.0.1:16883/detail
> 192.168.1.196
> server 127.0.0.1:16881 weight=1 max_conns=0 max_fails=1 fail_timeout=10 backup=0 down=0
> server 127.0.0.1:16882 weight=1 max_conns=0 max_fails=1 fail_timeout=10 backup=0 down=0

# 下线127.0.0.1:16882
curl -d "server 127.0.0.1:16881;" 127.0.0.1:16883/upstream/192.168.1.196
curl 127.0.0.1:16883/upstream/192.168.1.196
> server 127.0.0.1:16881

# 上线127.0.0.1:16882
curl -d "server 127.0.0.1:16881;server 127.0.0.1:16882;" 127.0.0.1:16883/upstream/192.168.1.196
curl 127.0.0.1:16883/upstream/backend

```
  

## 备注
- 添加新模块方法
  在`configure`中添加新模块，如`--add-module=./ngx_http_dyups_module-master`
   
  `mark`(PS：不要`install`，否则会覆盖原有配置)
  
  `cp ./objs/nginx`覆盖原安装目录