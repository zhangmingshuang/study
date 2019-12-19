# SpringBoot配置加载顺序

> 配置文件支持yml与properties
>
> https://docs.spring.io/spring-boot/docs/2.2.3.BUILD-SNAPSHOT/reference/html/spring-boot-features.html#boot-features-external-config-application-property-files

[测试Demo](https://github.com/zhangmingshuang/testing/tree/master/spring-boot-config-load)

### SpringBoot中的配置加载顺序与其`属性`优先级是`相反`的，每多时候，我们说的只是加载顺序，而不是`属性`优先级。

## 默认情况下不同位置的加载优先级

#### 属性优先级
1. A /config subdirectory of the current directory.     
位于与jar包同级目录下的config文件夹，也就是当前目录下的/config子文件夹下配置
2. The current directory  
位于与jar包同级目录下（当前目录）
3. A classpath /config package
classpath下的config目录，如idea环境下，resource文件夹下的config文件夹，编译之后就是classpath下的config文件夹下配置
4. The classpath root
classpath根目录，如idea环境下，resource文件夹下配置

The list is ordered by precedence (properties defined in locations higher in the list override those defined in lower locations).

#### 加载优先级
加载优先级是与属性优先级相反的，所以加载顺序为
1. classpath:/ > 4. The classpath root
2. classpath:/config/ > 3. A classpath /config package
3. file:./ > 2. The current directory  
4. file:./config/ > 1. A /config subdirectory of the current directory.     


## application与bootstrap

bootstrap`优先`于application加载， 用于应用程序上下文的引导阶段。由`spring cloud BootstrapApplicationListener`在`ApplicationEnvironmentPreparedEvent`时进行加载。

可以将`bootstrap`配置理解为系统级别的参数配置，这些参数一般不会变更。

`application`配置可以理解为定义应用级别的参数，可以覆盖替换`bootstrap`的配置信息。


## properties与yml
yml`优先`于propertes加载
