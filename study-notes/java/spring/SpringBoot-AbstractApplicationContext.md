# 手撕SpringBoot-AbstractApplicationContext
通用的应用上下文`ApplicationContext`抽像容器。

## <a name="refresh">refresh</a>应用上下文刷新
- （模板方法）上下文刷新准备<a href="#prepareRefresh">prepareRefresh</a>
- （模板方法）`BeanFactory`刷新
- `BeanFactory`准备
    - 设置`ClassLoader`
    - 设置`EL`表达式解析器`StandardBeanExpressionResolver`
    - 设置`Properties`编辑器`ResourceEditorRegistrar`
    - 设置`Bean`的处理器`BeanPostProcessor:ApplicationContextAwareProcessor`
    ```java
    //需要配置忽略依赖接口
    // Configure the bean factory with context callbacks.
	beanFactory.addBeanPostProcessor(new ApplicationContextAwareProcessor(this));
	beanFactory.ignoreDependencyInterface(EnvironmentAware.class);
	beanFactory.ignoreDependencyInterface(EmbeddedValueResolverAware.class);
	beanFactory.ignoreDependencyInterface(ResourceLoaderAware.class);
	beanFactory.ignoreDependencyInterface(ApplicationEventPublisherAware.class);
	beanFactory.ignoreDependencyInterface(MessageSourceAware.class);
	beanFactory.ignoreDependencyInterface(ApplicationContextAware.class);
    //ApplicationContextAwareProcessor会在加载Aware接口的时候, 传递参数到相应的Aware实现类中
    //所以需要忽略Aware接口的自动装配
    ```
    - 配置自动装配规则
    - 配置`BeanPostProcessor:ApplicationListenerDetector`
    - 设置`BeanPostProcessor:LoadTimeWeaverAwareProcessor`
    - 设置默认环境`environment Bean Singleton`
- `BeanFactory:ConfigurableListableBeanFactory` 配置后置处理
- 实例化并调用`BeanFactory:ConfigurableListableBeanFactory`后置处理
- 设置`Bean`后置处理
- 国际化处理`initMessageSource`
- 事件广播处理`initApplicationEventMulticaster`
- 模板方法，子类刷新上下文`onRefresh`
- 注册监听器`registerListeners`
- 完成`BeanFactory`实始化，实例化所有非延迟加载的`Bean`
- 所有工作完成，执行刷新动作并发布事件
---

#### <a name="prepareRefresh">prepareRefresh</a>
- 设置`容器状态`
    - `closed=false` 标记未结束
    - `active=true` 标记活动中
- 实始化配置资源点位符（模板方法）
- 环境上下文`必需配置`校验，[祥见StandardEnvironment#validateRequiredProperties](SpringBoot-StandardEnvironment.md#validateRequiredProperties)
