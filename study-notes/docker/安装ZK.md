# 安装ZooKeeper

## 拉取
`docker pull zookeeper`


## 查看镜像
`docker image ls`

## 运行
```
docker run -d \
-p 2181:2181 \
-v /data/docker/zookeeper/:/data/ \
--restart always zookeeper
```


## 查看日志
`docker logs -f dzookeeper`


## 进入容器
`docker exec -it {containerId} /bin/bash`

### 单机启动
```
cd bin/
./zkCli.sh
```
