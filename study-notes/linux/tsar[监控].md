# 阿里开源 - 监控 - [tsar](https://github.com/alibaba/tsar?spm=a2c4e.11158692.649917.102.179c79b2fol3zI)

 - 整合 Nagios 监控 [跳转到>> Nagios安装](../linux/nagios.xi.install.md)
 - 高级功能
  - 输出到nagios

  > 首先配置output_interface file,nagios，
  >
  > 增加nagios输出
  >
  > 然后配置nagios服务器和端口，以及发送的间隔时间
  >
  > ####The IP address or the host running the NSCA daemon
  >
  > server_addr nagios.server.com
  >
  > ####The port on which the daemon is running - default is 5667
  >
  > server_port 8086
  >
  > ####The cycle of send alert to nagios
  >
  > cycle_time 300
  >
  > 由于是nagios的被动监控模式，需要制定nsca的位置和配置文件位置
  >
  > ####nsca client program
  >
  > send_nsca_cmd /usr/bin/send_nsca
  >
  > send_nsca_conf /home/a/conf/amon/send_nsca.conf
  >
  > 这里需要 nagios安装 nsca模块支持
  > 接下来制定哪些模块和字段需要进行监控，一共四个阀值对应nagios中的不同报警级别
  >
  > ####tsar mod alert config file
  >
  > ####threshold [hostname.]servicename.key;w-min;w-max;c-min;cmax;
  >
  > threshold cpu.util;50;60;70;80;
  >

  [nagios安装 nsca模块支持](./nagios.xi.install.md)
  

  - 输出到mysql
  > 配置： 首先配置output_interface file,db，增加db输出
  >
  > 然后配置哪些模块数据需要输出
  >
  > output_db_mod mod_cpu,mod_mem,mod_traffic,mod_load,mod_tcp,mod_udpmod_io
  >
  > 然后配置sql语句发送的目的地址和端口
  >
  > output_db_addr console2:56677
  >
