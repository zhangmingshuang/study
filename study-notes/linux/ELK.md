# 分布式日志采集系统　ＥＬＫ安装
  - 参考 [https://blog.csdn.net/mengfch/article/details/80915172](https://blog.csdn.net/mengfch/article/details/80915172)


  - E elasticsearch
  - L logstash
  - K kibana


如果服务器资源比较紧张，可以使用`filebeat`来做日志的收集工作，然后汇总到`logstash`

> 这也正是官方所推荐的（The `Filebeat` client is a lightweight, resource-friendly tool that collects logs from files on the server and forwards these logs to your Logstash instance for processing. Filebeat is designed for reliability and low latency. Filebeat has a light resource footprint on the host machine, and the Beats input plugin minimizes the resource demands on the Logstash instance.）

  - ES的文件限制

```sh
vi /etc/security/limits.conf
# 添加下面内容
test - nofile 65536  # test为操作账号
vi /etc/sysctl.conf
# 添加下面内容
vm.max_map_count=262144
# 保存并生效
sysctl -p
```


  - 安装elasticsearch

    1. 到官网下载压缩包。本例使用的是6.2.2版本。elasticsearch-6.2.2.zip。

    2. 解压：unzip elasticsearch-6.2.2.zip -d /sinochem/software/elasticsearch-6.2.2

    3. 配置文件修改。配置文件在解压目录中的./config下的elasticsearch.yml文件。需要我们修改的项为：

    ```yml
      # elasticsearch.yml

      cluster.name: es-application # 集群名字

      node.name: node-1 # 当前节点名字

      path.data: /sinochem/software/elasticsearch-6.2.2/data # es数据存放路径

      path.logs: /sinochem/software/elasticsearch-6.2.2/logs # es日志路径

      network.host: 10.144.132.70 # 当前节点IP地址

      http.port: 9200 # 监听端口号
    ```
    4. 启动服务。nohup ./bin/elasticsearch >/dev/null &


  - 安装Kibana

    1. 到官网下载压缩包。本例使用的是6.2.2版本。kibana-6.2.2-linux-x86_64.tar.gz。
    2. 解压：tar -zxvf kibana-6.2.2-linux-x86_64.tar.gz
    3. 配置文件修改。配置文件在解压目录中的./config下的kibana.yml文件。需要我们修改的项为：

    ```yml
    # kibana.yml

    server.port: 5601 # 服务端口

    server.host: "10.144.132.70" # 服务IP地址

    kibana.index: ".kibana" # kibana在es中的索引
    ```
    4. 启动服务。nohup ./bin/kibana >/dev/null &


- 安装Logstash

    1. 到官网下载压缩包。本例使用的是6.2.2版本。logstash-6.2.2.tar.gz。
    2. 解压：tar -zxvf logstash-6.2.2.tar.gz
    3. 配置文件修改。配置文件在解压目录中的./config下新增日志配置文件logstash.conf。文件内容为：

    ```yml
    input {
        # beat插件，监听5044端口
        beats {
            port => "5044"
        }
    }
    filter {
        grok {
            match => [ "message", "%{COMBINEDAPACHELOG}" ]
        }
        date {
            match => [ "timestamp", "yyyy-MM-dd HH:mm:ss.SSS" ]
        }
    }
    output {
        # 日志输出到ES
        elasticsearch {
            hosts => ["10.144.132.70:9200"]
            index => "logstash-%{+YYYY.MM.dd}"
        }
    }
    ```
    4. 启动服务。nohup ./bin/logstash -f config/logstash.conf --config.reload.automatic >/dev/null &



- 安装Filebeat

    以上几个都是安装在ELK系统体系下的，Filebeat需要安装到各个服务所在机器上。Filebeat使用的是6.3.0版本，使用该版本是为了使用其中的processors。当然以上也可以使用6.3.X版本。

  1. 到官网下载压缩包。filebeat-6.3.0-linux-x86_64.tar.gz。

  2. 解压：tar -zxvf filebeat-6.3.0-linux-x86_64.tar.gz

  3. 配置文件修改。配置文件在解压目录中的./config下的filebeat.yml文件。简单配置如下：

  ```yml
  filebeat.inputs:
  # 用户自定义部分 start
  - type: log
    paths:
      # 日志文件
      - /sinochem/app/cooperation/monitor-gateway/logs/*-info.log
    exclude_lines: ['^DBG']
    fields:
      # 服务名称
      app_name: monitor-gateway
    fields_under_root: true

  - type: log
    paths:
      - /sinochem/app/cooperation/monitor-admin-service/logs/*-info.log
    exclude_lines: ['^DBG']
    fields:
      app_name: monitor-admin-service
    fields_under_root: true
  # 用户自定义部分 end

  output.logstash:
    hosts: ["10.144.132.70:5044"]

  fields:
    # 需要用户设置环境变量，或者直接将本机IP替换变量
    ip_address: "${IP_ADDRESS:UNKNOWN}"
  fields_under_root: true

  processors:
  - drop_fields:
    fields: ["host"]
  # - add_host_metadata: ~
  # - decode_json_fields:
  #      fields: ["host"]

  filebeat.shutdown_timeout: 5s
  ```

  4. 启动服务。nohup ./bin/filebeat -e -c config/filebeat.yml -d publish >/dev/null &
