- Tomcat9启动加载流程

```
-> org.apache.catalina.startup.Bootstrap#main -> new Bootstrap
--> Bootstrap#static
----> 获取设置catalina.home
----> 获取设置catalina.base
--> Bootstrap#init
----> Bootstrap#initClassLoaders 建立classloaders
------> Bootstrap#createClassLoader  加载catalina.properties中对应的xxx.loader创建ClassLoader
        commonLoader (common)-> System Loader
        sharedLoader (shared)-> commonLoader -> System Loader
        catalinaLoader(server) -> commonLoader -> System Loader
        (by default the commonLoader is used for the sharedLoader and the serverLoader)
----> Thread.contextClassloader -> catalinaLoader
----> 加载并创建org.apache.catalina.startup.Catalina
    Class<?> startupClass = catalinaLoader.loadClass("org.apache.catalina.startup.Catalina");
    Object startupInstance = startupClass.getConstructor().newInstance();
----> 反射调用`Catalina#setParentClassLoader`
        <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< Bootstart#init完成

--> Bootstrap#load
----> Catalina#load
------> Catalina#initDirs
------> Catalina#initNaming
        setProperty(javax.naming.Context.INITIAL_CONTEXT_FACTORY,
            org.apache.naming.java.javaURLContextFactory ->default)
------> Catalina#createStartDigester 根据server.xml元素配置一个digester
        org.apache.catalina.core.StandardServer
        org.apache.catalina.deploy.NamingResources  Stores naming resources in the J2EE JNDI tree
        org.apache.catalina.LifecycleListener 主要监听start/stop事件
        org.apache.catalina.core.StandardService 监听多个Connectors
        org.apache.catalina.Connector 监听请求
        NamingRuleSet
        EngineRuleSet
        HostRuleSet
        ContextRuleSet
------> 加载server.xml 并使用digester#parse解析
        digester会使用sax解析xml并自动创建定义的对象
--------> 加载并创建  org.apache.catalina.core.StandardServer -> Catalina#setServer
--------> org.apache.catalina.LifecycleListener -> Server#addLifecycleListener
            -> 主要：org.apache.catalina.mbeans.GlobalResourcesLifecycleListener
            ----> Registry registry = MBeanUtils.createRegistry()
            ------> Registry#loadDescriptors 加载各个packageName下的mbeans-descriptors.xml
            !!----> 接收到Lifecycle.START_EVENT时 -> createMBeans
            !!------> 加载 startup/mbeans-descriptors.xml中的 org.apache.catalina.startup.ContextConfig 上下文配置
            -> .....
--------> org.apache.catalina.core.StandardService
            -> StandardServer#addService
--------> org.apache.catalina.connector.Connector
            -> StandardServer/StandardService#addConnector
--------> org.apache.catalina.core.StandardEngine
            -> StandardServer/StandardService#setContainer
--------> org.apache.catalina.core.StandardHost
            -> StandardServer/StandardService/StandardEngine#addChild
                -> new StandardPipeline
                -> HostConfig
--------> org.apache.catalina.core.StandardContext
            -> StandardServer/StandardService/StandardEngine/StandardHost#addChild (重点解析)
                -> StandardHost/ContainerBase#addChild
                --> StandardHost/ContainerBase#addChildInternal
                ----> child#start
                ------> StandardContext/LifecycleBase#start
                --------> ...
                ----------> StandardContext#initInternal
                ----------> StandardContext#startInternal
                ------------> 创建WebappLoader
                     WebappLoader webappLoader = new WebappLoader(getParentClassLoader());
                ------------> StandardManager
                ------------> StandardWrapper

------> Catalina#initStreams Assigns System.out and System.err to the SystemLogHandler class
------> Catalina#getServer#init
--------> StandardServer/LifecycleBase#init
    <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< Catalina#load完成 << Bootstrap#load


-->bootstrap#stop/start
---->反射调用`Catalina#star/stop`
```

- 参考[Tomcat-ServerStartup][./serverStartup.txt]
