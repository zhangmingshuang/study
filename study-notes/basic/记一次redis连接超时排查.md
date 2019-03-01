# 记一次redis连接超时排查

- 连接数查看 `netstat -an | grep 6379 | grep EST | wc -l`
```sh
127.0.0.1:6379> info clients
# Clients
connected_clients:7347
client_longest_output_list:0
client_biggest_input_buf:0
blocked_clients:0
```

  当服务端连接数达到最大，可以通过命令`client list`，列出连接数，查看age=130222 idle=130222，这两值表示连接存活的时间和已经空闲的时间。
  假如有IP的连接数过大，有可能是客户端连接使用不规范导致连接泄露。
- 最大连接数： `config get maxclients`
- 延迟测试： `redis-cli --latency-history`
