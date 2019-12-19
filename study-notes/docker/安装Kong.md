# Kong网关
> https://github.com/docker-library/docs/tree/master/kong/

## Postgres
```sh
docker run -d --name kong-database \
                -p 5432:5432 \
                -e "POSTGRES_USER=kong" \
                -e "POSTGRES_DB=kong" \
                postgres:9.6
```
###### 准备数据库
```
docker run --rm \
   --link kong-database:kong-database \
   -e "KONG_DATABASE=postgres" \
   -e "KONG_PG_HOST=kong-database" \
   -e "KONG_CASSANDRA_CONTACT_POINTS=kong-database" \
   kong kong migrations bootstrap
```

## 启动Kong
```sh
docker run -d --name kong \
    --link kong-database:kong-database \
    -e "KONG_DATABASE=postgres" \
    -e "KONG_PG_HOST=kong-database" \
    -e "KONG_CASSANDRA_CONTACT_POINTS=kong-database" \
    -e "KONG_PROXY_ACCESS_LOG=/dev/stdout" \
    -e "KONG_ADMIN_ACCESS_LOG=/dev/stdout" \
    -e "KONG_PROXY_ERROR_LOG=/dev/stderr" \
    -e "KONG_ADMIN_ERROR_LOG=/dev/stderr" \
    -e "KONG_ADMIN_LISTEN=0.0.0.0:8001, 0.0.0.0:8444 ssl" \
    -p 8000:8000 \
    -p 8443:8443 \
    -p 8001:8001 \
    -p 8444:8444 \
    kong
```
### 查看当前运行
`docker ps`
