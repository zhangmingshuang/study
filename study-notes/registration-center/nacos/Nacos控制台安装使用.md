# Nacos
> CentOS7

## 目录
/data/nacos

## 安装
```
cd /data/nacos
tar zvxf nacos-server-1.1.4.tar.gz --strip-components 1

groupadd -r nacos && useradd -r -g nacos nacos
chown -R nacos:nacos /data/nacos

su nacos
# 单机运行
./bin/startup.sh -m standalone -f config

# 修改版支持属性
localUserName=admin
localPassword=123456
```

## Nacos + SpringCloud配置说明
在`SpringCloud`版本整合中，需要以`bootstrap.properties`配置才可以生效

配置分为：共享分配与服务配置说明
```yaml
#服务器地址
spring.cloud.nacos.config.server-addr=192.168.1.195:18848
#默认为Public命名空间,可以省略不写
spring.cloud.nacos.config.namespace=f8ea53c2-5a39-4789-b2af-f0514ccd6c36
#指定配置群组 --如果是Public命名空间 则可以省略群组配置
spring.cloud.nacos.config.group=DEFAULT_GROUP
#文件名 -- 如果没有配置则默认为 ${spring.appliction.name}
spring.cloud.nacos.config.prefix=sgcc-wx
#指定文件后缀
spring.cloud.nacos.config.file-extension=yaml

spring.cloud.nacos.config.ext-config[0].group=public
spring.cloud.nacos.config.ext-config[0].data-id=public-config.yaml


# config explain.
# public config = ${spring.cloud.nacos.config.prefix}.yaml
# e.g sgcc-wx.yaml
#
# service config = public-config.yaml
#
# test:
# http://192.168.1.195:18848/nacos/v1/cs/configs?tenant=f8ea53c2-5a39-4789-b2af-f0514ccd6c36&dataId=public-config.yaml&group=public
#
# explain:
# http://ip/nacos/v1/cs/configs?tenant={namespace}
# &dataId=${spring.cloud.nacos.config.prefix}.${spring.cloud.nacos.config.file-extension} e.g public-config.yaml
# &group={group}
```
