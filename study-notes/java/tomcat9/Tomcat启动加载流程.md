- Bootstrap
```
->Bootstrap#static
-->获取设置catalina.home
-->设置catalina.base

->Bootstrap#main
-->bootstrap#init
---->initClassLoaders初始化catalinaLoader catalina.properties [类加载器说明](./ClassLoader.md)
---->加载并创建Catalina.
Class<?> startupClass = catalinaLoader.loadClass("org.apache.catalina.startup.Catalina");
Object startupInstance = startupClass.getConstructor().newInstance();
---->反射调用`Catalina#setParentClassLoader`设置父加载器
-->bootstrap#stop/start
---->反射调用`Catalina#star/stop`
```

- Catalina
```
->Catalina#setParentClassLoader 设置父加载器
->Catalina#start
-->getServer
---->load
------> initDirs
------> initNaming
------> 获取configFile.  conf/server.xml
------> createStartDigester 解析xml
--------> addObjectCreate. Server. org.apache.catalina.core.StandardServer
--------> addSetNext  addService org.apache.catalina.Service
------> getServer().init() 启动
```

- StandardServer
```
->StandardServer#
->new Mapper
->new MapperListener
globalNamingResources = new NamingResourcesImpl()
-->LifecycleBase#init
-->StandardServer#initInternal
---->LifecycleMBeanBase#initInternal
---->register...
---->new MBeanFactory
------>new StandardService
------>StandardServer#addService
---->globalNamingResources#init;
------>NamingResourcesImpl#initInternal
---->将jar加载进入ExtensionValidator
---->services[i]#init
```
