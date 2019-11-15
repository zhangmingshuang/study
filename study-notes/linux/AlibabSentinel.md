# Sentinel

## 依赖脚本
`cmd.sh`
`sentinel.sh`

## 目录
`/data/sentinel`

## dashboard

```sh
groupadd -r sentinel && useradd -r -g sentinel sentinel

chown -R sentinel:sentinel /data/sentinel  

mkdir /home/sentinel

chown -R sentinel:sentinel /home/sentinel

su sentinel

cd /data/sentinel

./sentinel.sh
```

## 脚本用法
```
sentinel.sh start
sentinel.sh stop
sentinel.sh restart
```


## 访问
`http://192.168.1.194:18091/#/login`

