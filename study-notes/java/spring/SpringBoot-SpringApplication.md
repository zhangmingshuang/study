# 手撕SpringBoot-SpringApplication

提供可以直接使用`main`方法直接启动一个`Spring`应用的入口。

它将自动创建一个`ApplicationContext`，注册一个`CommandLinePropertySource`解析如`Spring properties`类似的配置，自动刷新应用上下文，并加载对应的Bean对象。

## 使用示例
 ```java
 @Configuration
 @EnableAutoConfiguration
 public class MyApplication  {
     public static void main(String[] args) {
     SpringApplication.run(MyApplication.class, args);
    }
 }
 ```

并且，可以在启动时指定配置信息：
```java
 public static void main(String[] args) {
   SpringApplication application = new SpringApplication(MyApplication.class);
   // ... customize application settings here
   application.run(args)
 }
```

## Bean加载
`SpringApplication`可以从不同的资源中加载`Bean`，如：`@Configuration`。
同时，手动设置资源。

手动设置资源可以使用：
- 使用`AnnotatedBeanDefinitionReader`加载指定类
- 使用`XmlBeanDefinitionReader`加载`xml`配置类
- 使用`GroovyBeanDefinitionReader`加载`groovy script`
- 使用`ClassPathBeanDefinitionScanner`扫描指定包


## 源码解析(Note: 以SERVLET为解析)
> 参考： [启动过程](./SpringBoot启动主要过程.md)
### 启动代码
```java
@SpringBootApplication
public class MyApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class, args);
    }
}
```
### 过程解析
> new SpringApplication(primarySources).run(args)
#### new
`SpringApplication`在被创建时，会根据配置或者默认配置进去一些依赖的初始化。

主要依赖有：  
 - 资源加载器：`resourceLoader:ResourceLoader`
 - 主要资源容器：`primarySource:LinkedHashSet`。默认初始化时主要是`MyApplication`

主要初始化有：
 - 从`spring.factories`加载`ApplicationContextInitializer`
 - 从`spring.factories`加载`ApplicationListener`

#### run
- 从`spring.factories`加载运行时监听器`SpringApplicationRunListener`
- 创建默认启动参数`DefaultApplicationArguments:ApplicationArguments`
- <a href="#prepareEnvironment">创建上下文环境配置</a>`StandardServletEnvironment:StandardEnvironment:ConfigurableEnvironment`
- 根据`webApplicationType`创建`ConfigurableApplicationContext`。这里以`SEVLET`=`AnnotationConfigServletWebServerApplicationContext`解析。
- 从`spring.factories`中加载异常报告类`SpringBootExceptionReporter`
- 准备上下文<a href="#prepareContext">prepareContext</a>
- 刷新上下文<a href="refreshContext">refreshContext</a>
- 通知启动监听器`listeners:SpringApplicationRunListeners`
- 调用启动完成后处理器`ApplicationRunner`与`CommandLineRunner`

---

#### <a name="prepareContext">prepareContext</a>准备上下文
- `context`上下文准备，如设置环境`environment`
- `postProcessApplicationContext` 如果子类设置了应用上下文的自定义处理，在该方法中进行设置到应用上下文中，`beanNameGenerator`、`resourceLoader`、`addConversionService`。
- `applyInitializers` 初始化`ApplicationContextInitializer`
    默认初始化实例有：
    - `DelegatingApplicationContextInitializer`
    - `SharedMetadataReaderFactoryContextInitializer`
    - `ContextIdApplicationContextInitializer`
    - `ConfigurationWarningsApplicationContextInitializer`
    - `ServerPortInfoApplicationContextInitializer`
    - `ConditionEvaluationReportLoggingListener`
- 调用所有`listeners:SpringApplicationRunListener#contextPrepared`
- `load` 加载`Bean`到应用上下文中，[祥见Bean加载解析.md](SpringBoot-BeanDefinitionLoader.md)
- 调用所有`listeners:SpringApplicationRunListener#contextLoaded`

#### <a name="refreshContext">refreshContext</a>刷新上下文
- 刷新应用上下文`#refresh`
    - 调用父类[AbstractApplicationContext#refresh](./SpringBoot-AbstractApplicationContext.md#refresh)，父类调用该类的`#prepareRefresh`处理准备工作。
        - 清除缓存`scanner:ClassPathBeanDefinitionScanner#clearCache`
        - `AbstractApplicationContext#prepareRefresh`[祥见AbstractApplicationContext#refresh应用上下文](./SpringBoot-AbstractApplicationContext.md#prepareRefresh)
    - `BeanFactory准备`，父类调用该类的`#postProcessBeanFactory`处理准备工作
        - `super.postProcessBeanFactory(beanFactory)` > `ServletWebServerApplicationContext`
            - 配置`WebApplicationContextServletContextAwareProcessor`
        - `scanner:ClassPathBeanDefinitionScanner#scan`扫描指定包进行类加载
        - `reader:AnnotatedBeanDefinitionReader#register`注册`annotatedClasses`

- 注册`注销钩子`

#### <a name="prepareEnvironment">prepareEnvironment</a>初始化环境准备
###### getOrCreateEnvironment
根据`wepApplicationType`创建对应类，`SERVLET`= `StandardServletEnvironment`
###### configureEnvironment
这是一个模板方法，委托给<a href="#configurePropertySources">configurePropertySources</a>和<a href="#configureProfiles">configureProfiles</a>，所以，允许重写这两个方法实现控制环境配置。
```java
protected void configureEnvironment(ConfigurableEnvironment environment, String[] args) {
	...
	configurePropertySources(environment, args);
	configureProfiles(environment, args);
}
```
#### <a name="configurePropertySources">configurePropertySources</a>
添加、删除或者重排序任意`PropertySource`，并添加到`ConfigurableEnvironment`中的`sources:MutablePropertySources`。

#### <a name="configureProfiles">configureProfiles</a>
在`SpringBoot`中，支持`spring.profiles.active`规则，该方法可以用来指定`profile`。
```java
protected void configureProfiles(ConfigurableEnvironment environment, String[] args) {
    environment.getActiveProfiles(); // ensure they are initialized
    // But these ones should go first (last wins in a property key clash)
    Set<String> profiles = new LinkedHashSet<>(this.additionalProfiles);
    profiles.addAll(Arrays.asList(environment.getActiveProfiles()));
    environment.setActiveProfiles(StringUtils.toStringArray(profiles));
}
```
> `spring.profiles.active`即允许你通过命名约定按照一定的格式(application-{profile}.properties)来定义多个配置文件，然后通过在application.properyies通过spring.profiles.active来具体激活一个或者多个配置文件，如果没有没有指定任何profile的配置文件的话，spring boot默认会启动application-default.properties。
>
> 参考注解：`@Profile`
