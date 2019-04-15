# JAVA文件路径加载器
- [ResourcePatternResolver](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/core/io/support/ResourcePatternResolver.html)

All Superinterfaces:

 ResourceLoader

All Know Subinterfaces:

 ApplicationContext, ConfigurableApplicationContext, ConfigurableWebApplicationContext, WebApplicationContext

All Known Implementing Classes:
    AbstractApplicationContext, AbstractRefreshableApplicationContext, AbstractRefreshableConfigApplicationContext, AbstractRefreshableWebApplicationContext, AbstractXmlApplicationContext, AnnotationConfigApplicationContext, AnnotationConfigWebApplicationContext, ClassPathXmlApplicationContext, FileSystemXmlApplicationContext, GenericApplicationContext, GenericGroovyApplicationContext, GenericWebApplicationContext, GenericXmlApplicationContext, GroovyWebApplicationContext, PathMatchingResourcePatternResolver, ResourceAdapterApplicationContext, ServletContextResourcePatternResolver, StaticApplicationContext, StaticWebApplicationContext, XmlWebApplicationContext


接口： 定义实现解析本地地址下的资源文件


```java
ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(
                ValidateHandlerMethodResolver.class.getClassLoader());

try {
    Resource[] resources = resolver.getResources(
            ClassUtils.convertClassNameToResourcePath(ValidateProcessor.GENERATE_PACKET)
                    + "/*.class");
    for (Resource resource : resources) {
        String className = StringUtils
                .stripFilenameExtension(resource.getFilename());
        ValidateBeanMap.register(className);
    }
} catch (Exception e) {
    MagnetonLogger.error(e.getMessage(), e);
}
```
