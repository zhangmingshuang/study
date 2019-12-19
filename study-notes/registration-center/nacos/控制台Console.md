# 控制台 Console

## 启动
#### Nacos.java
`@SpringBootApplication(scanBasePackages = "com.alibaba.nacos")`
加载所有`com.alibab.nacos`包下的`Bean`

### 监听器
在`core`模块中，启动时会根据`spring.fatories`加载监听器用来进行环境初始化。

> SpringApplicationRunListener与ApplicationListener区别
> ApplicationListener 应用程序事件监听器，用来监听Spring事件
> SpringApplicationRunListener提供用于SpringApplication#run启动初始化过程的参与入口，将事件广播到ApplicationListener中

### LoggingSpringApplicationRunListener
`SpringApplicationRunListener`监听器，判断是否有环境配置，如果没有则输入`info`。
```java
@Override
   public void environmentPrepared(ConfigurableEnvironment environment) {
   if (!environment.containsProperty(CONFIG_PROPERTY)) {
       System.setProperty(CONFIG_PROPERTY, DEFAULT_NACOS_LOGBACK_LOCATION);
       if (logger.isInfoEnabled()) {
           logger.info("There is no property named \"{}\" in Spring Boot Environment, " +
                   "and whose value is {} will be set into System's Properties", CONFIG_PROPERTY,
               DEFAULT_NACOS_LOGBACK_LOCATION);
       }
   }
}
```
#### StartingSpringApplicationRunListener
##### environmentPrepared 环境准备完成
`SpringApplicationRunListener`监听器，在环境准备时配置`nacos`的系统配置`nacos.mode`, `nacos.function.mode`,`nacos.local.ip`

##### contextPrepared 上下文准备完成
- 判断`nacos`的模式，如果是集群模式，则加载集群信息并打印。
    ```java
    if (!STANDALONE_MODE) {
        ...
        //SystemUtils#readClusterConf 使用的静态引入
        List<String> clusterConf = readClusterConf();
        LOGGER.info("The server IP list of Nacos is {}", clusterConf);
        ....
    }
    ```
- 判断`nacos`的模式，如果是集群模式，在启动中输出`Nacos is starting...`。

##### started 启动完成
- 标识启动完成`starting=false`
- 输出相关配置日志信息


#### StandaloneProfileApplicationListener
`ApplicationListener`监听器，监听`ApplicationEnvironmentPreparedEvent`事件

##### onApplicationEvent
判断`nacos`模式，如果是`standalone`模式，设置配置`active.profile`为`standalone`。

### 后置处理
#### NacosDefaultPropertySourceEnvironmentPostProcessor
`EnvironmentPostProcessor`后置处理器，对配置信息进行加载覆盖。

### 加载Config模块
祥见[config模块解析](./config.md)
### 加载Naming模块
祥见[Naming模块解析](./naming.md)
