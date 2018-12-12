# CentOS 安装chrome浏览器
    - 下载Chrome浏览器的rpm包(https://www.chrome64bit.com/index.php/google-chrome-64-bit-for-linux)

```linux
[root@phy ~]# rpm -ivh google-chrome-stable_current_x86_64.rpm
error: Failed dependencies:
    libXss.so.1()(64bit) is needed by google-chrome-stable-70.0.3538.67-1.x86_64
    libappindicator3.so.1()(64bit) is needed by google-chrome-stable-70.0.3538.67-1.x86_64
    liberation-fonts is needed by google-chrome-stable-70.0.3538.67-1.x86_64
    libnss3.so(NSS_3.22)(64bit) is needed by google-chrome-stable-70.0.3538.67-1.x86_64
    libssl3.so(NSS_3.28)(64bit) is needed by google-chrome-stable-70.0.3538.67-1.x86_64
```

```linux
[root@phy ~]# yum install -y libappindicator-gtk3-12.10.0-13.el7
[root@phy~]# repoquery --nvr --whatprovides libnss3.so
Repository google-chrome is listed more than once in the configuration
nss-3.36.0-7.el7_5
nss-3.36.0-5.el7_5
nss-3.34.0-4.el7
[root@phy ~]# yum -y install nss-3.36.0-7.el7_5
[root@phy ~]# rpm -ivh google-chrome-stable_current_x86_64.rpm
error: Failed dependencies: libXss.so.1()(64bit) is needed by google-chrome-stable-70.0.3538.67-1.x86_64
liberation-fonts is needed by google-chrome-stable-70.0.3538.67-1.x86_64
[root@phy ~]# yum -y install libXScrnSaver
[root@phy ~]# rpm -ivh google-chrome-stable_current_x86_64.rpm
error: Failed dependencies: liberation-fonts is needed by google-chrome-stable-70.0.3538.67-1.x86_64
[root@phy ~]# yum -y install liberation-fonts
[root@phy ~]# rpm -ivh google-chrome-stable_current_x86_64.rpm
Preparing... ################################# [100%]

```
