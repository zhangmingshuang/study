# RedisBloom


## 安装
```sh
cd /data
mkdir redisbloom
tar -vxf ./env/RedisBloom-2.0.3.tar.gz -C ./redisbloom --strip-components 1
cd redisbloom
make
cd ..
chown -R redis:redis redisbloom
```

> `模块` = /data/redisbloom/redisbloom.so

## Redis使用

#### 方法1
`redis.conf`中配置`loadmodule /data/redisbloom/redisbloom.so`

#### 方法2
`redis-server --loadmodule /data/redisbloom/redisbloom.so`



