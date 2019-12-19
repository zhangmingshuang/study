# SpringCloudAlibabaSentinel

## 启动
#### 自动配置
```
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
com.alibaba.cloud.sentinel.SentinelWebAutoConfiguration,\
com.alibaba.cloud.sentinel.SentinelWebFluxAutoConfiguration,\
com.alibaba.cloud.sentinel.endpoint.SentinelEndpointAutoConfiguration,\
com.alibaba.cloud.sentinel.custom.SentinelAutoConfiguration,\
com.alibaba.cloud.sentinel.feign.SentinelFeignAutoConfiguration
```
##### SentinelWebAutoConfiguration
`作用`： 如果没有使用自定义的`CommonFilter`时，注册默认的`CommonFilter`

- 加载配置`SentinelProperties`
- 注册过滤器 `sentinelFilter:FilterRegistrationBean`
    > FilterRegistrationBean用来实现web.xml的自定义过滤器
    > ```xml
    <filter>
    	<filter-name>WebAccessAuthorizeFilterMvc</filter-name>
    	<filter-class>com.cmb.bip.filter.ManageAccessFilter</filter-class>
    	<init-param>
    		<param-name>EXCEPTION_URI</param-name>
    		<param-value>login.html,*.js</param-value>
    	</init-param>
    	<init-param>
    		<param-name>ERR_URL</param-name>
    		<param-value>login.html</param-value>
    	</init-param>
    </filter>
    <filter-mapping>
    	<filter-name>WebAccessAuthorizeFilterMvc</filter-name>
    	<url-pattern>/*</url-pattern>
    </filter-mapping>
    ```


#### 断路器
```
org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker=\
com.alibaba.cloud.sentinel.custom.SentinelCircuitBreakerConfiguration
```
