# 安装nginx1.14
- Nginx官方提供的版本说明
    - Mainline Version. 开发版
    - Stable Version. 最新稳定版本
    - Legacy Version.　历史稳定版本

- 安装前准备
    - GCC.    gcc -v .  安装： yum install gcc -y
    - PCRE.  Http模块正则解析.  yum install pcre pcre-devel -y
    - zlib.  GZIP格式压缩.    yum install zlib zlib-devel -y
    - openssl. yum install openssl openssl-devel -y


- 安装
    ./configure --prefix=/usr/local/nginx --pid-path=/run/nginx.pid  --error-log-path=/var/log/nginx/error.log --http-log-path=/var/log/nginx/access.log  --with-http_ssl_module --with-http_v2_module --with-http_stub_status_module --with-pcre   
    #生成 Makefile，为下一步的编译做准备
    make             #编译
    make install     #安装
