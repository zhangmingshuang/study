# tomcat9: WebAppClassLoader

我们知道， WebAppClassLoaser 是各个Webapp私有的类加载器，加载路径中的class只对当前Webapp可见，那么他是如何初始化的呢？WebAppClassLoaser 的初始化时间和这3个类加载器初始化的时间不同，由于WebAppClassLoaser 和Context 紧紧关联，因此咋初始化
org.apache.catalina.core.StandardContext 会一起初始化 WebAppClassLoader， 该类中startInternal方法含有初始化类加载器的逻辑，核心源码如下：
    @Override
    protected synchronized void startInternal() throws LifecycleException {
         if (getLoader() == null) {
              WebappLoader webappLoader = new WebappLoader(getParentClassLoader());
              webappLoader.setDelegate(getDelegate());
              setLoader(webappLoader);
        }
        if ((loader != null) && (loader instanceof Lifecycle)) {
              ((Lifecycle) loader).start();
        }
    }

首先创建 WebAppClassLoader ， 然后 setLoader（webappLoader），再调用start方法，该方法是个模板方法，内部有 startInternal 方法用于子类去实现， 我们看WebAppClassLoader的startInternal 方法核心实现：
    @Override
    protected void startInternal() throws LifecycleException {
            classLoader = createClassLoader();
            classLoader.setResources(container.getResources());
            classLoader.setDelegate(this.delegate);
            classLoader.setSearchExternalFirst(searchExternalFirst);
            if (container instanceof StandardContext) {
                classLoader.setAntiJARLocking(
                        ((StandardContext) container).getAntiJARLocking());
                classLoader.setClearReferencesStatic(
                        ((StandardContext) container).getClearReferencesStatic());
                classLoader.setClearReferencesStopThreads(
                        ((StandardContext) container).getClearReferencesStopThreads());
                classLoader.setClearReferencesStopTimerThreads(
                        ((StandardContext) container).getClearReferencesStopTimerThreads());
                classLoader.setClearReferencesHttpClientKeepAliveThread(
                        ((StandardContext) container).getClearReferencesHttpClientKeepAliveThread());
            }

            for (int i = 0; i < repositories.length; i++) {
                classLoader.addRepository(repositories[i]);
            }

    }

13.  进入 createClassLoader 方法
首先classLoader = createClassLoader();创建类加载器，并且设置其资源路径为当前Webapp下某个context的类资源。最后我们看看createClassLoader的实现：

    /**
     * Create associated classLoader.
     */
    private WebappClassLoader createClassLoader()
        throws Exception {

        Class<?> clazz = Class.forName(loaderClass);
        WebappClassLoader classLoader = null;

        if (parentClassLoader == null) {
            parentClassLoader = container.getParentClassLoader();
        }
        Class<?>[] argTypes = { ClassLoader.class };
        Object[] args = { parentClassLoader };
        Constructor<?> constr = clazz.getConstructor(argTypes);
        classLoader = (WebappClassLoader) constr.newInstance(args);

        return classLoader;

    }

这里的loaderClass 是 字符串 org.apache.catalina.loader.WebappClassLoader, 首先通过反射实例化classLoader。现在我们知道了， WebappClassLoader 是在 StandardContext 初始化的时候实例化的，也证明了WebappClassLoader 和 Context 息息相关。
