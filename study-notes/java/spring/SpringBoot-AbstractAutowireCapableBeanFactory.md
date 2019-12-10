# 手撕SpringBoot-AbstractAutowireCapableBeanFactory
提供容器之外的Bean注入装配功能

## 示例
```java
@Configuration
public class Test {
    //Getter/Setter/ToString
    public static class DependBean {
        private String name = "DependBean";
    }
    //Getter/Setter/ToString
    public static class Independent {
        @Autowired
        public DependBean dependBean;
    }
    @Bean
    public DependBean dependBean() {
        return new DependBean();
    }
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Test.class);
        System.out.println(context.getBean(DependBean.class));
        //Test.DependBean(name=DependBean)
        AutowireCapableBeanFactory acbf = context.getAutowireCapableBeanFactory();
        Independent bean = acbf.createBean(Independent.class);
        System.out.println(bean);
        //Test.Independent(dependBean=Test.DependBean(name=DependBean))
    }
}
```
需要注意的，`Independent`对象是不能通过`context`获取的，因为该实现只是创建一个外部Bean，并通过`Context`自动注入依赖容器中的`Bean`，而不是将`Independent`交给`Bean`容器管理
```java
acbf.getBean(Independent.class);
```
会抛出异常`org.springframework.beans.factory.NoSuchBeanDefinitionException: No qualifying bean of type 'Test$Independent' available`

# 解析
## createBean
### 接口定义
创建一个给定的Class类的完整`Bean`。并且执行`Bean`的完整初始化，包括`BeanPostProcess`。

注意：该接口的目的是为了创建一个新的实例，并填充字段、方法和应用所有的标准`Bean`初始化回调。

### 代码解析
```java
public <T> T createBean(Class<T> beanClass) throws BeansException {
	// Use prototype bean definition, to avoid registering bean as dependent bean.
	RootBeanDefinition bd = new RootBeanDefinition(beanClass);
	bd.setScope(SCOPE_PROTOTYPE);//默认多例
	bd.allowCaching = ClassUtils.isCacheSafe(beanClass, getBeanClassLoader());
	return (T) createBean(beanClass.getName(), bd, null);
}

/**
 * Central method of this class: creates a bean instance,
 * populates the bean instance, applies post-processors, etc.
 */
@Override
protected Object createBean(String beanName, RootBeanDefinition mbd, @Nullable Object[] args) throws BeanCreationException {
        ...
		Object beanInstance = doCreateBean(beanName, mbdToUse, args);
		...
}

/**
 * Actually create the specified bean. Pre-creation processing has already happened
 * at this point, e.g. checking {@code postProcessBeforeInstantiation} callbacks.
 * <p>Differentiates between default bean instantiation, use of a
 * factory method, and autowiring a constructor.
 */
protected Object doCreateBean(final String beanName, final RootBeanDefinition mbd, final @Nullable Object[] args)
		throws BeanCreationException {

	...
    //创建BeanWrapper
	instanceWrapper = createBeanInstance(beanName, mbd, args);
    ...
	final Object bean = instanceWrapper.getWrappedInstance();
	Class<?> beanType = instanceWrapper.getWrappedClass();
	...

	// Allow post-processors to modify the merged bean definition.
	...
	applyMergedBeanDefinitionPostProcessors(mbd, beanType, beanName);
    ...

    ...
    //处理循环引用
    ...

	// Initialize the bean instance.
	Object exposedObject = bean;
	try {
        //Bean属性填充
		populateBean(beanName, mbd, instanceWrapper);
        //初始化，执行init/PostContruct..
		exposedObject = initializeBean(beanName, exposedObject, mbd);
	}
	...
}
```
`Bean`的创建的主要核心是`doCreateBean`([祥见Bean加载](./SpringBean加载.md))，分为：
- createBeanInstance 创建BeanWrapper
- populateBean 填充Bean属性
- initializeBean 执行初始化方法，如init、PostContruct
