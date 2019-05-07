# Tomcat9: Catalina解析

该类由`Bootstrap`的`#init`方法利用`catalinaLoader`加载，并使用反射方法进行方法的调用。

初始化加载时调用`#setParentClassLoader`将`sharedLoader`传入。

然后根据`Bootstrap`的启动参数如：`start`或者`stop`进行方法调用。

#初始化 构造函数
`protected ClassLoader parentClassLoader = Catalina.class.getClassLoader();`
`#setSecurityProtection` 根据 `catalina.properties`设置`package.access`和`package.definition`的安全配置

#start

```java
-->load 加载Server
---->initDirs 初始java.io.tmpdir
---->initNaming JNDI依赖相关
---->configFIle 加载conf/server.xml
---->createStartDigester 重点！
---->根据创建的Digester加载解析配置文件conf/server.xml
------> new StandardContext
------> new Connector
------> new StandardServer
------> new StandardService
------> new LifecycleListener
------> ....
---->initStreams
---->getServer()#init
------> StandardServer#init
-->getServer()#start StandardServer#start
```

  - [StandardServer相关](./StandardServer.md)
  - [StandardContext相关](./StandardContext.md)

> Digester 基于SAX+事件驱动+栈的方式实现的另类XML解析工具
> - SAX，用于解析xml
> - 事件驱动，在SAX解析的过程中加入事件来支持我们的对象映射
> - 栈，当解析xml元素的开始和结束的时候，需要通过xml元素映射的类对象的入栈和出栈来完成事件的调用

#createStartDigester
```java
protected Digester createStartDigester() {
    long t1=System.currentTimeMillis();
    // Initialize the digester
    Digester digester = new Digester();
    digester.setValidating(false);//是否需要验证合法性
    digester.setRulesValidation(true);//如果属性丢失是否警告
    Map<Class<?>, List<String>> fakeAttributes = new HashMap<>();//伪属性列表
    // Ignore className on all elements
    List<String> objectAttrs = new ArrayList<>();
    objectAttrs.add("className");
    fakeAttributes.put(Object.class, objectAttrs);//className属性
    // Ignore attribute added by Eclipse for its internal tracking
    List<String> contextAttrs = new ArrayList<>();
    contextAttrs.add("source");
    fakeAttributes.put(StandardContext.class, contextAttrs);//source属性
    // Ignore Connector attribute used internally but set on Server
    List<String> connectorAttrs = new ArrayList<>();
    connectorAttrs.add("portOffset");
    fakeAttributes.put(Connector.class, connectorAttrs);//portOffset属性
    //构建伪属性列表
    digester.setFakeAttributes(fakeAttributes);
    //是否使用当前线程中的ClassLoader来进行类加载
    //这里相关的就是在 Bootstrap#init中设置的Thread.currentThread().setContextClassLoader(catalinaLoader);
    digester.setUseContextClassLoader(true);

    // Configure the actions we will be using
    //Digester.addObjectCreate(String pattern, String className, String attributeName)
    //pattern, 匹配的节点
    //className, 该节点对应的默认实体类
    //attributeName, 如果该节点有className属性, 用className的值替换默认实体类
    //如果匹配到Server节点
    digester.addObjectCreate("Server",
                             "org.apache.catalina.core.StandardServer",
                             "className");
    //属性印射Key，如果柳丁 到Server时，就填充属性
    digester.addSetProperties("Server");
    //Digester.addSetNext(String pattern, String methodName, String paramType)
    //pattern, 匹配的节点
    //methodName, 调用父节点的方法
    //paramType, 父节点的方法接收的参数类型
    //如果匹配到Server节点，调用setServer方法
    digester.addSetNext("Server",
                        "setServer",
                        "org.apache.catalina.Server");
    //同上意义，区别在于Server/GlobalNamingResources是表示父节点GlobalNamingResources
    digester.addObjectCreate("Server/GlobalNamingResources",
                             "org.apache.catalina.deploy.NamingResourcesImpl");
    digester.addSetProperties("Server/GlobalNamingResources");
    digester.addSetNext("Server/GlobalNamingResources",
                        "setGlobalNamingResources",
                        "org.apache.catalina.deploy.NamingResourcesImpl");
    //添加一个自定义规则
    digester.addRule("Server/Listener",
            new ListenerCreateRule(null, "className"));
    digester.addSetProperties("Server/Listener");
    digester.addSetNext("Server/Listener",
                        "addLifecycleListener",
                        "org.apache.catalina.LifecycleListener");
    //非常重要，这里定义了Service的类
    digester.addObjectCreate("Server/Service",
                             "org.apache.catalina.core.StandardService",
                             "className");
    digester.addSetProperties("Server/Service");
    digester.addSetNext("Server/Service",
                        "addService",
                        "org.apache.catalina.Service");

    digester.addObjectCreate("Server/Service/Listener",
                             null, // MUST be specified in the element
                             "className");
    digester.addSetProperties("Server/Service/Listener");
    digester.addSetNext("Server/Service/Listener",
                        "addLifecycleListener",
                        "org.apache.catalina.LifecycleListener");

    //Executor
    digester.addObjectCreate("Server/Service/Executor",
                     "org.apache.catalina.core.StandardThreadExecutor",
                     "className");
    digester.addSetProperties("Server/Service/Executor");

    digester.addSetNext("Server/Service/Executor",
                        "addExecutor",
                        "org.apache.catalina.Executor");


    digester.addRule("Server/Service/Connector",
                     new ConnectorCreateRule());
    digester.addRule("Server/Service/Connector", new SetAllPropertiesRule(
            new String[]{"executor", "sslImplementationName", "protocol"}));
    digester.addSetNext("Server/Service/Connector",
                        "addConnector",
                        "org.apache.catalina.connector.Connector");

    digester.addRule("Server/Service/Connector", new AddPortOffsetRule());

    digester.addObjectCreate("Server/Service/Connector/SSLHostConfig",
                             "org.apache.tomcat.util.net.SSLHostConfig");
    digester.addSetProperties("Server/Service/Connector/SSLHostConfig");
    digester.addSetNext("Server/Service/Connector/SSLHostConfig",
            "addSslHostConfig",
            "org.apache.tomcat.util.net.SSLHostConfig");

    digester.addRule("Server/Service/Connector/SSLHostConfig/Certificate",
                     new CertificateCreateRule());
    digester.addRule("Server/Service/Connector/SSLHostConfig/Certificate",
                     new SetAllPropertiesRule(new String[]{"type"}));
    digester.addSetNext("Server/Service/Connector/SSLHostConfig/Certificate",
                        "addCertificate",
                        "org.apache.tomcat.util.net.SSLHostConfigCertificate");

    digester.addObjectCreate("Server/Service/Connector/SSLHostConfig/OpenSSLConf",
                             "org.apache.tomcat.util.net.openssl.OpenSSLConf");
    digester.addSetProperties("Server/Service/Connector/SSLHostConfig/OpenSSLConf");
    digester.addSetNext("Server/Service/Connector/SSLHostConfig/OpenSSLConf",
                        "setOpenSslConf",
                        "org.apache.tomcat.util.net.openssl.OpenSSLConf");

    digester.addObjectCreate("Server/Service/Connector/SSLHostConfig/OpenSSLConf/OpenSSLConfCmd",
                             "org.apache.tomcat.util.net.openssl.OpenSSLConfCmd");
    digester.addSetProperties("Server/Service/Connector/SSLHostConfig/OpenSSLConf/OpenSSLConfCmd");
    digester.addSetNext("Server/Service/Connector/SSLHostConfig/OpenSSLConf/OpenSSLConfCmd",
                        "addCmd",
                        "org.apache.tomcat.util.net.openssl.OpenSSLConfCmd");

    digester.addObjectCreate("Server/Service/Connector/Listener",
                             null, // MUST be specified in the element
                             "className");
    digester.addSetProperties("Server/Service/Connector/Listener");
    digester.addSetNext("Server/Service/Connector/Listener",
                        "addLifecycleListener",
                        "org.apache.catalina.LifecycleListener");

    digester.addObjectCreate("Server/Service/Connector/UpgradeProtocol",
                              null, // MUST be specified in the element
                              "className");
    digester.addSetProperties("Server/Service/Connector/UpgradeProtocol");
    digester.addSetNext("Server/Service/Connector/UpgradeProtocol",
                        "addUpgradeProtocol",
                        "org.apache.coyote.UpgradeProtocol");

    // Add RuleSets for nested elements
    digester.addRuleSet(new NamingRuleSet("Server/GlobalNamingResources/"));
    digester.addRuleSet(new EngineRuleSet("Server/Service/"));
    digester.addRuleSet(new HostRuleSet("Server/Service/Engine/"));
    digester.addRuleSet(new ContextRuleSet("Server/Service/Engine/Host/"));
    addClusterRuleSet(digester, "Server/Service/Engine/Host/Cluster/");
    digester.addRuleSet(new NamingRuleSet("Server/Service/Engine/Host/Context/"));

    // When the 'engine' is found, set the parentClassLoader.
    digester.addRule("Server/Service/Engine",
                     new SetParentClassLoaderRule(parentClassLoader));
    addClusterRuleSet(digester, "Server/Service/Engine/Cluster/");

    long t2=System.currentTimeMillis();
    if (log.isDebugEnabled()) {
        log.debug("Digester for server.xml created " + ( t2-t1 ));
    }
    return digester;

}
```
这里在于整个[容器架构](./Tomcat架构)的宏观理解，否则会比较难理解加载了这么多东西。

比较重要的定义在`source StandardContext`和`service StandardServer`。

在加载配置文件时`conf/server.xml`时会初始化`StandardContext`和`StandardServer`。
