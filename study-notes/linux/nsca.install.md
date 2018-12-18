# [nsca 安装与使用](https://exchange.nagios.org/directory/Addons/Passive-Checks/NSCA--2D-Nagios-Service-Check-Acceptor/details)
 - 特别注意： 如果安装的是nagiosxi已经配置好了nsca模块，只需要修改密码即可

- 主Nagios中心服务部署（需要安装[完整的Nagios](./nagios.xi.install.md)）
 ```sh
    # wget http://prdownloads.sourceforge.net/sourceforge/nagios/nsca-2.9.2.tar.gz
    # tar zxvf nsca-2.9.2.tar.gz
    # cd nsca-2.9.2
    # ./configure
    # make all
 ```
 运行完成后会在src中生成几个文件，有可执行程序nsca、send_nsca，和一些配置文件、脚本。

 ```sh
 # cp src/nsca /usr/local/nagios/bin/
 # cp sample-config/nsca.cfg /usr/local/nagios/etc
 # chown nagios.nagios /usr/local/nagios/bin/nsca
 # chown nagios.nagios /usr/local/nagios/etc/nsca.cfg
 # cp init-script /etc/init.d/nsca
 # chmod a+x /etc/init.d/nsca
 # update-rc.d nsca defaults
```

修改nsca的配置文件

```sh
# vi /usr/local/nagios/etc/nsca.cfg
password=123456
```

修改nagios的配置文件

```sh
# vi /usr/local/nagios/etc/nagios.cfg
check_external_commands=1 # 配置nagios检查扩展命令
accept_passive_service_checks=1 # 配置接受被动服务检测的结果
accept_passive_host_checks=1 #配置接受被动主机检测的结果
```

检查没问题后重启nagios和启动nsca 。

- 客户端服务器安装(只需要安装 nagios-code 即可，不需要安装其他组件)
